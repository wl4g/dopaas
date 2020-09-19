/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.dts.codegen.engine.provider;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wl4g.components.common.annotation.Nullable;
import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.components.common.resource.StreamResource;
import com.wl4g.components.common.resource.resolver.ClassPathResourcePatternResolver;
import com.wl4g.components.core.utils.expression.SpelExpressions;
import com.wl4g.devops.dts.codegen.bean.GenProject;
import com.wl4g.devops.dts.codegen.bean.GenTable;
import com.wl4g.devops.dts.codegen.engine.context.GenerateContext;
import com.wl4g.devops.dts.codegen.engine.naming.CSharpSpecs;
import com.wl4g.devops.dts.codegen.engine.naming.GolangSpecs;
import com.wl4g.devops.dts.codegen.engine.naming.JavaSpecs;
import com.wl4g.devops.dts.codegen.engine.naming.PythonSpecs;
import freemarker.template.Template;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.commons.codec.net.URLCodec;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.components.common.collection.Collections2.ensureMap;
import static com.wl4g.components.common.io.ByteStreamUtils.readFullyToString;
import static com.wl4g.components.common.io.FileIOUtils.readFullyResourceString;
import static com.wl4g.components.common.io.FileIOUtils.writeFile;
import static com.wl4g.components.common.jvm.JvmRuntimeKit.isJVMDebugging;
import static com.wl4g.components.common.lang.Assert2.*;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.components.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.components.common.view.Freemarkers.renderingTemplateToString;
import static com.wl4g.components.core.utils.expression.SpelExpressions.create;
import static com.wl4g.devops.dts.codegen.utils.FreemarkerUtils.defaultGenConfigurer;
import static java.lang.String.format;
import static java.util.Objects.isNull;

/**
 * {@link AbstractGeneratorProvider}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
public abstract class AbstractGeneratorProvider implements GeneratorProvider {

	protected final SmartLogger log = getLogger(getClass());

	/**
	 * {@link GenerateContext}
	 */
	protected final GenerateContext context;

	public AbstractGeneratorProvider(GenerateContext context) {
		this.context = notNullOf(context, "context");
	}

	@Override
	public void run() {
		try {
			doHandleGenerate();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * DO generation project codes.
	 *
	 * @throws Exception
	 */
	protected abstract void doHandleGenerate() throws Exception;

	/**
	 * Processing generate codes.
	 * 
	 * @param provider
	 * @throws Exception
	 */
	protected void processGenerateWithTemplates(String provider) throws Exception {
		hasTextOf(provider, "provider");
		GenProject project = context.getGenProject();

		// Load templates.
		List<TemplateWrapper> tpls = loadTemplates(provider);

		// Handling generate
		doRenderingTemplates(tpls, project, context.getJobDir().getAbsolutePath());
	}

	/**
	 * Do rendering templates generate and save.
	 *
	 * @param tpls
	 * @param project
	 * @param writeBasePath
	 * @throws Exception
	 */
	protected void doRenderingTemplates(List<TemplateWrapper> tpls, GenProject project, String writeBasePath) throws Exception {
		for (TemplateWrapper tpl : tpls) {
			log.info("Rendering generate for tpl - {}", tpl.getTplPath());

			// Create rednering model.
			Map<String, Object> model = createRenderingModel(tpl.getTplPath(), project);

			if (tpl.isTpl()) {
				// foreach template by table
				if (tpl.isForeachTpl()) {
					for (GenTable tab : project.getGenTables()) {
						// Additidtion table model attributes
						model.putAll(toRenderingFlatModel(tab));

						// Call Pre rendering.
						String rendered = hasText(preRendering(tpl, model), "Pre rendering should return the rendered value");

						// Rendering with freemarker.
						String writePath = writeBasePath.concat("/").concat(resolveSpelExpression(tpl.getTplPath(), model));
						Template template = new Template(tpl.getFileName(), rendered, defaultGenConfigurer);
						rendered = renderingTemplateToString(template, model);

						// Call post rendered.
						postRendering(tpl, rendered, writePath);
					}
				}
				// Simple template.
				else {
					// Call Pre rendering.
					String rendered = hasText(preRendering(tpl, model), "Pre rendering should return the rendered value");

					// Rendering with freemarker.
					String writePath = writeBasePath.concat("/").concat(resolveSpelExpression(tpl.getTplPath(), model));
					Template template = new Template(tpl.getFileName(), rendered, defaultGenConfigurer);
					rendered = renderingTemplateToString(template, model);

					// Call post rendered.
					postRendering(tpl, rendered, writePath);
				}
			}
			// e.g: static resources files
			else {
				String targetPath = writeBasePath + "/" + resolveSpelExpression(tpl.getTplPath(), model);
				writeFile(new File(targetPath), tpl.getFileContent(), false);
			}
		}
	}

	/**
	 * Preparing rendering processing.
	 * 
	 * @param tpl
	 * @param model
	 * @return
	 */
	protected String preRendering(@NotNull TemplateWrapper tpl, @NotEmpty Map<String, Object> model) {
		notNullOf(tpl, "tpl");
		notEmptyOf(model, "model");

		// It is recommended to use FreeMarker to call Java methods.

		// // Rendering with spel ahead of time
		// //
		// // Note: After testing, we found that we should use spel to render
		// // first, otherwise FreeMarker will report an error.
		// //
		// return defaultExpressions.resolve(tpl.getFileContent(), model);
		return tpl.getFileContent();
	}

	/**
	 * Post rendered processing.
	 * 
	 * @param tpl
	 * @param rendered
	 * @param writePath
	 */
	protected void postRendering(@NotNull TemplateWrapper tpl, @NotBlank String rendered, @NotBlank String writePath) {
		notNullOf(tpl, "tpl");
		hasTextOf(rendered, "rendered");
		hasTextOf(writePath, "writePath");

		// Default by write to local disk
		writeFile(new File(writePath), rendered, false);
	}

	/**
	 * Gets object to flat map model
	 *
	 * @param object
	 * @return
	 * @throws Exception
	 */
	protected Map<String, Object> toRenderingFlatModel(Object object) throws Exception {
		return parseJSON(toJSONString(object), new TypeReference<HashMap<String, Object>>() {
		});
	}

	/**
	 * Customize rendering model
	 *
	 * @param tplPath
	 * @param project
	 * @param table
	 * @return
	 */
	protected Map<String, Object> customizeRenderingModel(@NotBlank String tplPath, @Nullable Object... beans) {
		return null;
	}

	/**
	 * Create rendering model
	 *
	 * @param tplPath
	 * @param project
	 * @param table
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> createRenderingModel(String tplPath, Object... beans) throws Exception {
		// Gets customize model.
		Map<String, Object> model = ensureMap(customizeRenderingModel(tplPath, beans));

		// Fill requires rendering parameters.
		if (!isNull(beans)) {
			for (Object bean : beans) {
				model.putAll(toRenderingFlatModel(bean));
			}
		}

		// Addidition special variables.
		model.put(VAR_WATERMARK, TPL_WATERMARK);

		// Addidition default utils. (Called when rendering templates for
		// Freemarker)
		model.put("javaSpecs", new JavaSpecs());
		model.put("csharpSpecs", new CSharpSpecs());
		model.put("golangSpecs", new GolangSpecs());
		model.put("pythonSpecs", new PythonSpecs());

		return model;
	}

	/**
	 * Resolving path SPEL expression
	 *
	 * @param expression
	 * @param model
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String resolveSpelExpression(String expression, final Map<String, Object> model) {
		if (expression.endsWith(TPL_SUFFIX)) {
			expression = expression.substring(0, expression.length() - TPL_SUFFIX.length());
		}
		final String expression0 = expression;
		log.debug("Resolving SPEL for expression: {}, model: {}", () -> expression0, () -> model);

		return defaultExpressions.resolve(expression0, model);
	}

	/**
	 * Load {@link Template} list by provider.
	 *
	 * @param provider
	 * @return
	 * @throws IOException
	 */
	private static final List<TemplateWrapper> loadTemplates(String provider) throws Exception {
		List<TemplateWrapper> tpls = templatesCache.get(provider);
		if (isJVMDebugging || isNull(tpls)) {
			synchronized (AbstractGeneratorProvider.class) {
				tpls = templatesCache.get(provider);
				if (isJVMDebugging || isNull(tpls)) {
					tpls = new ArrayList<>();

					// Scanning templates resources.
					String[] locations = getResourceLocations(provider).toArray(new String[0]);
					Set<StreamResource> resources = defaultResourceResolver.getResources(locations);
					STATICLOG.info("Loaded templates resources: {}", resources);

					for (StreamResource res : resources) {
						if (res.isReadable()) {
							tpls.add(wrapTemplate(res, provider));
						}
					}
					templatesCache.put(provider, tpls);
				}
			}
		}
		return tpls;
	}

	/**
	 * Wrapper {@link Template}
	 *
	 * @param res
	 * @param provider
	 * @return
	 * @throws Exception
	 */
	private static TemplateWrapper wrapTemplate(StreamResource res, String provider) throws Exception {
		/**
		 * e.g: res.getURI().toString()
		 * 
		 * <pre>
		 * jar:file:/opt/apps/acm/dts-manager-package/dts-manager-master-bin/
		 * libs/xcloud-devops-dts-codegen-master.jar!/generate-config/project-
		 * templates/springCloudMvnProvider/%23%7bT(JavaSpecs).lCase(organName)%
		 * 7d-%23%7bT(JavaSpecs).lCase(projectName)%7d/%23%7bT(JavaSpecs).lCase(
		 * organName)%7d-%23%7bT(JavaSpecs).lCase(projectName)%7d-dao/pom.xml.
		 * ftl
		 * </pre>
		 * 
		 * Note: The original path (need decoded) is used here.
		 */
		String path = res.getURI().toString();
		path = new String(URLCodec.decodeUrl(path.getBytes(UTF_8)));
		String projectRootPathPart = TPL_PROJECT_PATH.concat("/").concat(provider).concat("/");
		int i = path.indexOf(projectRootPathPart);
		if (i >= 0) {
			path = path.substring(i + projectRootPathPart.length());
		}
		return new TemplateWrapper(path, res.getFilename(), readFullyToString(res.getInputStream()));
	}

	/**
	 * Gets scanning resources locations.
	 * 
	 * @param provider
	 * @return
	 */
	private static List<String> getResourceLocations(@NotBlank String provider) {
		hasTextOf(provider, "provider");

		List<String> locations = new ArrayList<>();
		for (String suffix : LOAD_SUFFIXS) {
			locations.add(format(LOAD_PATTERN, provider, suffix));
		}
		return locations;
	}

	/**
	 * {@link TemplateWrapper}
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @author Vjay
	 * @version v1.0 2020-09-16
	 * @since
	 */
	protected final static class TemplateWrapper {

		// Template class path.
		private final String tplPath;
		private final String fileName;
		private final String fileContent;
		private final boolean isTpl;
		private final boolean isForeachTpl;

		protected TemplateWrapper(String tplPath, String filename, String fileContent) {
			hasTextOf(tplPath, "tplClassPath");
			hasTextOf(filename, "filename");
			hasTextOf(fileContent, "fileContent");
			this.tplPath = tplPath;
			this.fileName = filename;
			this.fileContent = fileContent;
			this.isTpl = filename.endsWith(TPL_SUFFIX);
			this.isForeachTpl = filename.contains(VAR_ENTITY_NAME);
		}

		public String getTplPath() {
			return tplPath;
		}

		public String getFileName() {
			return fileName;
		}

		public String getFileContent() {
			return fileContent;
		}

		public boolean isTpl() {
			return isTpl;
		}

		public boolean isForeachTpl() {
			return isForeachTpl;
		}

	}

	// Template configuration.
	public static final String TPL_BASEPATH = "generate-config";
	public static final String TPL_PROJECT_PATH = TPL_BASEPATH.concat("/project-templates");
	// Load tpl suffix rules.
	public static final String TPL_SUFFIX = ".ftl";
	public static final String[] LOAD_SUFFIXS = { TPL_SUFFIX, ".css", ".js", ".vue", ".ts", ".jpg", ".gif", ".html", ".json",
			".md", ".png", ".svg", ".eot", ".ttf", ".woff", ".woff2" };

	// e.g: classpath:/templates/xxGenProvider/**/*/.ftl
	public static final String LOAD_PATTERN = "classpath:/".concat(TPL_PROJECT_PATH).concat("/%s/**/*%s");

	// Definition of special variables.
	public static final String VAR_ENTITY_NAME = "entityName";
	public static final String VAR_WATERMARK = "watermark";
	public static final String TPL_WATERMARK = readFullyResourceString(TPL_BASEPATH.concat("/watermark.txt"));

	/**
	 * Static log of {@link SmartLogger}
	 */
	private static final SmartLogger STATICLOG = getLogger(AbstractGeneratorProvider.class);

	/**
	 * Global project {@link Template} cache.
	 */
	private static final Map<String, List<TemplateWrapper>> templatesCache = new HashMap<>();

	/**
	 * {@link SpelExpressions}
	 */
	private static final SpelExpressions defaultExpressions = create(CSharpSpecs.class, GolangSpecs.class, JavaSpecs.class,
			PythonSpecs.class);

	/**
	 * {@link ClassPathResourcePatternResolver}
	 */
	private static final ClassPathResourcePatternResolver defaultResourceResolver = new ClassPathResourcePatternResolver();

}
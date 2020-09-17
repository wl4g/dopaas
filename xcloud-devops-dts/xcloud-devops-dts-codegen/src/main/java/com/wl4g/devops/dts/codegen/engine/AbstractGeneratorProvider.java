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
package com.wl4g.devops.dts.codegen.engine;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wl4g.components.common.annotation.Nullable;
import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.components.common.resource.StreamResource;
import com.wl4g.components.common.resource.resolver.ClassPathResourcePatternResolver;
import com.wl4g.components.core.utils.expression.SpelExpressions;
import com.wl4g.devops.dts.codegen.bean.GenProject;
import com.wl4g.devops.dts.codegen.bean.GenTable;
import com.wl4g.devops.dts.codegen.core.context.GenerateContext;
import com.wl4g.devops.dts.codegen.engine.naming.CSharpSpecs;
import com.wl4g.devops.dts.codegen.engine.naming.GolangSpecs;
import com.wl4g.devops.dts.codegen.engine.naming.JavaSpecs;
import com.wl4g.devops.dts.codegen.engine.naming.PythonSpecs;
import freemarker.template.Template;

import javax.validation.constraints.NotBlank;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.wl4g.components.common.collection.Collections2.ensureMap;
import static com.wl4g.components.common.io.ByteStreamUtils.readFullyToString;
import static com.wl4g.components.common.io.FileIOUtils.writeFile;
import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.components.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.components.common.view.Freemarkers.renderingTemplateToString;
import static com.wl4g.components.core.utils.expression.SpelExpressions.create;
import static com.wl4g.devops.dts.codegen.utils.FreemarkerUtils.defaultGenConfigurer;
import static java.lang.String.valueOf;
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
			generate();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Hanlding generation.
	 *
	 * @throws Exception
	 */
	protected abstract void generate() throws Exception;

	protected void doGenerate(String provider) throws Exception {
		GenProject project = context.getGenProject();

		// Load templates.
		List<TemplateWrapper> tpls = loadTemplates(provider);

		// Handling generate
		doHandleGenerateAndSave(tpls, project, context.getJobDir().getAbsolutePath());
	}

	/**
	 * Do handling rendering generate and save.
	 *
	 * @param tpls
	 * @param project
	 * @param targetBasePath
	 * @throws Exception
	 */
	private void doHandleGenerateAndSave(List<TemplateWrapper> tpls, GenProject project, String targetBasePath) throws Exception {
		for (TemplateWrapper tpl : tpls) {
			// Create rednering model.
			Map<String, Object> model = createRenderingModel(tpl.getTplPath(), project);
			if (tpl.isTpl()) {
				// foreach template by table
				if (tpl.isForeachTpl()) {
					for (GenTable tab : project.getGenTables()) {
						// Additidtion table model attributes
						model.putAll(toRenderingFlatModel(tab));

						// Rendering tpl
						String targetPath = targetBasePath.concat("/").concat(resolveExpressionPath(tpl.getTplPath(), model));
						Template template = new Template(tpl.getFileName(), tpl.getFileContent(), defaultGenConfigurer);
						String fileContent = renderingTemplateToString(template, model);
						writeFile(new File(targetPath), fileContent, false);
					}
				}
				// Simple template.
				else {
					String targetPath = targetBasePath.concat("/").concat(resolveExpressionPath(tpl.getTplPath(), model));
					// Rendering tpl
					Template template = new Template(tpl.getFileName(), tpl.getFileContent(), defaultGenConfigurer);
					String fileContent = renderingTemplateToString(template, model);
					writeFile(new File(targetPath), fileContent, false);
				}
			}
			// e.g: static file
			else {
				String targetPath = targetBasePath + "/" + resolveExpressionPath(tpl.getTplPath(), model);
				writeFile(new File(targetPath), tpl.getFileContent(), false);
			}
		}
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

		return model;
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
	 * Resolving path SPEL expression
	 *
	 * @param tplPath
	 * @param model
	 * @return
	 */
	private String resolveExpressionPath(String tplPath, Map<String, Object> model) {
		if (tplPath.endsWith(".ftl")) {
			tplPath = tplPath.substring(0, tplPath.length() - 4);
		}
		return valueOf(defaultExpressions.resolve(tplPath, model));
	}

	/**
	 * Load {@link Template} list by provider.
	 *
	 * @param provider
	 * @return
	 * @throws IOException
	 */
	private static List<TemplateWrapper> loadTemplates(String provider) throws IOException {
		List<TemplateWrapper> tpls = templatesCache.get(provider);
		if (isNull(tpls)) {
			synchronized (AbstractGeneratorProvider.class) {
				tpls = templatesCache.get(provider);
				if (isNull(tpls)) {
					tpls = new ArrayList<>();
					Set<StreamResource> resources = defaultResourceResolver
							.getResources("classpath:/" + DEFAULT_TPL_BASEPATH + "/" + provider + "/**/*.ftl");
					for (StreamResource res : resources) {
						if (res.getFile().isFile()) {
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
	 * @throws IOException
	 */
	private static TemplateWrapper wrapTemplate(StreamResource res, String provider) throws IOException {
		String path = res.getURI().getPath();
		String splitStr = DEFAULT_TPL_BASEPATH + "/" + provider + "/";
		int i = path.indexOf(splitStr);
		if (i >= 0) {
			path = path.substring(i + splitStr.length());
		}
		return new TemplateWrapper(path, res.getFilename(), readFullyToString(res.getInputStream()));
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
			this.isTpl = filename.endsWith(DEFAULT_TPL_SUFFIX);
			this.isForeachTpl = filename.contains(VARIABLE_ENTITY_NAME);
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
	private static final String DEFAULT_TPL_BASEPATH = "projects-template";
	private static final String DEFAULT_TPL_SUFFIX = ".fpl";

	// Template configuration.
	private static final String VARIABLE_ENTITY_NAME = "entityName";

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
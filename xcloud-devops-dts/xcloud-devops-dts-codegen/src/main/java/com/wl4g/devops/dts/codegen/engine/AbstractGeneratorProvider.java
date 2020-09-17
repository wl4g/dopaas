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

import static com.wl4g.components.core.utils.expression.SpelExpressions.create;
import static com.wl4g.components.common.io.ByteStreamUtils.readFullyToString;
import static com.wl4g.components.common.io.FileIOUtils.writeFile;
import static com.wl4g.components.common.view.Freemarkers.renderingTemplateToString;

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
import static com.wl4g.devops.dts.codegen.utils.FreemarkerUtils.defaultGenConfigurer;
import freemarker.template.Template;

import static java.util.Objects.isNull;
import java.io.*;
import java.util.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static org.apache.commons.beanutils.BeanUtils.describe;

import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;

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
		genCode(provider, project, context.getJobDir().getAbsolutePath());
	}

	private void genCode(String provider, GenProject project, String jobPath) throws Exception {
		List<TemplateWrapper> tpls = loadTemplates(provider);
		doHandleGenerateAndSave(tpls, project, jobPath);
	}

	private void doHandleGenerateAndSave(List<TemplateWrapper> tpls, GenProject project, String targetBasePath) throws Exception {
		for (TemplateWrapper tpl : tpls) {
			if (tpl.isTpl()) {
				// Create rednering model.
				Map<String, Object> model = createRenderingModel(tpl.getTplPath(), project, null);
				if (tpl.isForeachTpl()) { // foreach table
					for (GenTable tab : project.getGenTables()) {
						String targetPath = targetBasePath.concat("/").concat(parseTablePath(tpl.getTplPath(), tab));
						Template template = new Template(tpl.getFileName(), tpl.getFileContent(), defaultGenConfigurer);
						String fileContent = renderingTemplateToString(template, model);
						writeFile(new File(targetPath), fileContent, false);
					}
				} else {
					String targetPath = targetBasePath.concat("/").concat(parsePackagePath(tpl.getTplPath(), project));
					Template template = new Template(tpl.getFileName(), tpl.getFileContent(), defaultGenConfigurer);
					String fileContent = renderingTemplateToString(template, model);
					writeFile(new File(targetPath), fileContent, false);
				}
			} else { // e.g: static file
				String targetPath = targetBasePath + "/" + parsePackagePath(tpl.getTplPath(), project);
				writeFile(new File(targetPath), tpl.getFileContent(), false);
			}
		}
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
	private Map<String, Object> createRenderingModel(String tplPath, GenProject project, GenTable table) throws Exception {
		// Gets customize model.
		Map<String, Object> model = customizeRenderingModel(tplPath, project, table);

		// Fill requires rendering parameters.
		model.putAll(describe(project));
		model.putAll(describe(table));

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
	protected Map<String, Object> customizeRenderingModel(@NotBlank String tplPath, @NotNull GenProject project,
			@Nullable GenTable table) {
		return null;
	}

	private String parsePackagePath(String tplPath, GenProject project) {
		if (tplPath.endsWith(".ftl")) {
			tplPath = tplPath.substring(0, tplPath.length() - 4);
		}
		tplPath = "" + defaultExpressions.resolve(tplPath, project);
		return tplPath;
	}

	private String parseTablePath(String tplPath, GenTable table) {
		if (tplPath.endsWith(".ftl")) {
			tplPath = tplPath.substring(0, tplPath.length() - 4);
		}
		tplPath = "" + defaultExpressions.resolve(tplPath, table);
		return tplPath;
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
	private static final String DEFAULT_TPL_SUFFIX = ".tpl";

	// Template configuration.
	private static final String VARIABLE_ENTITY_NAME = "entityName";

	/** Global project {@link Template} cache. */
	private static final Map<String, List<TemplateWrapper>> templatesCache = new HashMap<>();

	/** {@link SpelExpressions} */
	private static final SpelExpressions defaultExpressions = create(CSharpSpecs.class, GolangSpecs.class, JavaSpecs.class,
			PythonSpecs.class);

	/** {@link ClassPathResourcePatternResolver} */
	private static final ClassPathResourcePatternResolver defaultResourceResolver = new ClassPathResourcePatternResolver();

}
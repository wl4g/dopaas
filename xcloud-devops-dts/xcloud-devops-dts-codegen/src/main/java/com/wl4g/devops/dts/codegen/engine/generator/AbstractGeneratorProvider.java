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
package com.wl4g.devops.dts.codegen.engine.generator;

import com.wl4g.components.common.annotation.Nullable;
import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.components.core.utils.expression.SpelExpressions;
import com.wl4g.devops.dts.codegen.bean.GenProject;
import com.wl4g.devops.dts.codegen.bean.GenTable;
import com.wl4g.devops.dts.codegen.config.CodegenProperties;
import com.wl4g.devops.dts.codegen.engine.context.GenerateContext;
import com.wl4g.devops.dts.codegen.engine.generator.render.RenderModel;
import com.wl4g.devops.dts.codegen.engine.specs.BaseSpecs;
import com.wl4g.devops.dts.codegen.engine.template.TemplateResource;
import com.wl4g.devops.dts.codegen.exception.RenderingGenerateException;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.google.common.base.Charsets.UTF_8;

import static com.wl4g.devops.dts.codegen.engine.generator.render.ModelAttributeConstants.GEN_PROJECT_FULLNAME;
import static com.wl4g.devops.dts.codegen.engine.generator.render.ModelAttributeConstants.GEN_PROJECT_FULLPATH;
import static com.wl4g.components.common.collection.Collections2.ensureMap;
import static com.wl4g.components.common.io.FileIOUtils.writeFile;
import static com.wl4g.components.common.lang.Assert2.*;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.components.common.view.Freemarkers.createDefault;
import static com.wl4g.components.common.view.Freemarkers.renderingTemplateToString;
import static com.wl4g.components.core.bean.BaseBean.DISABLED;
import static com.wl4g.components.core.utils.expression.SpelExpressions.hasSpelTemplateExpr;
import static com.wl4g.devops.dts.codegen.engine.generator.render.RenderUtil.convertToRenderingModel;
import static com.wl4g.devops.dts.codegen.engine.generator.render.ModelAttributeConstants.*;
import static java.io.File.separator;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;

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

	/**
	 * Generate primary {@link RenderModel}.
	 */
	protected final RenderModel primaryModel;

	public AbstractGeneratorProvider(@NotNull GenerateContext context, @Nullable Map<String, Object> defaultFlatModel) {
		this.context = notNullOf(context, "context");
		// Primary rendering model.
		this.primaryModel = initPrimaryRenderingModel(context.getConfiguration(), context, defaultFlatModel);
	}

	@Override
	public void close() throws IOException {
		context.getMetadataResolver().close();
	}

	/**
	 * Processing generate codes with templates.
	 *
	 * @param provider
	 * @throws Exception
	 */
	protected void doGenerateWithTemplates(String provider) throws Exception {
		hasTextOf(provider, "provider");
		GenProject project = context.getGenProject();

		// Locate load templates.
		List<TemplateResource> ress = context.getLocator().locate(provider);

		// Rendering templates
		for (TemplateResource res : ress) {
			try {
				// Core generate processing.
				coreRenderingGenerate(res, project);
			} catch (Exception e) {
				throw new RenderingGenerateException(format("Cannot rendering template for %s", res), e);
			}
		}

	}

	/**
	 * Handling core rendering templates generate and save.
	 *
	 * @param res
	 * @param project
	 * @throws Exception
	 */
	private void coreRenderingGenerate(TemplateResource res, GenProject project) throws Exception {
		log.debug("Rendering generate for - {}", res.getRawFilename());

		if (res.isRender()) {
			// Foreach rendering entitys. (Note: included resolve moduleName)
			if (res.isForeachEntitys()) {
				for (GenTable tab : project.getGenTables()) {
					// Skip disable genTable(entity) rendering.
					if (equalsIgnoreCase(tab.getStatus(), valueOf(DISABLED))) {
						continue;
					}
					context.setGenTable(tab); // Set current genTable

					// When traversing the rendering table (entity), it
					// needs to share the item information and must be
					// cloned to prevent it from being covered.
					RenderModel tableModel = primaryModel.clone();

					// Add rendering model of GenTable.
					tableModel.putAll(convertToRenderingModel(tab));

					// Rendering.
					processRenderingTemplateToString(res, tableModel);
				}
			}
			// Foreach rendering modules. (Note: no-include resolve entityName)
			else if (res.isForeachModules()) {
				// Rendering of moduleMap.
				Map<String, List<GenTable>> moduleMap = primaryModel.getElement(GEN_MODULE_MAP);

				for (Entry<String, List<GenTable>> ent : moduleMap.entrySet()) {
					String moduleName = ent.getKey();
					List<GenTable> tablesOfModule = ent.getValue();

					// When traversing the rendering module, it
					// needs to share the item information and must be
					// cloned to prevent it from being covered.
					RenderModel moduleModel = primaryModel.clone();

					// Add rendering model of module tables.
					moduleModel.putAll(convertToRenderingModel(tablesOfModule));
					moduleModel.put(GEN_MODULE_NAME, moduleName);

					// Rendering.
					processRenderingTemplateToString(res, moduleModel);
				}
			}
			// Simple template rendering.
			else {
				// Clone the primary model to customize the model.
				RenderModel model = primaryModel.clone();

				// Rendering.
				processRenderingTemplateToString(res, model);
			}
		}
		// Static resource no-render content.
		else {
			// Clone the primary model to customize the model.
			RenderModel model = primaryModel.clone();

			// Add customize model.
			customizeRenderingModel(res, model);

			// If the 'if' directives is enabled and returns false, the
			// template is not rendered.
			if (!processIfDirectives(res, model)) {
				return;
			}

			// Call post rendered.
			afterRenderingComplete(res, res.getContent(), resolveTemplatePath(res, model));
		}

	}

	/**
	 * Customize rendering model
	 *
	 * @param res
	 * @param model
	 * @return
	 */
	protected void customizeRenderingModel(@NotNull TemplateResource res, @NotNull RenderModel model) {
		GenProject project = context.getGenProject();

		String fullProjectName = project.getOrganName().concat("-").concat(project.getProjectName());
		// Capital letters are allowed in special cases.
		// fullProjectName.toString().toLowerCase(US)
		model.put(GEN_PROJECT_FULLNAME, fullProjectName);

		String fullProjectPath = project.getOrganType().concat(File.separator).concat(project.getOrganName())
				.concat(File.separator).concat(project.getProjectName());
		model.put(GEN_PROJECT_FULLPATH, fullProjectPath);
	}

	/**
	 * Preparing rendering processing.
	 *
	 * @param res
	 * @param model
	 * @return
	 */
	protected String preRendering(@NotNull TemplateResource res, @NotEmpty Map<String, Object> model) {
		notNullOf(res, "res");
		notEmptyOf(model, "model");

		// Customizable pre rendering:
		// ...

		// Default nothing do
		return res.getContentAsString();
	}

	/**
	 * Post rendering complete processing.
	 *
	 * @param res
	 * @param renderedBytes
	 * @param writePath
	 */
	protected void afterRenderingComplete(@NotNull TemplateResource res, @NotNull byte[] renderedBytes,
			@NotBlank String writePath) {
		notNullOf(res, "res");
		notNullOf(renderedBytes, "renderedBytes");
		hasTextOf(writePath, "writePath");

		// Default by write to local disk
		writeFile(new File(writePath), renderedBytes, false);
	}

	/**
	 * Processing resolving template path.
	 *
	 * @param res
	 * @param model
	 * @return Return resolved template resource canonical path.
	 * @throws Exception
	 */
	private final String resolveTemplatePath(TemplateResource res, RenderModel model) throws Exception {
		notNullOf(res, "res");
		notEmptyOf(model, "model");
		res.validate();

		String writeBasePath = context.getJobDir().getAbsolutePath();
		// Resolve template path with SPEL expression.
		log.debug("Resolving template path for pathname: {}, model: {}", res.getPathname(), model);
		return writeBasePath.concat(separator).concat(spelExpr.resolve(res.getPathname(), model));
	}

	/**
	 * Processing template rendering to string.
	 *
	 * @param res
	 * @param model
	 * @return Return rendered source codes file path.
	 * @throws Exception
	 */
	private final String processRenderingTemplateToString(TemplateResource res, RenderModel model) throws Exception {
		notNullOf(res, "res");
		notEmptyOf(model, "model");
		res.validate();

		log.debug("Gen rendering of model: {}", model);

		// Step1: Add customize model.
		customizeRenderingModel(res, model);

		// Step2: If the 'if' directives is enabled and returns false, the
		// template is not rendered.
		if (!processIfDirectives(res, model)) {
			return null;
		}

		// Step3: Resolve template path.
		String writePath = resolveTemplatePath(res, model);

		// Step4: Preparing rendering.
		String renderedString = preRendering(res, model);
		renderedString = isBlank(renderedString) ? res.getContentAsString() : renderedString; // Fallback

		// Step5: Core rendering.
		Template template = new Template(res.getShortFilename(), renderedString, defaultGenConfigurer);
		renderedString = renderingTemplateToString(template, model);

		// Step6: After rendered processing.
		afterRenderingComplete(res, renderedString.getBytes(UTF_8), writePath);

		return writePath;
	}

	/**
	 * Process parse the 'if' directives expression and get the result.
	 * 
	 * @param res
	 * @param model
	 * @return
	 */
	private final boolean processIfDirectives(TemplateResource res, RenderModel model) {
		if (res.isIfDirectives()) {
			// 1. Match checks against the key of the data model.
			if (model.containsKey(res.getIfDirectivesExpr())) {
				return true;
			}
			// 2. Match the check according to the spel expression.
			if (hasSpelTemplateExpr(res.getIfDirectivesExpr())) {
				return spelExpr.resolve(res.getIfDirectivesExpr(), model);
			}
			// 3. If there is no match, the result is false
			return false;
		}
		// Direct pass without 'if' directives.
		return true;
	}

	/**
	 * Initialization primary rendering model.
	 *
	 * @param config
	 * @param context
	 * @param defaultFlatModel
	 * @return
	 */
	private final RenderModel initPrimaryRenderingModel(CodegenProperties config, GenerateContext context,
			@Nullable Map<String, Object> defaultFlatModel) {
		RenderModel model = new RenderModel(config.isAllowRenderingCustomizeModelOverride());

		try {
			// Step1: Add model of GenProject.
			//
			model.putAll(convertToRenderingModel(context.getGenProject()));

			// Step2: Add model of GenDataSource.
			//
			Map<String, Object> datasource = convertToRenderingModel(context.getGenDataSource());
			// Gen DB version.
			datasource.put(GEN_DB_VERSION, context.getGenDataSource().getDbversion());
			model.put(GEN_DB, datasource);

			// Step3: Add model of watermark.
			//
			model.put(GEN_COMMON_WATERMARK, context.getConfiguration().getWatermarkContent());

			// Step4: Add model of moduleMap. moduleMap{moduleName => tables}
			//
			Map<String, List<GenTable>> moduleMap = new HashMap<>();
			for (GenTable tab : context.getGenProject().getGenTables()) {
				String moduleName = tab.getModuleName();
				List<GenTable> tablesOfModule = moduleMap.getOrDefault(moduleName, new ArrayList<>());
				tablesOfModule.add(tab);
				moduleMap.put(moduleName, tablesOfModule);
			}
			model.put(GEN_MODULE_MAP, moduleMap);

			// Step5: Merge and add default model.
			//
			Map<String, Object> mergeDefaultModel = ensureMap(defaultFlatModel);
			mergeDefaultModel.put(GEN_COMMON_BASESPECS, new BaseSpecs());
			mergeDefaultModel.forEach((key, value) -> model.put(key, value));

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		return model.readonly();
	}

	/**
	 * {@link SpelExpressions}
	 */
	private static final SpelExpressions spelExpr = SpelExpressions.create();

	/** Default freemarker {@link Configuration} */
	private static final Configuration defaultGenConfigurer = createDefault().withVersion(Configuration.VERSION_2_3_27)
			.withTemplateLoaders(singletonList(new StringTemplateLoader())).build();

}
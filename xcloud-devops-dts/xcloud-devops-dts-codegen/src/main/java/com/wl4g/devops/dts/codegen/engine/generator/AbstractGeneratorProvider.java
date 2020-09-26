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
import com.wl4g.devops.dts.codegen.engine.template.GenTemplateLocator.TemplateResourceWrapper;
import com.wl4g.devops.dts.codegen.utils.MapRenderModel;
import freemarker.template.Template;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.google.common.collect.Lists.newArrayList;
import static com.wl4g.components.common.collection.Collections2.safeArray;
import static com.wl4g.components.common.io.FileIOUtils.writeFile;
import static com.wl4g.components.common.lang.Assert2.*;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.components.common.view.Freemarkers.renderingTemplateToString;
import static com.wl4g.devops.dts.codegen.engine.specs.BaseSpecs.firstLCase;
import static com.wl4g.devops.dts.codegen.engine.template.GenTemplateLocator.DEFAULT_TPL_SUFFIX;
import static com.wl4g.devops.dts.codegen.utils.FreemarkerUtils.defaultGenConfigurer;
import static com.wl4g.devops.dts.codegen.utils.ModelAttributeDefinitions.*;
import static com.wl4g.devops.dts.codegen.utils.RenderPropertyUtils.convertToRenderingModel;
import static java.io.File.separator;
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
	 * Generate primary {@link MapRenderModel}.
	 */
	protected final MapRenderModel primaryModel;

	public AbstractGeneratorProvider(@NotNull GenerateContext context, @Nullable Object... defaultSubModels) {
		this.context = notNullOf(context, "context");
		// Primary rendering model.
		this.primaryModel = initPrimaryRenderingModel(context.getConfiguration(), context, defaultSubModels);
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
		List<TemplateResourceWrapper> tplResources = context.getLocator().locate(provider);

		// Rendering templates
		for (TemplateResourceWrapper res : tplResources) {
			// Core generate processing.
			coreRenderingGenerate(res, project);
		}

	}

	/**
	 * Handling core rendering templates generate and save.
	 *
	 * @param tplResource
	 * @param project
	 * @throws Exception
	 */
	protected void coreRenderingGenerate(TemplateResourceWrapper tplResource, GenProject project) throws Exception {
		log.info("Rendering generate for - {}", tplResource.getPathname());

		if (tplResource.isTemplate()) {
			// Foreach rendering entitys. (Note: included resolve moduleName)
			if (tplResource.isForeachEntitys()) {
				for (GenTable tab : project.getGenTables()) {
					context.setGenTable(tab); // Set current genTable

					// When traversing the rendering table (entity), it
					// needs to share the item information and must be
					// cloned to prevent it from being covered.
					MapRenderModel tableModel = primaryModel.clone();

					// Add rendering model of GenTable.
					tableModel.putAll(convertToRenderingModel(tab));

					// Rendering.
					processRenderingTemplateToString(tplResource, tableModel);
				}
			}
			// Foreach rendering modules. (Note: no-include resolve entityName)
			else if (tplResource.isForeachModules()) {
				// Rendering of moduleMap.
				Map<String, List<GenTable>> moduleMap = primaryModel.getElement(GEN_MODULE_MAP);

				for (Entry<String, List<GenTable>> ent : moduleMap.entrySet()) {
					String moduleName = ent.getKey();
					List<GenTable> tablesOfModule = ent.getValue();

					// When traversing the rendering module, it
					// needs to share the item information and must be
					// cloned to prevent it from being covered.
					MapRenderModel moduleModel = primaryModel.clone();

					// Add rendering model of module tables.
					moduleModel.putAll(convertToRenderingModel(tablesOfModule));
					moduleModel.put(GEN_MODULE_NAME, moduleName);

					// Rendering.
					processRenderingTemplateToString(tplResource, moduleModel);
				}
			}
			// Simple template rendering.
			else {
				// Clone the primary model to customize the model.
				MapRenderModel model = primaryModel.clone();

				// Rendering.
				processRenderingTemplateToString(tplResource, model);
			}
		}
		// Static resource no-render content.
		else {
			// Clone the primary model to customize the model.
			MapRenderModel model = primaryModel.clone();

			// Rendering.
			processRenderingTemplateToString(tplResource, model);
		}

	}

	/**
	 * Preparing rendering processing.
	 * 
	 * @param tplResource
	 * @param model
	 * @return
	 */
	protected String preRendering(@NotNull TemplateResourceWrapper tplResource, @NotEmpty Map<String, Object> model) {
		notNullOf(tplResource, "tplResource");
		notEmptyOf(model, "model");

		// It is recommended to use FreeMarker to call Java methods.

		// // Rendering with spel ahead of time
		// //
		// // Note: After testing, we found that we should use spel to render
		// // first, otherwise FreeMarker will report an error.
		// //
		// return defaultExpressions.resolve(tpl.getFileContent(), model);
		return tplResource.getContent();
	}

	/**
	 * Post rendering complete processing.
	 * 
	 * @param tplResource
	 * @param renderedString
	 * @param writePath
	 */
	protected void postRenderingComplete(@NotNull TemplateResourceWrapper tplResource, @NotBlank String renderedString,
			@NotBlank String writePath) {
		notNullOf(tplResource, "tplResource");
		hasTextOf(renderedString, "renderedString");
		hasTextOf(writePath, "writePath");

		// Default by write to local disk
		writeFile(new File(writePath), renderedString, false);
	}

	/**
	 * Customize rendering model
	 *
	 * @param tplResource
	 * @param model
	 * @return
	 */
	protected void customizeRenderingModel(@NotNull TemplateResourceWrapper tplResource, @NotNull MapRenderModel model) {
	}

	/**
	 * Resolving path SPEL expression
	 *
	 * @param expression
	 * @param model
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected final String resolveSpelExpression(String expression, final MapRenderModel model) {
		if (expression.endsWith(DEFAULT_TPL_SUFFIX)) {
			expression = expression.substring(0, expression.length() - DEFAULT_TPL_SUFFIX.length());
		}
		final String expr = expression;
		log.debug("Resolving SPEL for expression: {}, model: {}", () -> expr, () -> model);

		return spelExpr.resolve(expr, model);
	}

	/**
	 * Do processing template rendering to string.
	 * 
	 * @param tplResource
	 * @param model
	 * @return Return rendered source codes file path.
	 * @throws Exception
	 */
	private final String processRenderingTemplateToString(TemplateResourceWrapper tplResource, MapRenderModel model)
			throws Exception {
		notNullOf(tplResource, "tplResource");
		notEmptyOf(model, "model");
		tplResource.validate();

		log.debug("Gen rendering of model: {}", model);

		// Step1: Add customize model.
		customizeRenderingModel(tplResource, model);

		// Step2: Preparing rendering.
		String renderedString = preRendering(tplResource, model);
		renderedString = isBlank(renderedString) ? tplResource.getContent() : renderedString; // Fallback

		// Step3: Core rendering.
		Template template = new Template(tplResource.getName(), renderedString, defaultGenConfigurer);
		renderedString = renderingTemplateToString(template, model);

		// Step4: Resolve SPEL template path.
		String writeBasePath = context.getJobDir().getAbsolutePath();
		String writePath = writeBasePath.concat(separator).concat(resolveSpelExpression(tplResource.getPathname(), model));

		// Step5: Call post rendered.
		postRenderingComplete(tplResource, renderedString, writePath);

		return writePath;
	}

	/**
	 * Initialization primary rendering model.
	 * 
	 * @param config
	 * @param context
	 * @param defaultSubModels
	 * @return
	 */
	private final MapRenderModel initPrimaryRenderingModel(CodegenProperties config, GenerateContext context,
			@Nullable Object... defaultSubModels) {
		MapRenderModel model = new MapRenderModel(config.isAllowRenderingCustomizeModelOverride());

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

			// Step5: Add default models.
			//
			newArrayList(safeArray(Object.class, defaultSubModels)).forEach(subModel -> {
				if (subModel instanceof Class) {
					// e.g: JavaSpecs => JavaSpecs.class
					model.put(subModel.getClass().getSimpleName(), subModel);
				} else {
					// e.g: javaSpecs => new JavaSpecs()
					model.put(firstLCase(subModel.getClass().getSimpleName()), subModel);
				}
			});

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		return model.readonly();
	}

	/**
	 * {@link SpelExpressions}
	 */
	private static final SpelExpressions spelExpr = SpelExpressions.create();

}
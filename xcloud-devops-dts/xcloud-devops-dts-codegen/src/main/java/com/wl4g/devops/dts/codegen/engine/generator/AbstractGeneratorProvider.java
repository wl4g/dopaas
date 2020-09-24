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

import static com.google.common.collect.Lists.newArrayList;
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

import static java.io.File.separator;

import static com.wl4g.components.common.collection.Collections2.safeArray;
import static com.wl4g.components.common.io.FileIOUtils.readFullyResourceString;
import static com.wl4g.components.common.io.FileIOUtils.writeFile;
import static com.wl4g.components.common.lang.Assert2.*;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.components.common.view.Freemarkers.renderingTemplateToString;
import static com.wl4g.devops.dts.codegen.engine.template.ClassPathGenTemplateLocator.TPL_BASEPATH;
import static com.wl4g.devops.dts.codegen.engine.template.GenTemplateLocator.DEFAULT_TPL_SUFFIX;
import static com.wl4g.devops.dts.codegen.engine.naming.BaseSpecs.firstLCase;
import static com.wl4g.devops.dts.codegen.utils.FreemarkerUtils.defaultGenConfigurer;
import static com.wl4g.devops.dts.codegen.utils.RenderPropertyUtils.convertToRenderingModel;
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

	public AbstractGeneratorProvider(@NotNull GenerateContext context, @Nullable Object... addModels) {
		this.context = notNullOf(context, "context");
		// Primary rendering model.
		this.primaryModel = initPrimaryRenderingModel(context.getConfiguration(), context, addModels);
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

		// Core processing generate.
		coreRenderingTemplates(tplResources, project, context.getJobDir().getAbsolutePath());
	}

	/**
	 * Handling core rendering templates generate and save.
	 *
	 * @param tplResources
	 * @param project
	 * @param writeBasePath
	 * @throws Exception
	 */
	protected void coreRenderingTemplates(List<TemplateResourceWrapper> tplResources, GenProject project, String writeBasePath)
			throws Exception {
		for (TemplateResourceWrapper res : tplResources) {
			log.info("Rendering generate for - {}", res.getPathname());

			if (res.isRender()) {
				// Foreach rendering entitys(tables)
				if (res.isForeachTemplate()) {
					for (GenTable tab : project.getGenTables()) {
						context.setGenTable(tab); // Set current genTable

						// When traversing the rendering table (entity), it
						// needs to share the item information and must be
						// cloned to prevent it from being covered.
						MapRenderModel tableModel = primaryModel.clone();

						// Add rendering model of GenTable.
						tableModel.putAll(convertToRenderingModel(tab));

						// Add customization rendering model.
						customizeRenderingModel(res, tableModel);

						// Rendering source templates.
						String writePath = writeBasePath.concat(separator)
								.concat(resolveSpelExpression(res.getPathname(), tableModel));
						String renderedString = processRenderingTemplateToString(res, tableModel);

						// Call post rendered.
						postRenderingComplete(res, renderedString, writePath);
					}
				}
				// Foreach rendering module
				else if (res.isForeachModule()) {
					// Rendering of moduleMap.
					Map<String, List<GenTable>> moduleMap = primaryModel.getElement("moduleMap");

					for (Entry<String, List<GenTable>> ent : moduleMap.entrySet()) {
						String moduleName = ent.getKey();
						List<GenTable> tablesOfModule = ent.getValue();

						// When traversing the rendering module, it
						// needs to share the item information and must be
						// cloned to prevent it from being covered.
						MapRenderModel moduleModel = primaryModel.clone();

						// Add rendering model of module tables.
						moduleModel.putAll(convertToRenderingModel(tablesOfModule));
						moduleModel.put("moduleName", moduleName);

						// Add customization rendering model.
						customizeRenderingModel(res, moduleModel);

						// Rendering source templates.
						String writePath = writeBasePath.concat(separator)
								.concat(resolveSpelExpression(res.getPathname(), moduleModel));
						String renderedString = processRenderingTemplateToString(res, moduleModel);

						// Call post rendered.
						postRenderingComplete(res, renderedString, writePath);
					}
				}
				// Simple template rendering.
				else {
					// Clone the primary model to customize the model.
					MapRenderModel model = primaryModel.clone();

					// Add customization rendering model.
					customizeRenderingModel(res, model);

					// Rendering source templates.
					String writePath = writeBasePath.concat(separator).concat(resolveSpelExpression(res.getPathname(), model));
					String renderedString = processRenderingTemplateToString(res, model);

					// Call post rendered.
					postRenderingComplete(res, renderedString, writePath);
				}
			}
			// Static resource no-render.
			else {
				String writePath = writeBasePath.concat(separator).concat(resolveSpelExpression(res.getPathname(), primaryModel));
				writeFile(new File(writePath), res.getContent(), false);
			}
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
	 * @return
	 * @throws Exception
	 */
	private final String processRenderingTemplateToString(TemplateResourceWrapper tplResource, MapRenderModel model)
			throws Exception {
		notNullOf(tplResource, "tplResource");
		notEmptyOf(model, "model");
		tplResource.validate();

		log.debug("Generate rendering model: {}", model);

		// Preparing rendering
		String renderedString = preRendering(tplResource, model);
		renderedString = isBlank(renderedString) ? tplResource.getContent() : renderedString; // Fallback

		// Primary rendering
		Template template = new Template(tplResource.getName(), renderedString, defaultGenConfigurer);
		return renderingTemplateToString(template, model);
	}

	/**
	 * Initialization primary rendering model.
	 * 
	 * @param config
	 * @param context
	 * @param addModels
	 * @return
	 */
	private final MapRenderModel initPrimaryRenderingModel(CodegenProperties config, GenerateContext context,
			@Nullable Object... addModels) {
		MapRenderModel model = new MapRenderModel(config.isAllowRenderingCustomizeModelOverride());

		try {
			//
			// 1. --- Add model of GenProject.
			//
			model.putAll(convertToRenderingModel(context.getGenProject()));

			//
			// 2. --- Add model of GenDataSource.
			//
			Map<String, Object> datasource = convertToRenderingModel(context.getGenDataSource());
			// Gen DB version.
			datasource.put("dbVersion", context.getMetadataResolver().findDBVersion());
			model.put("datasource", datasource);

			//
			// 3. --- Add model of watermark.
			//
			model.put("watermark", MODEL_WATERMARK_VALUE);

			//
			// 4. --- Add model of moduleMap.
			// moduleMap{moduleName => tables}
			//
			Map<String, List<GenTable>> moduleMap = new HashMap<>();
			for (GenTable tab : context.getGenProject().getGenTables()) {
				String moduleName = tab.getModuleName();
				List<GenTable> tablesOfModule = moduleMap.getOrDefault(moduleName, new ArrayList<>());
				tablesOfModule.add(tab);
				moduleMap.put(moduleName, tablesOfModule);
			}
			model.put("moduleMap", moduleMap);

			//
			// 5. --- Add model of SPEL utilitiys.
			//
			newArrayList(safeArray(Object.class, addModels)).forEach(addModel -> {
				if (addModel instanceof Class) {
					// e.g: JavaSpecs => JavaSpecs.class
					model.put(addModel.getClass().getSimpleName(), addModel);
				} else {
					// e.g: javaSpecs => new JavaSpecs()
					model.put(firstLCase(addModel.getClass().getSimpleName()), addModel);
				}
			});

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		return model.readonly();
	}

	// Rendering model for watermark.
	public static final String MODEL_WATERMARK_VALUE = readFullyResourceString(TPL_BASEPATH.concat("/watermark.txt"));

	/**
	 * {@link SpelExpressions}
	 */
	private static final SpelExpressions spelExpr = SpelExpressions.create();

}
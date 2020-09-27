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
import com.wl4g.devops.dts.codegen.engine.specs.BaseSpecs;
import com.wl4g.devops.dts.codegen.engine.template.GenTemplateLocator.TemplateResourceWrapper;
import com.wl4g.devops.dts.codegen.exception.RenderingGenerateException;
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

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.components.common.collection.Collections2.ensureMap;
import static com.wl4g.components.common.io.FileIOUtils.writeFile;
import static com.wl4g.components.common.lang.Assert2.*;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.components.common.view.Freemarkers.renderingTemplateToString;
import static com.wl4g.devops.dts.codegen.engine.template.GenTemplateLocator.DEFAULT_TPL_SUFFIX;
import static com.wl4g.devops.dts.codegen.utils.FreemarkerUtils.defaultGenConfigurer;
import static com.wl4g.devops.dts.codegen.utils.ModelAttributeDefinitions.*;
import static com.wl4g.devops.dts.codegen.utils.RenderPropertyUtils.convertToRenderingModel;
import static java.io.File.separator;
import static java.lang.String.format;
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

	public AbstractGeneratorProvider(@NotNull GenerateContext context, @Nullable Map<String, Object> defaultFlatModel) {
		this.context = notNullOf(context, "context");
		// Primary rendering model.
		this.primaryModel = initPrimaryRenderingModel(context.getConfiguration(), context, defaultFlatModel);
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
	 * @param tplResource
	 * @param project
	 * @throws Exception
	 */
	protected void coreRenderingGenerate(TemplateResourceWrapper tplResource, GenProject project) throws Exception {
		log.debug("Rendering generate for - {}", tplResource.getPathname());

		if (tplResource.isRender()) {
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

			// Add customize model.
			customizeRenderingModel(tplResource, model);

			// Call post rendered.
			postRenderingComplete(tplResource, tplResource.getContent(), resolveTemplatePath(tplResource, model));
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

		// Customizable pre rendering:
		// ...

		// Default nothing do
		return tplResource.getContentAsString();
	}

	/**
	 * Post rendering complete processing.
	 * 
	 * @param tplResource
	 * @param renderedBytes
	 * @param writePath
	 */
	protected void postRenderingComplete(@NotNull TemplateResourceWrapper tplResource, @NotNull byte[] renderedBytes,
			@NotBlank String writePath) {
		notNullOf(tplResource, "tplResource");
		notNullOf(renderedBytes, "renderedBytes");
		hasTextOf(writePath, "writePath");

		// Default by write to local disk
		writeFile(new File(writePath), renderedBytes, false);
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
	 * Processing resolving template path.
	 * 
	 * @param tplResource
	 * @param model
	 * @return Return resolved template resource canonical path.
	 * @throws Exception
	 */
	protected final String resolveTemplatePath(TemplateResourceWrapper tplResource, MapRenderModel model) throws Exception {
		notNullOf(tplResource, "tplResource");
		notEmptyOf(model, "model");
		tplResource.validate();

		String pathname = tplResource.getPathname();

		// Resolve SPEL template path.
		String writeBasePath = context.getJobDir().getAbsolutePath();

		if (pathname.endsWith(DEFAULT_TPL_SUFFIX)) {
			pathname = pathname.substring(0, pathname.length() - DEFAULT_TPL_SUFFIX.length());
		}

		log.debug("Resolving template path for pathname: {}, model: {}", pathname, model);
		return writeBasePath.concat(separator).concat(spelExpr.resolve(pathname, model));

	}

	/**
	 * Processing template rendering to string.
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

		// Step2: Process internal 'has' directive.
		if (tplResource.isHasDirective() && !model.containsKey(tplResource.getHasDirectiveVar())) {
			// If the 'has' directive is enabled, but there is no corresponding
			// variable in the model, the file will not be rendered and saved.
			return null;
		}

		// Step3: Preparing rendering.
		String renderedString = preRendering(tplResource, model);
		renderedString = isBlank(renderedString) ? tplResource.getContentAsString() : renderedString; // Fallback

		// Step4: Core rendering.
		Template template = new Template(tplResource.getName(), renderedString, defaultGenConfigurer);
		renderedString = renderingTemplateToString(template, model);

		// Step5: Resolve template path.
		String writePath = resolveTemplatePath(tplResource, model);

		// Step6: Call post rendered.
		postRenderingComplete(tplResource, renderedString.getBytes(UTF_8), writePath);

		return writePath;
	}

	/**
	 * Initialization primary rendering model.
	 * 
	 * @param config
	 * @param context
	 * @param defaultFlatModel
	 * @return
	 */
	private final MapRenderModel initPrimaryRenderingModel(CodegenProperties config, GenerateContext context,
			@Nullable Map<String, Object> defaultFlatModel) {
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

}
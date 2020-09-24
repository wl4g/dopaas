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

import com.google.common.collect.Lists;
import com.wl4g.components.common.annotation.Nullable;
import com.wl4g.components.common.log.SmartLogger;
import com.wl4g.components.core.utils.expression.SpelExpressions;
import com.wl4g.devops.dts.codegen.bean.GenProject;
import com.wl4g.devops.dts.codegen.bean.GenTable;
import com.wl4g.devops.dts.codegen.config.CodegenProperties;
import com.wl4g.devops.dts.codegen.engine.context.GenerateContext;
import com.wl4g.devops.dts.codegen.engine.naming.BaseSpecs;
import com.wl4g.devops.dts.codegen.engine.template.GenTemplateLocator.RenderingResourceWrapper;
import com.wl4g.devops.dts.codegen.utils.MapRenderModel;
import freemarker.template.Template;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.List;
import java.util.Map;

import static com.wl4g.components.common.collection.Collections2.safeArray;
import static com.wl4g.components.common.io.FileIOUtils.readFullyResourceString;
import static com.wl4g.components.common.io.FileIOUtils.writeFile;
import static com.wl4g.components.common.lang.Assert2.*;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.components.common.view.Freemarkers.renderingTemplateToString;
import static com.wl4g.components.core.utils.expression.SpelExpressions.create;
import static com.wl4g.devops.dts.codegen.engine.template.ClassPathGenTemplateLocator.TPL_BASEPATH;
import static com.wl4g.devops.dts.codegen.engine.template.GenTemplateLocator.DEFAULT_TPL_SUFFIX;
import static com.wl4g.devops.dts.codegen.utils.FreemarkerUtils.defaultGenConfigurer;
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
public abstract class AbstractGeneratorProvider implements GeneratorProvider, InitializingBean {

	protected final SmartLogger log = getLogger(getClass());

	/**
	 * {@link GenerateContext}
	 */
	protected final GenerateContext context;

	/**
	 * {@link SpelExpressions}
	 */
	protected final SpelExpressions spelExpr;

	/**
	 * {@link CodegenProperties}
	 */
	@Autowired
	protected CodegenProperties config;

	/**
	 * Generate primary {@link MapRenderModel}.
	 */
	protected MapRenderModel primaryModel;

	public AbstractGeneratorProvider(@NotNull GenerateContext context, @Nullable Class<?>... spelUtilClasses) {
		this.context = notNullOf(context, "context");

		// Add SPEL utils classes
		List<Class<?>> classes = Lists.newArrayList(safeArray(Class.class, spelUtilClasses));
		classes.add(BaseSpecs.class);
		this.spelExpr = create(classes.toArray(new Class[0]));
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.primaryModel = initPrimaryRenderingModel();
	}

	@Override
	public void run() {
		try {
			doGenerate();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Handle generate project codes.
	 *
	 * @throws Exception
	 */
	protected abstract void doGenerate() throws Exception;

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
		List<RenderingResourceWrapper> tpls = context.getLocator().locate(provider);

		// Handling generate
		handleRenderingTemplates(tpls, project, context.getJobDir().getAbsolutePath());
	}

	/**
	 * Handling rendering templates generate and save.
	 *
	 * @param resources
	 * @param project
	 * @param writeBasePath
	 * @throws Exception
	 */
	protected void handleRenderingTemplates(List<RenderingResourceWrapper> resources, GenProject project, String writeBasePath)
			throws Exception {
		for (RenderingResourceWrapper res : resources) {
			log.info("Rendering generate for - {}", res.getPath());

			// Clone rendering from primary model.
			MapRenderModel model = primaryModel.clone();

			if (res.isTemplate()) {
				// Foreach generate(for example: entity/bean)
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
								.concat(resolveSpelExpression(res.getPath(), tableModel));
						String renderedString = doHandleRenderingTemplateToString(res, tableModel);

						// Call post rendered.
						postRenderingComplete(res, renderedString, writePath);
					}
				}
				// Simple template.
				else {
					// Add customization rendering model.
					customizeRenderingModel(res, model);

					// Rendering source templates.
					String writePath = writeBasePath.concat(separator).concat(resolveSpelExpression(res.getPath(), model));
					String renderedString = doHandleRenderingTemplateToString(res, model);

					// Call post rendered.
					postRenderingComplete(res, renderedString, writePath);
				}
			}
			// e.g: static resources files
			else {
				String writePath = writeBasePath.concat(separator).concat(resolveSpelExpression(res.getPath(), model));
				writeFile(new File(writePath), res.getContent(), false);
			}
		}
	}

	/**
	 * Preparing rendering processing.
	 * 
	 * @param resource
	 * @param model
	 * @return
	 */
	protected String preRendering(@NotNull RenderingResourceWrapper resource, @NotEmpty Map<String, Object> model) {
		notNullOf(resource, "resource");
		notEmptyOf(model, "model");

		// It is recommended to use FreeMarker to call Java methods.

		// // Rendering with spel ahead of time
		// //
		// // Note: After testing, we found that we should use spel to render
		// // first, otherwise FreeMarker will report an error.
		// //
		// return defaultExpressions.resolve(tpl.getFileContent(), model);
		return resource.getContent();
	}

	/**
	 * Post rendering complete processing.
	 * 
	 * @param resource
	 * @param renderedString
	 * @param writePath
	 */
	protected void postRenderingComplete(@NotNull RenderingResourceWrapper resource, @NotBlank String renderedString,
			@NotBlank String writePath) {
		notNullOf(resource, "resource");
		hasTextOf(renderedString, "renderedString");
		hasTextOf(writePath, "writePath");

		// Default by write to local disk
		writeFile(new File(writePath), renderedString, false);
	}

	/**
	 * Customize rendering model
	 *
	 * @param resource
	 * @param model
	 * @return
	 */
	protected void customizeRenderingModel(@NotNull RenderingResourceWrapper resource, @NotNull MapRenderModel model) {
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
	 * @param resource
	 * @param model
	 * @return
	 * @throws Exception
	 */
	private String doHandleRenderingTemplateToString(RenderingResourceWrapper resource, MapRenderModel model) throws Exception {
		notNullOf(resource, "resource");
		notEmptyOf(model, "model");
		resource.validate();

		log.debug("Generate rendering model: {}", model);

		// Preparing rendering
		String renderedString = preRendering(resource, model);
		renderedString = isBlank(renderedString) ? resource.getContent() : renderedString; // Fallback

		// Primary rendering
		Template template = new Template(resource.getName(), renderedString, defaultGenConfigurer);
		return renderingTemplateToString(template, model);
	}

	/**
	 * Initialization primary rendering model.
	 *
	 * @return
	 */
	private MapRenderModel initPrimaryRenderingModel() {
		MapRenderModel model = new MapRenderModel(config.isAllowRenderingCustomizeModelOverride());

		try {
			// Add rendering model of GenProject.
			model.putAll(convertToRenderingModel(context.getGenProject()));

			// Add rendering model of GenDataSource.
			Map<String, Object> datasource = convertToRenderingModel(context.getGenDataSource());
			// Gen DB version.
			datasource.put("dbVersion", context.getMetadataResolver().findDBVersion());
			model.put("datasource", datasource);

			// Add rendering model of watermark.
			model.put(MODEL_FOR_WATERMARK, MODEL_FOR_WATERMARK_VALUE);

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		return model.readonly();
	}

	// Definition of special variables.
	public static final String MODEL_FOR_WATERMARK = "watermark";
	public static final String MODEL_FOR_WATERMARK_VALUE = readFullyResourceString(TPL_BASEPATH.concat("/watermark.txt"));

}
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
import com.wl4g.components.common.reflect.ReflectionUtils2.*;
import com.wl4g.components.common.reflect.TypeUtils2;
import com.wl4g.components.core.utils.expression.SpelExpressions;
import com.wl4g.devops.dts.codegen.bean.GenProject;
import com.wl4g.devops.dts.codegen.bean.GenTable;
import com.wl4g.devops.dts.codegen.config.CodegenProperties;
import com.wl4g.devops.dts.codegen.engine.context.GenerateContext;
import com.wl4g.devops.dts.codegen.engine.naming.CSharpSpecs;
import com.wl4g.devops.dts.codegen.engine.naming.GolangSpecs;
import com.wl4g.devops.dts.codegen.engine.naming.JavaSpecs;
import com.wl4g.devops.dts.codegen.engine.naming.PythonSpecs;
import com.wl4g.devops.dts.codegen.engine.template.GenTemplateLocator.RenderingResourceWrapper;
import com.wl4g.devops.dts.codegen.utils.RenderableMapModel;
import freemarker.template.Template;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.wl4g.components.common.io.FileIOUtils.readFullyResourceString;
import static com.wl4g.components.common.io.FileIOUtils.writeFile;
import static com.wl4g.components.common.lang.Assert2.*;
import static com.wl4g.components.common.lang.StringUtils2.isTrue;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.components.common.reflect.ReflectionUtils2.*;
import static com.wl4g.components.common.view.Freemarkers.renderingTemplateToString;
import static com.wl4g.components.core.utils.expression.SpelExpressions.create;
import static com.wl4g.devops.dts.codegen.engine.template.ClassPathGenTemplateLocator.TPL_BASEPATH;
import static com.wl4g.devops.dts.codegen.engine.template.GenTemplateLocator.DEFAULT_TPL_SUFFIX;
import static com.wl4g.devops.dts.codegen.utils.FreemarkerUtils.defaultGenConfigurer;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
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
	 * {@link CodegenProperties}
	 */
	@Autowired
	protected CodegenProperties config;

	/**
	 * {@link GenerateContext}
	 */
	protected final GenerateContext context;

	/**
	 * Generate primary {@link RenderableMapModel}.
	 */
	protected RenderableMapModel primaryModel;

	public AbstractGeneratorProvider(GenerateContext context) {
		this.context = notNullOf(context, "context");
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

			if (res.isTemplate()) {
				// Foreach generate(for example: entity/bean)
				if (res.isForeachTemplate()) {
					for (GenTable tab : project.getGenTables()) {
						context.setGenTable(tab); // Set current genTable

						// When traversing the rendering table (entity), it
						// needs to share the item information and must be
						// cloned to prevent it from being covered.
						RenderableMapModel model = primaryModel.clone();

						// Add rendering model of GenTable.
						model.putAll(convertToRenderingModel(tab));

						// Add customization rendering model.
						customizeRenderingModel(res, model);

						// Rendering source templates.
						String writePath = writeBasePath.concat("/").concat(resolveSpelExpression(res.getPath(), model));
						String renderedString = doHandleRenderingTemplateToString(res, model);

						// Call post rendered.
						postRenderingComplete(res, renderedString, writePath);
					}
				}
				// Simple template.
				else {
					// Add customization rendering model.
					customizeRenderingModel(res, primaryModel);

					// Rendering source templates.
					String writePath = writeBasePath.concat("/").concat(resolveSpelExpression(res.getPath(), primaryModel));
					String renderedString = doHandleRenderingTemplateToString(res, primaryModel);

					// Call post rendered.
					postRenderingComplete(res, renderedString, writePath);
				}
			}
			// e.g: static resources files
			else {
				String targetPath = writeBasePath + "/" + resolveSpelExpression(res.getPath(), primaryModel);
				writeFile(new File(targetPath), res.getContent(), false);
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
	protected void customizeRenderingModel(@NotNull RenderingResourceWrapper resource, @NotNull RenderableMapModel model) {
	}

	/**
	 * Converting object to flat map model
	 *
	 * @param bean
	 * @return
	 * @throws Exception
	 */
	protected final Map<String, Object> convertToRenderingModel(final @NotNull Object bean,
			@Nullable final String... includeFieldNames) throws Exception {
		notNullOf(bean, "bean");
		final List<String> includes = isNull(includeFieldNames) ? emptyList() : asList(includeFieldNames);

		final Map<String, Object> model = new HashMap<>();
		doFullWithFields(bean, new FieldFilter() {
			@Override
			public boolean matches(Field field) {
				return isGenericModifier(field.getModifiers()) && includes.contains(field.getName());
			}

			@Override
			public boolean describeForObjField(Field field) {
				RenderingProperty rp = field.getDeclaredAnnotation(RenderingProperty.class);
				return nonNull(rp) && isTrue(rp.describeForObjField());
			}
		}, (field, objOfField) -> {
			if (Objects.isNull(objOfField)) {
				objOfField = TypeUtils2.instantiate(null, field.getType());
			}
			RenderingProperty rp = field.getDeclaredAnnotation(RenderingProperty.class);
			if (nonNull(rp)) {
				Object modelAttrVal = getField(field, objOfField);
				// Note: Combined with FreeMarker script, no value is saved when
				// there is no available value.
				if (!isNull(modelAttrVal) || (modelAttrVal instanceof String && !isBlank((String) modelAttrVal))) {
					model.put(field.getName(), modelAttrVal);
				}
			}
		});

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
		if (expression.endsWith(DEFAULT_TPL_SUFFIX)) {
			expression = expression.substring(0, expression.length() - DEFAULT_TPL_SUFFIX.length());
		}
		final String spelExpr = expression;
		log.debug("Resolving SPEL for expression: {}, model: {}", () -> spelExpr, () -> model);

		return defaultExpressions.resolve(spelExpr, model);
	}

	/**
	 * Do processing template rendering to string.
	 * 
	 * @param resource
	 * @param model
	 * @return
	 * @throws Exception
	 */
	private String doHandleRenderingTemplateToString(RenderingResourceWrapper resource, RenderableMapModel model)
			throws Exception {
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
	private RenderableMapModel initPrimaryRenderingModel() {
		RenderableMapModel model = new RenderableMapModel(config.isAllowRenderingCustomizeModelOverride());

		try {
			// Add rendering model of GenProject.
			model.putAll(convertToRenderingModel(context.getGenProject(), "remark"));

			// Add rendering model of GenDataSource.
			Map<String, Object> datasource = convertToRenderingModel(context.getGenDataSource());
			// Gen DB version.
			datasource.put("dbVersion", context.getMetadataResolver().findDBVersion());
			model.put("datasource", datasource);

			// Add rendering model of watermark.
			model.put(VAR_WATERMARK, TPL_WATERMARK);

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		return model.disableModifiable();
	}

	/**
	 * Whether the property fields of the annotation system bean will be
	 * serialized to the rendering model.
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version 2020-09-20
	 * @sine v1.0.0
	 * @see
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD })
	public static @interface RenderingProperty {

		/**
		 * It is used to control whether to continue to reflect the structure of
		 * the field recursively if the field traversed by reflection is of
		 * object type.
		 * 
		 * @return
		 */
		String describeForObjField() default "Yes";

	}

	// Definition of special variables.
	public static final String VAR_WATERMARK = "watermark";
	public static final String TPL_WATERMARK = readFullyResourceString(TPL_BASEPATH.concat("/watermark.txt"));

	/**
	 * {@link SpelExpressions}
	 */
	private static final SpelExpressions defaultExpressions = create(CSharpSpecs.class, GolangSpecs.class, JavaSpecs.class,
			PythonSpecs.class);

}
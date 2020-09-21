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
import com.wl4g.components.common.reflect.ReflectionUtils2.FieldFilter;
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
import com.wl4g.devops.dts.codegen.engine.template.GenTemplateLocator.TemplateWrapper;
import com.wl4g.devops.dts.codegen.utils.RenderableModelMap;

import static com.wl4g.devops.dts.codegen.engine.template.ClassPathGenTemplateLocator.TPL_BASEPATH;
import static com.wl4g.devops.dts.codegen.engine.template.GenTemplateLocator.DEFAULT_TPL_SUFFIX;

import freemarker.template.Template;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.*;

import static com.wl4g.components.common.io.FileIOUtils.readFullyResourceString;
import static com.wl4g.components.common.io.FileIOUtils.writeFile;
import static com.wl4g.components.common.lang.Assert2.*;
import static com.wl4g.components.common.lang.StringUtils2.isTrue;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.components.common.reflect.ReflectionUtils2.doFullWithFields;
import static com.wl4g.components.common.reflect.ReflectionUtils2.getField;
import static com.wl4g.components.common.reflect.ReflectionUtils2.isGenericModifier;
import static com.wl4g.components.common.view.Freemarkers.renderingTemplateToString;
import static com.wl4g.components.core.utils.expression.SpelExpressions.create;
import static com.wl4g.devops.dts.codegen.utils.FreemarkerUtils.defaultGenConfigurer;
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
public abstract class AbstractGeneratorProvider implements GeneratorProvider {

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

	public AbstractGeneratorProvider(GenerateContext context) {
		this.context = notNullOf(context, "context");
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
		List<TemplateWrapper> tpls = context.getLocator().locate(provider);

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
			RenderableModelMap primaryModel = createRenderingModel(tpl.getTplPath(), project);

			if (tpl.isTpl()) {
				// foreach template by table
				if (tpl.isForeachTpl()) {
					for (GenTable tab : project.getGenTables()) {
						// In order to avoid override replacement of the model
						// key, it must be cloned.
						RenderableModelMap model = primaryModel.clone();

						// Add table attributes model
						model.putAll(convertToRenderingModel(tab));

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
					String rendered = hasText(preRendering(tpl, primaryModel), "Pre rendering should return the rendered value");

					// Rendering with freemarker.
					String writePath = writeBasePath.concat("/").concat(resolveSpelExpression(tpl.getTplPath(), primaryModel));
					Template template = new Template(tpl.getFileName(), rendered, defaultGenConfigurer);
					rendered = renderingTemplateToString(template, primaryModel);

					// Call post rendered.
					postRendering(tpl, rendered, writePath);
				}
			}
			// e.g: static resources files
			else {
				String targetPath = writeBasePath + "/" + resolveSpelExpression(tpl.getTplPath(), primaryModel);
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
	 * Customize rendering model
	 *
	 * @param model
	 * @param tplPath
	 * @param beans
	 * @return
	 */
	protected void customizeRenderingModel(@NotNull RenderableModelMap model, @NotBlank String tplPath,
			@Nullable Object... beans) {
	}

	/**
	 * Converting object to flat map model
	 *
	 * @param object
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> convertToRenderingModel(Object object) throws Exception {
		final Map<String, Object> model = new HashMap<>();

		doFullWithFields(object, new FieldFilter() {
			@Override
			public boolean matches(Field field) {
				return isGenericModifier(field.getModifiers());
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
	 * Create rendering model
	 *
	 * @param tplPath
	 * @param project
	 * @param table
	 * @return
	 * @throws Exception
	 */
	private RenderableModelMap createRenderingModel(String tplPath, Object... innerRequiresBeans) throws Exception {
		RenderableModelMap model = new RenderableModelMap(config.isAllowRenderingCustomizeModelOverride());

		// Add requires rendering parameters.
		if (!isNull(innerRequiresBeans)) {
			for (Object bean : innerRequiresBeans) {
				model.putAll(convertToRenderingModel(bean));
			}
		}

		// Add variable of watermark.
		model.put(VAR_WATERMARK, TPL_WATERMARK);

		// Add variable of naming utils.
		model.put("javaSpecs", new JavaSpecs());
		model.put("csharpSpecs", new CSharpSpecs());
		model.put("golangSpecs", new GolangSpecs());
		model.put("pythonSpecs", new PythonSpecs());

		// Add customization model attibutes.
		customizeRenderingModel(model, tplPath, innerRequiresBeans);

		log.debug("Gen rendering model: {}", model);
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
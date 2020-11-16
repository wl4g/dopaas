/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
import com.wl4g.devops.dts.codegen.bean.GenProject;
import com.wl4g.devops.dts.codegen.engine.context.GenerateContext;
import com.wl4g.devops.dts.codegen.engine.generator.render.RenderModel;
import com.wl4g.devops.dts.codegen.engine.specs.BaseSpecs;
import com.wl4g.devops.dts.codegen.engine.template.TemplateResource;

import javax.validation.constraints.NotNull;
import java.util.Map;

import static com.wl4g.devops.dts.codegen.engine.generator.render.ModelAttributeConstants.*;
import static java.util.Locale.US;

/**
 * {@link BasedWebGeneratorProvider}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-09-20
 * @sine v1.0.0
 * @see
 */
public abstract class BasedWebGeneratorProvider extends AbstractGeneratorProvider {

	public BasedWebGeneratorProvider(@NotNull GenerateContext context, @Nullable Map<String, Object> defaultSubModels) {
		// Add model for naming utils.
		super(context, defaultSubModels);
	}

	@Override
	protected void customizeRenderingModel(@NotNull TemplateResource resource, @NotNull RenderModel model) {
		super.customizeRenderingModel(resource, model);

		// Add environment defaults attributes.
		addEnvironmentDefaultAttributes(context.getGenProject(), model);

		// Add swagger options.
		GenProject project = context.getGenProject();
		model.put(GEN_SHORTCUT_CHECK_SWAGGER,
				BaseSpecs.isConfOr(project.getExtraOptions(), "swagger.ui", "bootstrapSwagger2", "officialOas"));

		// Add IAM options.
		model.put(GEN_SHORTCUT_CHECK_IAMCLUSTER, BaseSpecs.isConf(project.getExtraOptions(), "iam.mode", "cluster"));
		model.put(GEN_SHORTCUT_CHECK_IAMLOCAL, BaseSpecs.isConf(project.getExtraOptions(), "iam.mode", "local"));
	}

	/**
	 * Add environment default model attributes.
	 * 
	 * @param project
	 * @param model
	 */
	private void addEnvironmentDefaultAttributes(GenProject project, RenderModel model) {
		String projectName = project.getProjectName().toLowerCase(US);
		String organType = project.getOrganType().toLowerCase(US);
		String organName = project.getOrganName().toLowerCase(US);
		model.put(GEN_DEF_ENTRYAPP_NAME, projectName.concat("-server"));
		model.put(GEN_DEF_ENTRYAPP_SUBDOMAIN, projectName.concat("-services"));
		model.put(GEN_DEF_ENTRYAPP_PORT, "28080");

		model.put(GEN_DEF_DEV_TOPDOMAIN, organName.concat(".debug"));
		model.put(GEN_DEF_DEV_SERVICE_HOST, projectName.concat("-services.").concat((String) model.get(GEN_DEF_DEV_TOPDOMAIN)));
		model.put(GEN_DEF_DEV_VIEW_SERVICE_HOST, projectName.concat(".").concat((String) model.get(GEN_DEF_DEV_TOPDOMAIN)));
		model.put(GEN_DEF_DEV_VIEW_SERVICE_PORT, "38080");
		model.put(GEN_DEF_DEV_REDIS_HOST, "redis.".concat(organName).concat(".debug"));
		// Automatically generated according to the data source information
		// configured by the generator.
		// model.put(GEN_DEF_DEV_MYSQL_HOST,
		// "mysql.".concat(organName).concat(".debug"));
		// model.put(GEN_DEF_DEV_ORACLE_HOST,
		// "oracle.".concat(organName).concat(".debug"));
		// model.put(GEN_DEF_DEV_POSTGRE_HOST,
		// "postgre.".concat(organName).concat(".debug"));

		model.put(GEN_DEF_FAT_TOPDOMAIN, organName.concat(".fat"));
		model.put(GEN_DEF_FAT_SERVICE_HOST, projectName.concat("-services.").concat((String) model.get(GEN_DEF_FAT_TOPDOMAIN)));
		model.put(GEN_DEF_FAT_VIEW_SERVICE_HOST, projectName.concat(".").concat((String) model.get(GEN_DEF_FAT_TOPDOMAIN)));
		model.put(GEN_DEF_FAT_REDIS_HOST, "redis.".concat(organName).concat(".fat"));
		model.put(GEN_DEF_FAT_MYSQL_HOST, "mysql.".concat(organName).concat(".fat"));
		model.put(GEN_DEF_FAT_ORACLE_HOST, "oracle.".concat(organName).concat(".fat"));
		model.put(GEN_DEF_FAT_POSTGRE_HOST, "postgre.".concat(organName).concat(".fat"));

		model.put(GEN_DEF_UAT_TOPDOMAIN, organName.concat(".uat"));
		model.put(GEN_DEF_UAT_SERVICE_HOST, projectName.concat("-services.").concat((String) model.get(GEN_DEF_UAT_TOPDOMAIN)));
		model.put(GEN_DEF_UAT_VIEW_SERVICE_HOST, projectName.concat(".").concat((String) model.get(GEN_DEF_UAT_TOPDOMAIN)));
		model.put(GEN_DEF_UAT_REDIS_HOST, "redis.".concat(organName).concat(".uat"));
		model.put(GEN_DEF_UAT_MYSQL_HOST, "mysql.".concat(organName).concat(".uat"));
		model.put(GEN_DEF_UAT_ORACLE_HOST, "oracle.".concat(organName).concat(".uat"));
		model.put(GEN_DEF_UAT_POSTGRE_HOST, "postgre.".concat(organName).concat(".uat"));

		// e.g: portal-services.mydomain.com
		model.put(GEN_DEF_PRO_TOPDOMAIN, organName.concat(".").concat(organType));
		model.put(GEN_DEF_PRO_SERVICE_HOST, projectName.concat("-services.").concat((String) model.get(GEN_DEF_PRO_TOPDOMAIN)));
		// e.g: portal.mydomain.com
		model.put(GEN_DEF_PRO_VIEW_SERVICE_HOST, projectName.concat(".").concat((String) model.get(GEN_DEF_PRO_TOPDOMAIN)));
		model.put(GEN_DEF_PRO_REDIS_HOST, "redis.".concat(organName).concat(".").concat(organType));
		// model.put(GEN_DEF_PRO_MYSQL_HOST,
		// "mysql.".concat(organName).concat(".").concat(organType));
		model.put(GEN_DEF_PRO_MYSQL_HOST, projectName.concat(".mysql.rds.aliyuncs.com"));
		model.put(GEN_DEF_PRO_ORACLE_HOST, "oracle.".concat(organName).concat(".").concat(organType));
		model.put(GEN_DEF_PRO_POSTGRE_HOST, "postgre.".concat(organName).concat(".").concat(organType));
	}

}
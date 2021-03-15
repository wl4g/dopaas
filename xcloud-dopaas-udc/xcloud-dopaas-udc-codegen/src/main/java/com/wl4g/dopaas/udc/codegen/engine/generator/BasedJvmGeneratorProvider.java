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
package com.wl4g.dopaas.udc.codegen.engine.generator;

import javax.annotation.Nullable;

import com.wl4g.dopaas.common.bean.udc.GenProject;
import com.wl4g.dopaas.common.bean.udc.GenTable;
import com.wl4g.dopaas.udc.codegen.engine.context.GenerateContext;
import com.wl4g.dopaas.udc.codegen.engine.generator.render.RenderModel;
import com.wl4g.dopaas.udc.codegen.engine.specs.JavaSpecs;
import com.wl4g.dopaas.udc.codegen.engine.template.GenTemplateResource;

import javax.validation.constraints.NotNull;

import java.util.Map;

import static com.wl4g.dopaas.common.constant.UdcConstants.ModelAttributeConstants.GEN_COMMON_JAVASPECS;
import static com.wl4g.dopaas.common.constant.UdcConstants.ModelAttributeConstants.GEN_TABLE_PACKAGENAME;
import static com.wl4g.dopaas.common.constant.UdcConstants.ModelAttributeConstants.GEN_TABLE_BEAN_SUBMODULE_PACKAGENAME;
import static com.wl4g.dopaas.common.constant.UdcConstants.ModelAttributeConstants.GEN_TABLE_DAO_SUBMODULE_PACKAGENAME;
import static com.wl4g.dopaas.common.constant.UdcConstants.ModelAttributeConstants.GEN_TABLE_SERVICE_SUBMODULE_PACKAGENAME;
import static com.wl4g.dopaas.common.constant.UdcConstants.ModelAttributeConstants.GEN_TABLE_CONTROLLER_SUBMODULE_PACKAGENAME;
import static com.wl4g.dopaas.common.constant.UdcConstants.ModelAttributeConstants.GEN_SHORTCUT_CHECK_MVNASSTAR;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * {@link BasedJvmGeneratorProvider}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-09-20
 * @sine v1.0.0
 * @see
 */
public abstract class BasedJvmGeneratorProvider extends BasedWebGeneratorProvider {

	public BasedJvmGeneratorProvider(@NotNull GenerateContext context, @Nullable Map<String, Object> defaultSubModels) {
		// Add model for naming utils.
		super(context, defaultSubModels);
	}

	@Override
	protected void customizeRenderingModel(@NotNull GenTemplateResource resource, @NotNull RenderModel model) {
		super.customizeRenderingModel(resource, model);

		// Add JavaSpecs
		model.put(GEN_COMMON_JAVASPECS, new JavaSpecs());

		GenProject project = context.getGenProject();
		GenTable table = context.getGenTable();

		// Add model for java/scala/groovy/kotlin packageName
		// e.g: {organType}.{organName}.{projectName}.{moduleName}
		// => com.mycompany.myproject.sys
		StringBuffer packageName = new StringBuffer(project.getOrganType());
		packageName.append(".").append(project.getOrganName());
		packageName.append(".").append(project.getProjectName());
		if (nonNull(table)) { // If there
			packageName.append(".").append(table.getModuleName());
		}
		// Capital letters are allowed in special cases.
		// packageName.toString().toLowerCase(US)
		model.put(GEN_TABLE_PACKAGENAME, packageName.toString());

		// Add model for sub module packageName.
		// e.g: bean.order, dao.order, service.order, controller.order
		if (nonNull(table)) { // If there
			String beanSubModulePackageName = "bean";
			String daoSubModulePackageName = "dao";
			String serviceSubModulePackageName = "service";
			String controllerSubModulePackageName = "controller";
			if (!isBlank(table.getSubModuleName())) { // Optional
				beanSubModulePackageName = beanSubModulePackageName.concat(".").concat(table.getSubModuleName());
				daoSubModulePackageName = daoSubModulePackageName.concat(".").concat(table.getSubModuleName());
				serviceSubModulePackageName = serviceSubModulePackageName.concat(".").concat(table.getSubModuleName());
				controllerSubModulePackageName = controllerSubModulePackageName.concat(".").concat(table.getSubModuleName());
			}
			model.put(GEN_TABLE_BEAN_SUBMODULE_PACKAGENAME, beanSubModulePackageName);
			model.put(GEN_TABLE_DAO_SUBMODULE_PACKAGENAME, daoSubModulePackageName);
			model.put(GEN_TABLE_SERVICE_SUBMODULE_PACKAGENAME, serviceSubModulePackageName);
			model.put(GEN_TABLE_CONTROLLER_SUBMODULE_PACKAGENAME, controllerSubModulePackageName);
		}

		// Add MvnAssTar options.
		model.put(GEN_SHORTCUT_CHECK_MVNASSTAR, JavaSpecs.isConf(project.getExtraOptions(), "build.asset-type", "MvnAssTar"));
	}

}
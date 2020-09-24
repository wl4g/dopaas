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

import static java.util.Locale.US;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import javax.validation.constraints.NotNull;

import com.wl4g.devops.dts.codegen.bean.GenProject;
import com.wl4g.devops.dts.codegen.bean.GenTable;
import com.wl4g.devops.dts.codegen.engine.context.GenerateContext;
import com.wl4g.devops.dts.codegen.engine.naming.JavaSpecs;
import com.wl4g.devops.dts.codegen.engine.template.GenTemplateLocator.TemplateResourceWrapper;
import com.wl4g.devops.dts.codegen.utils.MapRenderModel;

/**
 * {@link BasedJvmGeneratorProvider}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-09-20
 * @sine v1.0.0
 * @see
 */
public abstract class BasedJvmGeneratorProvider extends AbstractGeneratorProvider {

	public BasedJvmGeneratorProvider(@NotNull GenerateContext context) {
		// Add model for naming utils.
		super(context, new JavaSpecs());
	}

	@Override
	protected void customizeRenderingModel(@NotNull TemplateResourceWrapper resource, @NotNull MapRenderModel model) {
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
		model.put("packageName", packageName.toString().toLowerCase(US));

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
			model.put("beanSubModulePackageName", beanSubModulePackageName);
			model.put("daoSubModulePackageName", daoSubModulePackageName);
			model.put("serviceSubModulePackageName", serviceSubModulePackageName);
			model.put("controllerSubModulePackageName", controllerSubModulePackageName);
		}

	}

}

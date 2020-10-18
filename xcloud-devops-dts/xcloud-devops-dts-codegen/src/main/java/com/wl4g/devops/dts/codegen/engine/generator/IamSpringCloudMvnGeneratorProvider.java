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

import com.wl4g.devops.dts.codegen.bean.GenProject;
import com.wl4g.devops.dts.codegen.engine.context.GenerateContext;
import com.wl4g.devops.dts.codegen.engine.generator.render.RenderModel;
import com.wl4g.devops.dts.codegen.engine.specs.JavaSpecs;
import com.wl4g.devops.dts.codegen.engine.template.TemplateResource;
import static com.wl4g.devops.dts.codegen.engine.generator.render.ModelAttributeConstants.GEN_SHORTCUT_CHECK_SWAGGER;

import javax.validation.constraints.NotNull;

/**
 * Spring cloud architecture generator based on IAM system, </br>
 * It will generate includes: SpringMVC controller, service, service impl,
 * mybatis Dao/mapper.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
public class IamSpringCloudMvnGeneratorProvider extends BasedJvmGeneratorProvider {

	public IamSpringCloudMvnGeneratorProvider(@NotNull GenerateContext context) {
		super(context, null);
	}

	@Override
	public void doGenerate() throws Exception {
		doGenerateWithTemplates(GenProviderAlias.IAM_SPINGCLOUD_MVN);
	}

	@Override
	protected void customizeRenderingModel(@NotNull TemplateResource resource, @NotNull RenderModel model) {
		super.customizeRenderingModel(resource, model);

		GenProject project = context.getGenProject();
		model.put(GEN_SHORTCUT_CHECK_SWAGGER,
				JavaSpecs.isConfOr(project.getExtraOptions(), "gen.swagger.ui", "bootstrapSwagger2", "officialOas"));
	}

}
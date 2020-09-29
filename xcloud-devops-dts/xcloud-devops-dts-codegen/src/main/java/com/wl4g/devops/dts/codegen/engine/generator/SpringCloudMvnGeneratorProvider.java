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

import com.wl4g.devops.dts.codegen.engine.context.GenerateContext;
import com.wl4g.devops.dts.codegen.engine.template.GenTemplateLocator;
import com.wl4g.devops.dts.codegen.utils.MapRenderModel;

import javax.validation.constraints.NotNull;

import static com.wl4g.devops.dts.codegen.engine.specs.BaseSpecs.checkConfigured;
import static com.wl4g.devops.dts.codegen.utils.ModelAttributeDefinitions.GEN_PROJECT_EXTRA_IAM_SECURITY_MODE_CLUSTER;

/**
 * SpringMVC service, serviceImpl and controller generator.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
public class SpringCloudMvnGeneratorProvider extends BasedJvmGeneratorProvider {

    public SpringCloudMvnGeneratorProvider(@NotNull GenerateContext context) {
        super(context, null);
    }

    @Override
    public void doGenerate() throws Exception {
        doGenerateWithTemplates(GenProviderAlias.SPINGCLOUD_MVN);
    }

    @Override
    protected void customizeRenderingModel(GenTemplateLocator.@NotNull TemplateResourceWrapper resource, @NotNull MapRenderModel model) {
        super.customizeRenderingModel(resource, model);

        //check enable iam security-mode = cluster
        if (checkConfigured(context.getGenProject().getExtraOptions(), "gen.iam.security-mode", "cluster")) {
            model.put(GEN_PROJECT_EXTRA_IAM_SECURITY_MODE_CLUSTER, true);
        }


    }
}
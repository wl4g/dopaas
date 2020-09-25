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

import javax.validation.constraints.NotNull;

import static com.wl4g.devops.dts.codegen.utils.ModelAttributeDefinitions.*;
import com.wl4g.devops.dts.codegen.engine.context.GenerateContext;
import com.wl4g.devops.dts.codegen.engine.naming.AngularSpecs;
import com.wl4g.devops.dts.codegen.engine.template.GenTemplateLocator.TemplateResourceWrapper;
import com.wl4g.devops.dts.codegen.utils.MapRenderModel;

/**
 * {@link AngularJSGeneratorProvider}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
public class AngularJSGeneratorProvider extends AbstractGeneratorProvider {

	public AngularJSGeneratorProvider(@NotNull GenerateContext context) {
		super(context);
	}

	@Override
	public void doGenerate() throws Exception {
		doGenerateWithTemplates(GenProviderAlias.NGJS);
	}

	@Override
	protected void customizeRenderingModel(@NotNull TemplateResourceWrapper resource, @NotNull MapRenderModel model) {
		// Add variable of naming utils.
		model.put(GEN_COMMON_NGSPECS, new AngularSpecs());
	}

}
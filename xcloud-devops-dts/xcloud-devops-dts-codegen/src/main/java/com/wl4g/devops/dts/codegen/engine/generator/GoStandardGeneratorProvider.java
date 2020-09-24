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

import com.wl4g.devops.dts.codegen.config.CodegenProperties;
import com.wl4g.devops.dts.codegen.engine.context.GenerateContext;
import com.wl4g.devops.dts.codegen.engine.naming.GolangSpecs;
import com.wl4g.devops.dts.codegen.engine.template.GenTemplateLocator.TemplateResourceWrapper;
import com.wl4g.devops.dts.codegen.utils.MapRenderModel;

/**
 * Golang standard generator provider.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
public class GoStandardGeneratorProvider extends AbstractGeneratorProvider {

	public GoStandardGeneratorProvider(@NotNull CodegenProperties config, @NotNull GenerateContext context) {
		super(config, context, new GolangSpecs());
	}

	@Override
	public void doGenerate() throws Exception {
		processGenerateWithTemplates(GenProviderAlias.GO_STANDARD);
	}

	@Override
	protected void customizeRenderingModel(@NotNull TemplateResourceWrapper resource, @NotNull MapRenderModel model) {
		// Add variable of naming utils.
		model.put("golangSpecs", new GolangSpecs());
	}

}
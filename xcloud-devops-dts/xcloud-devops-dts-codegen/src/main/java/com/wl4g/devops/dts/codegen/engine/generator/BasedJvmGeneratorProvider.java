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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.wl4g.devops.dts.codegen.bean.GenProject;
import com.wl4g.devops.dts.codegen.engine.context.GenerateContext;
import com.wl4g.devops.dts.codegen.utils.RenderableDataModel;

/**
 * {@link BasedJvmGeneratorProvider}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-09-20
 * @sine v1.0.0
 * @see
 */
public abstract class BasedJvmGeneratorProvider extends AbstractGeneratorProvider {

	public BasedJvmGeneratorProvider(GenerateContext context) {
		super(context);
	}

	@Override
	protected void customizeRenderingModel(@NotNull RenderableDataModel model, @NotBlank String tplPath, Object... beans) {
		GenProject project = context.getGenProject();

		// Add variable of java/scala/groovy/kotlin project packageName.
		StringBuffer packageName = new StringBuffer(project.getOrganType());
		packageName.append(".").append(project.getOrganName());
		packageName.append(".").append(project.getProjectName());
		model.put("packageName", packageName.toString().toLowerCase(US));

	}

}

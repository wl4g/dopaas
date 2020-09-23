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
import com.wl4g.devops.dts.codegen.bean.GenProject;
import com.wl4g.devops.dts.codegen.bean.GenTable;
import com.wl4g.devops.dts.codegen.engine.context.GenerateContext;
import com.wl4g.devops.dts.codegen.engine.template.GenTemplateLocator;
import com.wl4g.devops.dts.codegen.utils.MapRenderModel;

import javax.validation.constraints.NotNull;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link ModularGeneratorProvider}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
public abstract class ModularGeneratorProvider extends AbstractGeneratorProvider {

	public ModularGeneratorProvider(@NotNull GenerateContext context, @Nullable Class<?>... spelUtilClasses) {
		super(context, spelUtilClasses);
	}

	@Override
	protected void customizeRenderingModel(GenTemplateLocator.@NotNull RenderingResourceWrapper resource,
			@NotNull MapRenderModel model) {

		GenProject project = context.getGenProject();
		List<GenTable> tables = project.getGenTables();

		// Target: moduleMap{moduleName => entityNames}
		Map<String, List<String>> modules = new HashMap<>();
		for (GenTable tab : tables) {
			String moduleName = tab.getModuleName();
			List<String> entityNames = modules.get(moduleName);
			if (isNull(entityNames)) {
				entityNames = new ArrayList<>();
			}
			entityNames.add(tab.getEntityName());
			modules.put(moduleName, entityNames);
		}

		model.put("moduleMap", modules);
	}

}
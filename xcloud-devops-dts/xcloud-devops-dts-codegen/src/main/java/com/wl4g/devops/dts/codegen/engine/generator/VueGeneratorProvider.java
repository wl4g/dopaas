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
import com.wl4g.devops.dts.codegen.bean.GenTable;
import com.wl4g.devops.dts.codegen.engine.context.GenerateContext;
import com.wl4g.devops.dts.codegen.engine.template.GenTemplateLocator;
import com.wl4g.devops.dts.codegen.utils.MapRenderModel;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link VueGeneratorProvider}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
public class VueGeneratorProvider extends AbstractGeneratorProvider {

	public VueGeneratorProvider(GenerateContext context) {
		super(context);
	}

	@Override
	protected void doGenerate() throws Exception {
		processGenerateWithTemplates(GenProviderAlias.VUEJS);
	}

	@Override
	protected void customizeRenderingModel(GenTemplateLocator.@NotNull RenderingResourceWrapper resource, @NotNull MapRenderModel model) {
		GenProject genProject = context.getGenProject();
		List<GenTable> genTables = genProject.getGenTables();

		Map<String,List<String>> moduleMap = new HashMap<>();
		for(GenTable genTable : genTables){
			String moduleName = genTable.getModuleName();
			List<String> entityNames = moduleMap.get(moduleName);
			if(null == entityNames){
				entityNames = new ArrayList<>();
			}
			entityNames.add(genTable.getEntityName());
			moduleMap.put(moduleName,entityNames);
		}
		model.put("moduleMap",moduleMap);
	}
}
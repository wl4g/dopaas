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
package com.wl4g.devops.dts.codegen.engine;

import com.wl4g.components.common.io.FileIOUtils;
import com.wl4g.devops.dts.codegen.bean.GenTable;
import com.wl4g.devops.dts.codegen.core.context.GenerateContext;
import com.wl4g.devops.dts.codegen.utils.FreemarkerUtils2;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * SpringMVC service, serviceImpl and controller generator.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
public class SpringMvcGeneratorProvider extends BasedBackendGeneratorProvider {

	public SpringMvcGeneratorProvider(GenerateContext context) {
		super(context);
	}

	@Override
	public void run() {
		GenTable genTable = context.getGenTable();
		try {
			String gen = FreemarkerUtils2.gen("Controller.ftl", genTable);

			File jobDir = codegenProperties.getJobDir(context.getGenTable().getId());

			String packageName = context.getGenTable().getPackageName();
			String subModuleName = context.getGenTable().getSubModuleName();
			String moduleName = context.getGenTable().getModuleName();
			String className = context.getGenTable().getClassName();
			packageName = packageName.replaceAll("\\.","/");

			File file = new File(jobDir, packageName + "/" +moduleName+ "/web/" +subModuleName+"/"+  className + "Controller.java");

			FileIOUtils.writeFile(file, gen,false);


		} catch (IOException | TemplateException e) {
			log.error("gen fail", e);
		}

	}

}
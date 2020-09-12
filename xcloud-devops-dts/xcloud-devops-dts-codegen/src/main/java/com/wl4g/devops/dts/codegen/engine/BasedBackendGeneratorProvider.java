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

import com.wl4g.devops.dts.codegen.core.context.GenerateContext;
import freemarker.template.TemplateException;

import java.io.IOException;

/**
 * {@link BasedBackendGeneratorProvider}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
public abstract class BasedBackendGeneratorProvider extends AbstractGeneratorProvider {

	public BasedBackendGeneratorProvider(GenerateContext context) {
		super(context);
	}

	void genFileToLoacl(String templateName, String demixingPackage, String suffix) throws IOException, TemplateException {
		String packageName = context.getGenTable().getPackageName();
		String subModuleName = context.getGenTable().getSubModuleName();
		String moduleName = context.getGenTable().getModuleName();
		String className = context.getGenTable().getClassName();
		packageName = packageName.replaceAll("\\.", "/");
		genFileToLoacl(templateName,
				packageName + "/" + moduleName + "/" + demixingPackage + "/" + subModuleName + "/" + className + suffix);
	}

}
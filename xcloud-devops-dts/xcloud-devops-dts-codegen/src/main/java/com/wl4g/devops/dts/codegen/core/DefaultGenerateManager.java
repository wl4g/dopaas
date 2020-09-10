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
package com.wl4g.devops.dts.codegen.core;

import com.wl4g.components.core.framework.beans.NamingPrototypeBeanFactory;
import com.wl4g.devops.dts.codegen.core.param.GenericParameter;
import com.wl4g.devops.dts.codegen.provider.GeneratorProvider;
import com.wl4g.devops.dts.codegen.provider.backend.SSMGeneratorProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.wl4g.components.common.lang.Assert2.notEmptyOf;
import static java.util.Collections.unmodifiableList;

/**
 * {@link DefaultGenerateManager}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
public class DefaultGenerateManager implements GenerateManager {

	/** {@link GeneratorProvider} */
	protected final List<GeneratorProvider> providers;

	/** {@link NamingPrototypeBeanFactory} */
	@Autowired
	protected NamingPrototypeBeanFactory beanFactory;

	public DefaultGenerateManager(List<GeneratorProvider> providers) {
		notEmptyOf(providers, "providers");
		this.providers = unmodifiableList(providers);
	}

	@Override
	public void execute(GenericParameter parameter) {
		// TODO Auto-generated method stub

		SSMGeneratorProvider ssmGeneratorProvider = beanFactory.getPrototypeBean("ssm", null);

	}

	// TODO
	// ...

}

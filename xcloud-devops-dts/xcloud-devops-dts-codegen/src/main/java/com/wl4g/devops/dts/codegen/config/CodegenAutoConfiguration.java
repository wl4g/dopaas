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
package com.wl4g.devops.dts.codegen.config;

import com.wl4g.components.core.framework.beans.NamingPrototype;
import com.wl4g.components.core.framework.beans.NamingPrototypeBeanFactory;
import com.wl4g.devops.dts.codegen.core.DefaultGenerateManager;
import com.wl4g.devops.dts.codegen.core.context.GenerateContext;
import com.wl4g.devops.dts.codegen.engine.GeneratorProvider;
import com.wl4g.devops.dts.codegen.engine.SSMGeneratorProvider;
import com.wl4g.devops.dts.codegen.engine.VueCodegenProvider;
import com.wl4g.devops.dts.codegen.engine.parse.MySQLV5xMetadataPaser;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * {@link CodegenAutoConfiguration}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
@Configuration
public class CodegenAutoConfiguration {

	@Bean
	public CodegenProperties codegenProperties() {
		return new CodegenProperties();
	}

	@Bean
	public DefaultGenerateManager defaultGenerateManager(NamingPrototypeBeanFactory beanFactory,
			List<GeneratorProvider> providers) {
		return new DefaultGenerateManager(beanFactory, providers);
	}

	// --- Generator provider's. ---

	@NamingPrototype({ BEAN_PROVIDER_SSM })
	@Bean
	public SSMGeneratorProvider ssmGeneratorProvider(GenerateContext context) {
		return new SSMGeneratorProvider(context);
	}

	@NamingPrototype({ BEAN_PROVIDER_VUE })
	@Bean
	public VueCodegenProvider vueCodegenProvider(GenerateContext context) {
		return new VueCodegenProvider(context);
	}

	@NamingPrototype({ BEAN_PARSER_MYSQL })
	@Bean
	public MySQLV5xMetadataPaser mySQLV5xMetadataPaser() {
		return new MySQLV5xMetadataPaser();
	}

	public static final String BEAN_PARSER_MYSQL = "mysql";
	public static final String BEAN_PROVIDER_VUE = "vueProvider";
	public static final String BEAN_PROVIDER_SSM = "ssmProvider";

}

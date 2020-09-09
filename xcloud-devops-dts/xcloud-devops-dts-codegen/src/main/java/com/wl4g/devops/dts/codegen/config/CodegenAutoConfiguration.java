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
import com.wl4g.devops.dts.codegen.core.DefaultGenerateManager;
import com.wl4g.devops.dts.codegen.database.MySQLV5xMetadataPaser;
import com.wl4g.devops.dts.codegen.provider.GeneratorProvider;
import com.wl4g.devops.dts.codegen.provider.backend.SSMGeneratorProvider;
import com.wl4g.devops.dts.codegen.provider.frontend.VueCodegenProvider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

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
	public DefaultGenerateManager defaultGenerateManager(List<GeneratorProvider> providers) {
		return new DefaultGenerateManager(providers);
	}

	// --- Generator Provider's. ---

	@NamingPrototype({ "ssm", "standardBackend" })
	@Bean
	public SSMGeneratorProvider ssmGeneratorProvider() {
		return new SSMGeneratorProvider();
	}

	@NamingPrototype({ "vue", "standardVue" })
	@Bean
	public VueCodegenProvider vueCodegenProvider() {
		return new VueCodegenProvider();
	}

	@NamingPrototype({ "mysqlPaser" })
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public MySQLV5xMetadataPaser mySQLV57xMetadataPaser() {
		return new MySQLV5xMetadataPaser();
	}

	// TODO
	// ...

}

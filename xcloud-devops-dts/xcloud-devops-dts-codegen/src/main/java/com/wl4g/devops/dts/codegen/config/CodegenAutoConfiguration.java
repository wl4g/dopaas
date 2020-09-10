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

import static com.wl4g.components.common.reflect.ReflectionUtils2.getFieldValues;

import com.wl4g.components.core.framework.beans.NamingPrototype;
import com.wl4g.components.core.framework.beans.NamingPrototypeBeanFactory;
import com.wl4g.components.core.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.dts.codegen.bean.GenDataSource;
import com.wl4g.devops.dts.codegen.core.DefaultGenerateManager;
import com.wl4g.devops.dts.codegen.core.context.GenerateContext;
import com.wl4g.devops.dts.codegen.engine.SSMGeneratorProvider;
import com.wl4g.devops.dts.codegen.engine.VueCodegenProvider;
import com.wl4g.devops.dts.codegen.engine.converter.DbTypeConverter;
import com.wl4g.devops.dts.codegen.engine.converter.MySQLV5xTypeConverter;
import com.wl4g.devops.dts.codegen.engine.converter.DbTypeConverter.DbConverterType;
import com.wl4g.devops.dts.codegen.engine.resolver.MySQLV5xMetadataResolver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import java.util.List;

import static com.wl4g.devops.dts.codegen.config.CodegenAutoConfiguration.GenProviderType.*;

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
	public DefaultGenerateManager defaultGenerateManager(NamingPrototypeBeanFactory beanFactory) {
		return new DefaultGenerateManager(beanFactory);
	}

	// --- Generator provider's. ---

	@Bean
	@NamingPrototype({ SSM })
	@Scope(SCOPE_PROTOTYPE)
	public SSMGeneratorProvider ssmGeneratorProvider(GenerateContext context) {
		return new SSMGeneratorProvider(context);
	}

	@Bean({ VUE })
	@Scope(SCOPE_PROTOTYPE)
	public VueCodegenProvider vueCodegenProvider(GenerateContext context) {
		return new VueCodegenProvider(context);
	}

	// --- DB metadata resolver's. ---

	@Bean({ MySQLV5xMetadataResolver.DB_TYPE })
	@Scope(SCOPE_PROTOTYPE)
	public MySQLV5xMetadataResolver mySQLV5xMetadataResolver(GenDataSource genDataSource) {
		return new MySQLV5xMetadataResolver(genDataSource);
	}

	// --- DB type converter's. ---

	@Bean
	public DbTypeConverter mySQLV5xTypeConverter() {
		return new MySQLV5xTypeConverter();
	}

	@Bean
	public GenericOperatorAdapter<DbConverterType, DbTypeConverter> dbTypeConverterAdapter(
			List<DbTypeConverter> dbTypeConverters) {
		return new GenericOperatorAdapter<DbConverterType, DbTypeConverter>(dbTypeConverters) {
		};
	}

	/**
	 * {@link GenProviderType}
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @sine v1.0.0
	 * @see
	 */
	public static interface GenProviderType {

		public static final String SSM = "ssmGenProvider";
		public static final String VUE = "vueGenProvider";

		/** Of {@link GenProviderType} fields values. */
		public static final String[] VALUES = getFieldValues(GenProviderType.class, "VALUES").toArray(new String[] {});

	}

}

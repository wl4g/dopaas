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
import com.wl4g.devops.dts.codegen.engine.AngularJSGeneratorProvider;
import com.wl4g.devops.dts.codegen.engine.MapperDaoGeneratorProvider;
import com.wl4g.devops.dts.codegen.engine.SpringMvcGeneratorProvider;
import com.wl4g.devops.dts.codegen.engine.VueGeneratorProvider;
import com.wl4g.devops.dts.codegen.engine.converter.DbTypeConverter;
import com.wl4g.devops.dts.codegen.engine.converter.MySQLV5xTypeConverter;
import com.wl4g.devops.dts.codegen.engine.converter.DbTypeConverter.DbConverterType;
import com.wl4g.devops.dts.codegen.engine.resolver.MySQLV5xMetadataResolver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
	@NamingPrototype({ MAPPER })
	public MapperDaoGeneratorProvider mapperDaoGeneratorProvider(GenerateContext context) {
		return new MapperDaoGeneratorProvider(context);
	}

	@Bean
	@NamingPrototype({ MVC })
	public SpringMvcGeneratorProvider springMvcGeneratorProvider(GenerateContext context) {
		return new SpringMvcGeneratorProvider(context);
	}

	@Bean
	@NamingPrototype({ VUEJS })
	public VueGeneratorProvider vueGeneratorProvider(GenerateContext context) {
		return new VueGeneratorProvider(context);
	}

	@Bean
	@NamingPrototype({ AGJS })
	public AngularJSGeneratorProvider angularJSGeneratorProvider(GenerateContext context) {
		return new AngularJSGeneratorProvider(context);
	}

	// --- DB metadata resolver's. ---

	@Bean
	@NamingPrototype({ MySQLV5xMetadataResolver.DB_TYPE })
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

		public static final String MAPPER = "mapperGenProvider";
		public static final String MVC = "mvcGenProvider";

		public static final String VUEJS = "vueGenProvider";
		public static final String AGJS = "agGenProvider";

		/** List of field values of class {@link GenProviderType}. */
		public static final String[] VALUES = getFieldValues(GenProviderType.class, "VALUES").toArray(new String[] {});

	}

}

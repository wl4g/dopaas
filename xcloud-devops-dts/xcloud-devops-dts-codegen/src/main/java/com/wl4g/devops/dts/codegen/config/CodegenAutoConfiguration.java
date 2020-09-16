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
import com.wl4g.components.core.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.dts.codegen.bean.GenDataSource;
import com.wl4g.devops.dts.codegen.core.DefaultGenerateManager;
import com.wl4g.devops.dts.codegen.core.context.GenerateContext;
import com.wl4g.devops.dts.codegen.engine.AngularJSGeneratorProvider;
import com.wl4g.devops.dts.codegen.engine.GoStandardGeneratorProvider;
import com.wl4g.devops.dts.codegen.engine.SpringCloudMvnGeneratorProvider;
import com.wl4g.devops.dts.codegen.engine.VueGeneratorProvider;
import com.wl4g.devops.dts.codegen.engine.converter.DbTypeConverter;
import com.wl4g.devops.dts.codegen.engine.converter.DbTypeConverter.ConverterKind;
import com.wl4g.devops.dts.codegen.engine.converter.MySQLV5TypeConverter;
import com.wl4g.devops.dts.codegen.engine.converter.OracleV11gTypeConverter;
import com.wl4g.devops.dts.codegen.engine.converter.PostgreSQLV10TypeConverter;
import com.wl4g.devops.dts.codegen.engine.resolver.MetadataResolver;
import com.wl4g.devops.dts.codegen.engine.resolver.MySQLV5MetadataResolver;
import com.wl4g.devops.dts.codegen.engine.resolver.OracleV11gMetadataResolver;
import com.wl4g.devops.dts.codegen.engine.resolver.PostgreSQLV10MetadataResolver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static com.wl4g.devops.dts.codegen.engine.GeneratorProvider.GenProviderAlias.*;
import static com.wl4g.devops.dts.codegen.engine.resolver.MetadataResolver.ResolverAlias.*;

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

	// --- Metadata resolver's. ---

	@Bean
	@NamingPrototype({ MYSQLV5 })
	public MetadataResolver mySQLV5xMetadataResolver(GenDataSource genDS) {
		return new MySQLV5MetadataResolver(genDS);
	}

	@Bean
	@NamingPrototype({ ORACLEV11G })
	public MetadataResolver oracleV11gMetadataResolver(GenDataSource genDS) {
		return new OracleV11gMetadataResolver(genDS);
	}

	@Bean
	@NamingPrototype({ POSTGRESQLV10 })
	public MetadataResolver postgreSQLV10MetadataResolver(GenDataSource genDS) {
		return new PostgreSQLV10MetadataResolver(genDS);
	}

	// --- DB type converter's. ---

	@Bean
	public DbTypeConverter mySQLV5xTypeConverter() {
		return new MySQLV5TypeConverter();
	}

	@Bean
	public DbTypeConverter oracleV11gTypeConverter() {
		return new OracleV11gTypeConverter();
	}

	@Bean
	public DbTypeConverter postgreSQLV10TypeConverter() {
		return new PostgreSQLV10TypeConverter();
	}

	@Bean
	public GenericOperatorAdapter<ConverterKind, DbTypeConverter> dbTypeConverterAdapter(List<DbTypeConverter> dbTypeConverters) {
		return new GenericOperatorAdapter<ConverterKind, DbTypeConverter>(dbTypeConverters) {
		};
	}

	// --- Generator provider's. ---

	@Bean
	@NamingPrototype({ GO_STANDARD })
	public GoStandardGeneratorProvider goStandardGeneratorProvider(GenerateContext context) {
		return new GoStandardGeneratorProvider(context);
	}

	@Bean
	@NamingPrototype({SPINGCLOUD_MVN})
	public SpringCloudMvnGeneratorProvider springMvcGeneratorProvider(GenerateContext context) {
		return new SpringCloudMvnGeneratorProvider(context);
	}

	@Bean
	@NamingPrototype({ VUEJS })
	public VueGeneratorProvider vueGeneratorProvider(GenerateContext context) {
		return new VueGeneratorProvider(context);
	}

	@Bean
	@NamingPrototype({ NGJS })
	public AngularJSGeneratorProvider angularJSGeneratorProvider(GenerateContext context) {
		return new AngularJSGeneratorProvider(context);
	}

}
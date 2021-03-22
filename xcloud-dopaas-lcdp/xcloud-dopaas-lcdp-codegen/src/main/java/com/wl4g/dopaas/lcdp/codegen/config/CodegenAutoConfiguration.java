/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.lcdp.codegen.config;

import static com.wl4g.dopaas.common.constant.LcdpConstants.GenProviderAlias.CSHARP_STANDARD;
import static com.wl4g.dopaas.common.constant.LcdpConstants.GenProviderAlias.GO_GONICWEB;
import static com.wl4g.dopaas.common.constant.LcdpConstants.GenProviderAlias.IAM_SPINGCLOUD_MVN;
import static com.wl4g.dopaas.common.constant.LcdpConstants.GenProviderAlias.IAM_VUEJS;
import static com.wl4g.dopaas.common.constant.LcdpConstants.GenProviderAlias.NGJS;
import static com.wl4g.dopaas.common.constant.LcdpConstants.GenProviderAlias.PYTHON_STANDARD;
import static com.wl4g.dopaas.common.constant.LcdpConstants.GenProviderAlias.SPINGDUBBO_MVN;
import static com.wl4g.dopaas.lcdp.codegen.engine.resolver.MetadataResolver.ResolverAlias.MYSQLV5;
import static com.wl4g.dopaas.lcdp.codegen.engine.resolver.MetadataResolver.ResolverAlias.ORACLEV11G;
import static com.wl4g.dopaas.lcdp.codegen.engine.resolver.MetadataResolver.ResolverAlias.POSTGRESQLV10;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.component.core.framework.beans.NamingPrototype;
import com.wl4g.dopaas.common.bean.lcdp.GenDataSource;
import com.wl4g.dopaas.common.constant.LcdpConstants;
import com.wl4g.dopaas.lcdp.codegen.console.CodegenConsole;
import com.wl4g.dopaas.lcdp.codegen.engine.DefaultGenerateEngineImpl;
import com.wl4g.dopaas.lcdp.codegen.engine.context.GenerateContext;
import com.wl4g.dopaas.lcdp.codegen.engine.generator.AngularJSGeneratorProvider;
import com.wl4g.dopaas.lcdp.codegen.engine.generator.CsharpStandardGeneratorProvider;
import com.wl4g.dopaas.lcdp.codegen.engine.generator.GoGonicWebGeneratorProvider;
import com.wl4g.dopaas.lcdp.codegen.engine.generator.IamSpringCloudMvnGeneratorProvider;
import com.wl4g.dopaas.lcdp.codegen.engine.generator.IamVueGeneratorProvider;
import com.wl4g.dopaas.lcdp.codegen.engine.generator.PythonStandardGeneratorProvider;
import com.wl4g.dopaas.lcdp.codegen.engine.generator.SpringDubboMvnGeneratorProvider;
import com.wl4g.dopaas.lcdp.codegen.engine.resolver.MetadataResolver;
import com.wl4g.dopaas.lcdp.codegen.engine.resolver.db.MySQLV5MetadataResolver;
import com.wl4g.dopaas.lcdp.codegen.engine.resolver.db.OracleV11gMetadataResolver;
import com.wl4g.dopaas.lcdp.codegen.engine.resolver.db.PostgreSQLV10MetadataResolver;
import com.wl4g.dopaas.lcdp.codegen.engine.template.ClassPathGenTemplateLocator;
import com.wl4g.dopaas.lcdp.codegen.engine.template.GenTemplateLocator;
import com.wl4g.dopaas.lcdp.codegen.i18n.CodegenResourceMessageBundler;

/**
 * {@link CodegenAutoConfiguration}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-07
 * @since
 */
@Configuration
public class CodegenAutoConfiguration {

	@Bean(BEAN_CODEGEN_MSG_SOURCE)
	@ConditionalOnMissingBean
	public CodegenResourceMessageBundler codegenResourceMessageBundler() {
		return new CodegenResourceMessageBundler();
	}

	@Bean
	@ConfigurationProperties(prefix = LcdpConstants.KEY_CODEGEN_PREFIX)
	public CodegenProperties codegenProperties() {
		return new CodegenProperties();
	}

	// --- Console. ---

	@Bean
	public CodegenConsole codegenConsole() {
		return new CodegenConsole();
	}

	// --- Generator Engine. ---

	@Bean
	public DefaultGenerateEngineImpl defaultGenerateEngineImpl() {
		return new DefaultGenerateEngineImpl();
	}

	// --- Metadata Resolver's. ---

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

	// --- Generator Template Locator. ---

	@Bean
	public GenTemplateLocator classPathGenTemplateLocator() {
		return new ClassPathGenTemplateLocator();
	}

	// --- Generator Provider's. ---

	@Bean
	@NamingPrototype({ GO_GONICWEB })
	public GoGonicWebGeneratorProvider goStandardGeneratorProvider(GenerateContext context) {
		return new GoGonicWebGeneratorProvider(context);
	}

	@Bean
	@NamingPrototype({ PYTHON_STANDARD })
	public PythonStandardGeneratorProvider pythonStandardGeneratorProvider(GenerateContext context) {
		return new PythonStandardGeneratorProvider(context);
	}

	@Bean
	@NamingPrototype({ CSHARP_STANDARD })
	public CsharpStandardGeneratorProvider csharpStandardGeneratorProvider(GenerateContext context) {
		return new CsharpStandardGeneratorProvider(context);
	}

	@Bean
	@NamingPrototype({ IAM_SPINGCLOUD_MVN })
	public IamSpringCloudMvnGeneratorProvider iamSpringMvcGeneratorProvider(GenerateContext context) {
		return new IamSpringCloudMvnGeneratorProvider(context);
	}

	@Bean
	@NamingPrototype({ SPINGDUBBO_MVN })
	public SpringDubboMvnGeneratorProvider springDubboMvnGeneratorProvider(GenerateContext context) {
		return new SpringDubboMvnGeneratorProvider(context);
	}

	@Bean
	@NamingPrototype({ IAM_VUEJS })
	public IamVueGeneratorProvider iamVueGeneratorProvider(GenerateContext context) {
		return new IamVueGeneratorProvider(context);
	}

	@Bean
	@NamingPrototype({ NGJS })
	public AngularJSGeneratorProvider angularJSGeneratorProvider(GenerateContext context) {
		return new AngularJSGeneratorProvider(context);
	}

	public static final String BEAN_CODEGEN_MSG_SOURCE = "codegenResourceMessageBundler";

}
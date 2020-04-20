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
package com.wl4g.devops.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.github.pagehelper.PageHelper;
import com.wl4g.devops.support.mybatis.session.MultipleSqlSessionFactoryBean;
import com.wl4g.devops.tool.common.crypto.CrypticSource;
import com.wl4g.devops.tool.common.crypto.symmetric.AESCryptor;

import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;

import static java.lang.String.format;
import static java.lang.String.valueOf;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * DataSource configuration
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月13日
 * @since
 */
@Configuration
public class DataSourceAutoConfiguration {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Value("${mybatis.typeAliasesPackage}")
	private String typeAliasesPackage;
	@Value("${mybatis.configLocation}")
	private String configLocation;
	@Value("${mybatis.mapperLocations}")
	private String mapperLocations;
	@Autowired
	private Environment env;

	@Bean
	@RefreshScope
	@ConditionalOnMissingBean
	public DruidDataSource dataSource(DruidProperties prop) {
		DruidDataSource datasource = new DruidDataSource();
		datasource.setUrl(prop.getUrl());
		datasource.setUsername(prop.getUsername());
		String plain = prop.getPassword();
		if (valueOf(env.getProperty("spring.profiles.active")).equalsIgnoreCase("prod")) {
			try {
				// TODO using dynamic cipherKey??
				byte[] cipherKey = AESCryptor.getEnvCipherKey("DEVOPS_CIPHER_KEY");
				plain = new AESCryptor().decrypt(cipherKey, CrypticSource.fromHex(prop.getPassword())).toString();
			} catch (Throwable th) {
				throw new IllegalStateException(format("Unable to decryption database password for '%s'", prop.getPassword()),
						th);
			}
		}
		datasource.setPassword(plain);
		datasource.setDriverClassName(prop.getDriverClassName());
		datasource.setInitialSize(prop.getInitialSize());
		datasource.setMinIdle(prop.getMinIdle());
		datasource.setMaxActive(prop.getMaxActive());
		datasource.setMaxWait(prop.getMaxWait());
		datasource.setTimeBetweenEvictionRunsMillis(prop.getTimeBetweenEvictionRunsMillis());
		datasource.setMinEvictableIdleTimeMillis(prop.getMinEvictableIdleTimeMillis());
		datasource.setValidationQuery(prop.getValidationQuery());
		datasource.setTestWhileIdle(prop.isTestWhileIdle());
		datasource.setTestOnBorrow(prop.isTestOnBorrow());
		datasource.setTestOnReturn(prop.isTestOnReturn());
		try {
			datasource.setFilters(prop.getFilters());
		} catch (SQLException e) {
			log.error("druid configuration initialization filter", e);
		}
		return datasource;
	}

	@Bean
	public SqlSessionFactoryBean SqlSessionFactoryBean(DataSource dataSource) throws Exception {
		// Define path matcher resolver.
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

		// SqlSessionFactory
		SqlSessionFactoryBean factory = new MultipleSqlSessionFactoryBean();
		factory.setDataSource(dataSource);
		factory.setTypeAliases(getTypeAliases(resolver));
		factory.setConfigLocation(new ClassPathResource(configLocation));

		// Page.
		PageHelper pageHelper = new PageHelper();
		Properties props = new Properties();
		props.setProperty("dialect", "mysql");
		props.setProperty("reasonable", "true");
		props.setProperty("supportMethodsArguments", "true");
		props.setProperty("returnPageInfo", "check");
		props.setProperty("params", "count=countSql");
		pageHelper.setProperties(props); // 添加插件
		factory.setPlugins(new Interceptor[] { pageHelper });

		factory.setMapperLocations(resolver.getResources(mapperLocations));
		return factory;
	}

	@Bean
	public ServletRegistrationBean druidServlet(DruidProperties prop) {
		ServletRegistrationBean reg = new ServletRegistrationBean();
		reg.setServlet(new StatViewServlet());
		reg.addUrlMappings("/druid/*");
		reg.addInitParameter("loginUsername", prop.getWebLoginUsername());
		reg.addInitParameter("loginPassword", prop.getWebLoginPassword());
		reg.addInitParameter("logSlowSql", prop.getLogSlowSql());
		return reg;
	}

	@Bean
	public FilterRegistrationBean filterRegistrationBean() {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(new WebStatFilter());
		filterRegistrationBean.addUrlPatterns("/druid/*");
		filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
		filterRegistrationBean.addInitParameter("profileEnable", "true");
		return filterRegistrationBean;
	}

	@Bean
	public DruidProperties druidProperties() {
		return new DruidProperties();
	}

	/**
	 * Let typeAliasesPackage alias bean support wildcards.
	 * 
	 * @return
	 */
	private Class<?>[] getTypeAliases(PathMatchingResourcePatternResolver resolver) throws Exception {
		List<Class<?>> typeAliases = new ArrayList<>();

		// Define metadataReader
		MetadataReaderFactory metadataReaderFty = new CachingMetadataReaderFactory(resolver);

		for (String pkg : typeAliasesPackage.split(",")) {
			// Get location
			String location = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(pkg)
					+ "**/*.class";
			// Get resources.
			Resource[] resources = resolver.getResources(location);
			if (resources != null) {
				for (Resource resource : resources) {
					if (resource.isReadable()) {
						MetadataReader metadataReader = metadataReaderFty.getMetadataReader(resource);
						typeAliases.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
					}
				}
			}
		}

		return typeAliases.toArray(new Class<?>[] {});
	}

	@ConfigurationProperties(prefix = "spring.datasource.druid")
	public static class DruidProperties {

		private String url;
		private String username;
		private String password;
		private String driverClassName;
		private int initialSize;
		private int minIdle;
		private int maxActive;
		private int maxWait;
		private int timeBetweenEvictionRunsMillis;
		private int minEvictableIdleTimeMillis;
		private String validationQuery;
		private boolean testWhileIdle;
		private boolean testOnBorrow;
		private boolean testOnReturn;
		private String filters;

		private String logSlowSql;
		private String webLoginUsername = "druid";
		private String webLoginPassword = "druid";

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getDriverClassName() {
			return driverClassName;
		}

		public void setDriverClassName(String driverClassName) {
			this.driverClassName = driverClassName;
		}

		public int getInitialSize() {
			return initialSize;
		}

		public void setInitialSize(int initialSize) {
			this.initialSize = initialSize;
		}

		public int getMinIdle() {
			return minIdle;
		}

		public void setMinIdle(int minIdle) {
			this.minIdle = minIdle;
		}

		public int getMaxActive() {
			return maxActive;
		}

		public void setMaxActive(int maxActive) {
			this.maxActive = maxActive;
		}

		public int getMaxWait() {
			return maxWait;
		}

		public void setMaxWait(int maxWait) {
			this.maxWait = maxWait;
		}

		public int getTimeBetweenEvictionRunsMillis() {
			return timeBetweenEvictionRunsMillis;
		}

		public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
			this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
		}

		public int getMinEvictableIdleTimeMillis() {
			return minEvictableIdleTimeMillis;
		}

		public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
			this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
		}

		public String getValidationQuery() {
			return validationQuery;
		}

		public void setValidationQuery(String validationQuery) {
			this.validationQuery = validationQuery;
		}

		public boolean isTestWhileIdle() {
			return testWhileIdle;
		}

		public void setTestWhileIdle(boolean testWhileIdle) {
			this.testWhileIdle = testWhileIdle;
		}

		public boolean isTestOnBorrow() {
			return testOnBorrow;
		}

		public void setTestOnBorrow(boolean testOnBorrow) {
			this.testOnBorrow = testOnBorrow;
		}

		public boolean isTestOnReturn() {
			return testOnReturn;
		}

		public void setTestOnReturn(boolean testOnReturn) {
			this.testOnReturn = testOnReturn;
		}

		public String getFilters() {
			return filters;
		}

		public void setFilters(String filters) {
			this.filters = filters;
		}

		public String getLogSlowSql() {
			return logSlowSql;
		}

		public void setLogSlowSql(String logSlowSql) {
			this.logSlowSql = logSlowSql;
		}

		public String getWebLoginUsername() {
			return webLoginUsername;
		}

		public void setWebLoginUsername(String webLoginUsername) {
			this.webLoginUsername = webLoginUsername;
		}

		public String getWebLoginPassword() {
			return webLoginPassword;
		}

		public void setWebLoginPassword(String webLoginPassword) {
			this.webLoginPassword = webLoginPassword;
		}

	}

	static {
		// com.alibaba.druid.support.logging.LogFactory.static{}
		System.setProperty("druid.logType", "slf4j");
	}

}
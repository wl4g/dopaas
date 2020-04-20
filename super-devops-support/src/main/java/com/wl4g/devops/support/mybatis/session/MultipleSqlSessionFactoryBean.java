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
package com.wl4g.devops.support.mybatis.session;

import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.util.Objects.nonNull;
import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.getField;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.core.io.Resource;

import com.wl4g.devops.support.mybatis.logging.LogbackImpl;
import com.wl4g.devops.tool.common.log.SmartLogger;

/**
 * Multiple DB connection composite sqlSessionFactory.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年2月24日
 * @since
 */
public class MultipleSqlSessionFactoryBean extends SqlSessionFactoryBean {

	final protected SmartLogger log = getLogger(getClass());

	@Override
	protected SqlSessionFactory buildSqlSessionFactory() throws IOException {
		applyDefaultConfiguration();
		return super.buildSqlSessionFactory();
	}

	/**
	 * Apply setting default configuration.
	 * 
	 * <pre>
	 * mybatis-config.xml:
	 * 
	 * &lt;?xml version="1.0" encoding="UTF-8" ?&gt;
	 * &lt;!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-config.dtd">
	 * &lt;configuration&gt;
	 *   &lt;settings&gt;
	 *     &lt;!-- LOGBACK|SLF4J|LOG4J|LOG4J2|JDK_LOGGING|COMMONS_LOGGING|STDOUT_LOGGING|NO_LOGGING --&gt;
	 *     &lt;setting name="logImpl" value="LOGBACK" /&gt;
	 *     ...
	 *   &lt;/settings&gt;
	 * &lt;/configuration&gt;
	 * </pre>
	 * 
	 * @throws IOException
	 */
	private void applyDefaultConfiguration() throws IOException {
		Resource configLocation = (Resource) getField(configLocationField, this);
		if (nonNull(configLocation)) {
			Properties props = (Properties) getField(configPropertiesField, this);
			XMLConfigBuilder builder = new XMLConfigBuilder(configLocation.getInputStream(), null, props);
			Configuration config = builder.getConfiguration();
			// Register support for logback.
			config.getTypeAliasRegistry().registerAlias("LOGBACK", LogbackImpl.class);
			setConfiguration(config);
		}
	}

	final public static Field configPropertiesField;
	final public static Field configLocationField;

	static {
		configPropertiesField = findField(SqlSessionFactoryBean.class, "configurationProperties", Properties.class);
		configLocationField = findField(SqlSessionFactoryBean.class, "configLocation", Resource.class);
		configPropertiesField.setAccessible(true);
		configLocationField.setAccessible(true);
	}

}

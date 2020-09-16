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
package com.wl4g.devops.dts.codegen.utils;

import com.wl4g.components.common.log.SmartLogger;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;

import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;

/**
 * {@link FreemarkerUtils2}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @author vjay
 * @version v1.0 2020-09-10
 * @since
 */
public abstract class FreemarkerUtils2 {
	protected static final SmartLogger log = getLogger(FreemarkerUtils2.class);

	public final static Configuration defaultGenConfigurer = configuration();

	/**
	 * freemarker configuration
	 */
	private static Configuration  configuration() {
		Configuration configuration = new Configuration(Configuration.VERSION_2_3_27);
		StringTemplateLoader templateLoader = new StringTemplateLoader();
		configuration.setTemplateLoader(templateLoader);
		configuration.setDefaultEncoding("UTF-8");
		return configuration;
	}

}
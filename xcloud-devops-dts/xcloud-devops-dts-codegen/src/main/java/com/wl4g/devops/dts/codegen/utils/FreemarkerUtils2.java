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
import com.wl4g.components.common.view.Freemarkers;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;

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

	public static String gen(String templatePath, Object model) throws IOException, TemplateException {
		Template template = defaultGenConfigurer.getTemplate(templatePath, UTF_8.name());
		return processTemplateIntoString(template, model);
	}

	private final static String TPL_BASE_PATH = "/ftl/";
	private final static Configuration defaultGenConfigurer = Freemarkers.create(TPL_BASE_PATH).build();

}
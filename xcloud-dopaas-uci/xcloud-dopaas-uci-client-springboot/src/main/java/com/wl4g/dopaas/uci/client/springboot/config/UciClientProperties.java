/*
 * Copyright (C) 2017 ~ 2025 the original author or authors.
 * <Wanglsir@gmail.com, 983708408@qq.com> Technology CO.LTD.
 * All rights reserved.
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
 * 
 * Reference to website: http://wl4g.com
 */
package com.wl4g.dopaas.uci.client.springboot.config;

import static com.wl4g.dopaas.common.constant.UciConstants.DEFAULT_META_HEADER_NAME;
import static com.wl4g.dopaas.common.constant.UciConstants.DEFAULT_META_NAME;
import static java.lang.String.format;

import com.wl4g.component.core.utils.context.SpringContextHolder;

import lombok.Getter;
import lombok.Setter;

/**
 * {@link UciClientProperties}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2021-04-20
 * @sine v1.0
 * @see
 */
@Getter
@Setter
public class UciClientProperties {

	private String metaInfoHeaderName = DEFAULT_META_HEADER_NAME;
	private String defaultMetaFile = format("/opt/apps/acm/%s-package/%s-master-bin/%s", getAppName0(), DEFAULT_META_NAME);

	private final String getAppName0() {
		return SpringContextHolder.getApplicationContext().getEnvironment().getProperty("spring.application.name", "");
	}

}

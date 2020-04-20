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
package com.wl4g.devops.iam.filter;

import com.wl4g.devops.iam.common.annotation.IamFilter;
import com.wl4g.devops.iam.common.config.AbstractIamProperties;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties;
import com.wl4g.devops.iam.common.filter.AbstractWhiteListInternalAuthenticationFilter;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_BASE;

import com.wl4g.devops.common.kit.access.IPAccessControl;

/**
 * Interactive authentication processing filter for internal and application
 * services
 * <p>
 * {@link org.apache.shiro.web.filter.authz.HostFilter}
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月30日
 * @since
 */
@IamFilter
public class InternalWhiteListServerAuthenticationFilter extends AbstractWhiteListInternalAuthenticationFilter {
	final public static String NAME = "server-internal";

	public InternalWhiteListServerAuthenticationFilter(IPAccessControl control,
			AbstractIamProperties<? extends ParamProperties> config) {
		super(control, config);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getUriMapping() {
		return URI_S_BASE + "/**";
	}

}
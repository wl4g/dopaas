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
package com.wl4g.devops.coss;

import static com.wl4g.devops.components.tools.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;

import org.springframework.beans.factory.InitializingBean;

import com.wl4g.devops.common.framework.operator.Operator;
import com.wl4g.devops.components.tools.common.log.SmartLogger;
import com.wl4g.devops.coss.common.CossEndpoint;
import com.wl4g.devops.coss.common.CossEndpoint.CossProvider;
import com.wl4g.devops.coss.common.model.Owner;

/**
 * Abstract composite object storage server file system API.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年2月28日
 * @since
 */
public abstract class ServerCossEndpoint<C> implements CossEndpoint, Operator<CossProvider>, InitializingBean {

	final protected SmartLogger log = getLogger(getClass());

	/**
	 * {@link C}
	 */
	final protected C config;

	public ServerCossEndpoint(C config) {
		notNullOf(config, "cossProperties");
		this.config = config;
	}

	/**
	 * Gets current session user owner. If the Iam service is activated, get the
	 * current login user of Iam.
	 * 
	 * @return
	 */
	protected Owner getCurrentOwner() {
		// try {
		// IamPrincipalInfo info = IamSecurityHolder.getPrincipalInfo();
		// return new Owner(info.getPrincipalId(), info.getPrincipal());
		// } catch (Exception e) {
		// log.warn("Unable gets IamPrincipal, cause by: {}", e.getMessage());
		// }
		// TODO
		return new Owner(null, null);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
	}

}
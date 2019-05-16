/*
 * Copyright 2015 the original author or authors.
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
package com.wl4g.devops.scm.common.utils;

import com.wl4g.devops.common.bean.scm.model.BaseModel;
import com.wl4g.devops.common.bean.scm.model.BaseModel.ReleaseInstance;
import com.wl4g.devops.common.constants.SCMDevOpsConstants;

public final class ScmUtils {

	/**
	 * Generate Zookeeper configuration path.
	 * 
	 * @param base
	 *            Basic release information.
	 * @param hostPortString
	 *            Host and port(host:port) information.
	 * @return
	 */
	public final static String genZkConfigPath(BaseModel base, ReleaseInstance instance) {
		checkInvalid(base, instance);
		StringBuffer path = new StringBuffer(SCMDevOpsConstants.CONF_DISCOVERY_ROOT);
		path.append("/");
		path.append(base.getApplication());
		path.append("/");
		path.append(base.getProfile());
		path.append("/");
		path.append(instance.toString());
		return path.toString();
	}

	private final static void checkInvalid(BaseModel base, ReleaseInstance instance) {
		base.validation(false, false);
		instance.validation();
	}

}
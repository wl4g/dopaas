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
package com.wl4g.devops.iam.common.utils;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_FAIL_LIMIT_RIP_PREFIX;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_FAIL_LIMIT_UID_PREFIX;
import static org.apache.commons.codec.binary.Hex.encodeHexString;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.List;

/**
 * Multiple-factor risk identification control security utility.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年3月19日 v1.0.0
 */
public abstract class RiskControlSecurityUtils extends AuthenticatingUtils {

	//
	// TODO Using risk service??
	//

	/**
	 * Gets version1 risk factor(e.g. Client remote IP and loginId)
	 *
	 * @param remoteHost
	 * @param uid
	 * @return
	 */
	public static List<String> getV1Factors(String remoteHost, String uid) {
		return new ArrayList<String>(2) {
			private static final long serialVersionUID = -5976569540781454836L;
			{
				String uidFactor = getUIDFactor(uid);
				if (isNotBlank(uidFactor)) {
					add(uidFactor);
				}
				String hostFactor = getIpFactor(remoteHost);
				if (isNotBlank(hostFactor)) {
					add(hostFactor);
				}
			}
		};
	}

	/**
	 * Gets remote IP limit factor.
	 *
	 * @param remoteHost
	 * @return
	 */
	public static String getIpFactor(String remoteHost) {
		return isNotBlank(remoteHost) ? (KEY_FAIL_LIMIT_RIP_PREFIX + encodeHexString(remoteHost.getBytes(UTF_8))) : EMPTY;
	}

	/**
	 * Gets login UID limit factor.
	 *
	 * @param uid
	 * @return
	 */
	public static String getUIDFactor(String uid) {
		return isNotBlank(uid) ? (KEY_FAIL_LIMIT_UID_PREFIX + uid) : EMPTY;
	}

}

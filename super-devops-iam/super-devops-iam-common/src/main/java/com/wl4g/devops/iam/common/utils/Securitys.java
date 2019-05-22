package com.wl4g.devops.iam.common.utils;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Charsets.*;
import static org.apache.commons.codec.binary.Hex.*;
import static org.apache.commons.lang3.StringUtils.*;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.*;

/**
 * IAM securiy utiliy tools.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月21日
 * @since
 */
public abstract class Securitys {

	/**
	 * Safety limiting factor(e.g. Client remote IP and login user-name)
	 * 
	 * @param remoteHost
	 * @param principal
	 * @return
	 */
	public static List<String> createFactors(String remoteHost, String principal) {
		return new ArrayList<String>(2) {
			private static final long serialVersionUID = -5976569540781454836L;
			{
				String principalFactor = createPrincipalFactor(principal);
				if (isNotBlank(principalFactor)) {
					add(principalFactor);
				}

				String hostFactor = createHostFactor(remoteHost);
				if (isNotBlank(hostFactor)) {
					// add(hostFactor);

					// for test::
					add(KEY_FAIL_LIMITER_RIP_PREFIX + System.currentTimeMillis());
				}
			}
		};
	}

	/**
	 * Create limit remote host factor.
	 * 
	 * @param remoteHost
	 * @return
	 */
	public static String createHostFactor(String remoteHost) {
		return isNotBlank(remoteHost) ? (KEY_FAIL_LIMITER_RIP_PREFIX + encodeHexString(remoteHost.getBytes(UTF_8))) : null;
	}

	/**
	 * Create limit login principal factor.
	 * 
	 * @param principal
	 * @return
	 */
	public static String createPrincipalFactor(String principal) {
		return isNotBlank(principal) ? (KEY_FAIL_LIMITER_USER_PREFIX + principal) : null;
	}

}

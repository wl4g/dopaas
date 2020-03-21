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
package com.wl4g.devops.iam.config.properties;

import java.util.ArrayList;
import java.util.List;

import static com.wl4g.devops.iam.config.properties.ServerParamProperties.DefaultRiskControlKey.*;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties;

/**
 * IAM server parameters configuration properties
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月29日
 * @since
 */
public class ServerParamProperties extends ParamProperties {
	private static final long serialVersionUID = 3258460473711285504L;

	/**
	 * Account parameter name at login time of account password.
	 */
	private String principalName = "principal";

	/**
	 * Password parameter name at login time of account password.
	 */
	private String credentialName = "credential";

	/**
	 * The secret public key that the client requests for authentication is used
	 * to login successfully encrypted additional ticket.
	 */
	private String clientPubKeySecretName = "clientSecret";

	/**
	 * When the authentication is successful, the access token will be returned.
	 * The client then uses sessionid + accesstoken as the credential to access
	 * the business API.
	 */
	private String clientSecretTokenName = "secretToken";

	/**
	 * Client type reference parameter name at login time of account password.
	 */
	private String clientRefName = "client_ref";

	/**
	 * Verification verifiedToken parameter name.
	 */
	private String verifiedTokenName = "verifiedToken";

	/**
	 * Dynamic verification code operation action type parameter key-name.
	 */
	private String smsActionName = "action";

	/**
	 * Risk identification control dynamic parameters.
	 * 
	 * @see {@link com.wl4g.devops.iam.authc.GenericAuthenticationToken#userProperties}
	 * @see <a href=
	 *      "https://github.com/Valve/fingerprintjs2/wiki/Stable-components">Fingerprintjs2-Stable-components</a>
	 */
	private List<String> requiredRiskControlParams = new ArrayList<String>() {
		private static final long serialVersionUID = -8690288151434386891L;
		{
			add(KEY_USERAGENT);
			add(KEY_PLATFORM);
			add(KEY_PIXEL_RATIO);
			add(KEY_TIMEZONE);
			add(KEY_LANGUAGE);
			add(KEY_DEVICE_MEMORY);
			add(KEY_CPU_CLASS);
			add(KEY_TOUCHSUPPORT);
			add(KEY_AVAILABLE_SCREEN_RESOLUTION);
		}
	};

	/**
	 * Risk identification control dynamic parameters.
	 * 
	 * @see {@link com.wl4g.devops.iam.authc.GenericAuthenticationToken#userProperties}
	 * @see <a href=
	 *      "https://github.com/Valve/fingerprintjs2/wiki/Stable-components">Fingerprintjs2-Stable-components</a>
	 */
	private List<String> optionalRiskControlParams = new ArrayList<String>() {
		private static final long serialVersionUID = -8690288151434386891L;
		{
			add(KEY_WEB_CANVAS);
			add(KEY_WEB_WEBGL);
			add(KEY_WEB_INDEXEDDB);
			add(KEY_WEB_SESSIONSTORAGE);
			add(KEY_WEB_LOCALSTORAGE);
			add(KEY_COLOR_DEPTH);
		}
	};

	public String getPrincipalName() {
		return principalName;
	}

	public void setPrincipalName(String loginUsername) {
		this.principalName = loginUsername;
	}

	public String getCredentialName() {
		return credentialName;
	}

	public void setCredentialName(String loginPassword) {
		this.credentialName = loginPassword;
	}

	public String getClientPubKeySecretName() {
		return clientPubKeySecretName;
	}

	public void setClientPubKeySecretName(String signatureName) {
		this.clientPubKeySecretName = signatureName;
	}

	public String getClientSecretTokenName() {
		return clientSecretTokenName;
	}

	public void setClientSecretTokenName(String accessTokenName) {
		this.clientSecretTokenName = accessTokenName;
	}

	public String getClientRefName() {
		return clientRefName;
	}

	public void setClientRefName(String clientRefName) {
		this.clientRefName = clientRefName;
	}

	public String getVerifiedTokenName() {
		return verifiedTokenName;
	}

	public void setVerifiedTokenName(String verifiedTokenName) {
		this.verifiedTokenName = verifiedTokenName;
	}

	public String getSmsActionName() {
		return smsActionName;
	}

	public void setSmsActionName(String smsActionName) {
		this.smsActionName = smsActionName;
	}

	public List<String> getRequiredRiskControlParams() {
		return requiredRiskControlParams;
	}

	public void setRequiredRiskControlParams(List<String> requiredRiskControlParams) {
		this.requiredRiskControlParams = requiredRiskControlParams;
	}

	public List<String> getOptionalRiskControlParams() {
		return optionalRiskControlParams;
	}

	public void setOptionalRiskControlParams(List<String> optionalRiskControlParams) {
		this.optionalRiskControlParams = optionalRiskControlParams;
	}

	/**
	 * User properties keys definitions.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020年3月19日
	 * @since
	 * @see <a href=
	 *      "https://github.com/Valve/fingerprintjs2/wiki/Stable-components">Fingerprintjs2-Stable-components</a>
	 */
	public static interface DefaultRiskControlKey {

		//
		// --- REQUIRED risk identification control parameters deinition. ---
		//

		/**
		 * Native userAgent string. </br>
		 * 
		 * <pre>
		 * e.g. 
		 * userAgent=Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.132 Safari/537.36)
		 * </pre>
		 */
		final public static String KEY_USERAGENT = "userAgent";

		/**
		 * Native OS platform string. (e.g. platform=win32)
		 */
		final public static String KEY_PLATFORM = "platform";

		/**
		 * Client screen pixel. (e.g. pixelRatio=1366x768)
		 */
		final public static String KEY_PIXEL_RATIO = "pixelRatio";

		/**
		 * Web browser client e.g: fingerprint2.timezone.
		 */
		final public static String KEY_TIMEZONE = "timezone";

		/**
		 * Web browser client e.g: fingerprint2.language.
		 */
		final public static String KEY_LANGUAGE = "language";

		/**
		 * Web browser client e.g: fingerprint2.deviceMemory.
		 */
		final public static String KEY_DEVICE_MEMORY = "deviceMemory";

		/**
		 * Web browser client e.g: fingerprint2.cpuClass.
		 */
		final public static String KEY_CPU_CLASS = "cpuClass";

		/**
		 * Web browser client e.g: fingerprint2.touchSupport.
		 */
		final public static String KEY_TOUCHSUPPORT = "touchSupport";

		/**
		 * Web browser client e.g: fingerprint2.availableScreenResolution.
		 */
		final public static String KEY_AVAILABLE_SCREEN_RESOLUTION = "availableScreenResolution";

		//
		// --- OPTIONAL risk identification control parameters deinition. ---
		//

		/**
		 * Web browser client e.g: fingerprint2.canvas.
		 */
		final public static String KEY_WEB_CANVAS = "canvas";

		/**
		 * Web browser client e.g: fingerprint2.webgl.
		 */
		final public static String KEY_WEB_WEBGL = "webgl";

		/**
		 * Web browser client e.g: fingerprint2.indexedDb.
		 */
		final public static String KEY_WEB_INDEXEDDB = "indexedDb";

		/**
		 * Web browser client e.g: fingerprint2.sessionStorage.
		 */
		final public static String KEY_WEB_SESSIONSTORAGE = "sessionStorage";

		/**
		 * Web browser client e.g: fingerprint2.localStorage.
		 */
		final public static String KEY_WEB_LOCALSTORAGE = "localStorage";

		/**
		 * Web browser client e.g: fingerprint2.colorDepth.
		 */
		final public static String KEY_COLOR_DEPTH = "colorDepth";

	}

}
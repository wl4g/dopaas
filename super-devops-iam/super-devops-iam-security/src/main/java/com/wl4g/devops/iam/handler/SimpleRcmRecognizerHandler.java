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
package com.wl4g.devops.iam.handler;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.*;
import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.util.Collections.unmodifiableList;
import static java.util.Locale.US;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.devops.iam.common.cache.EnhancedCache;
import com.wl4g.devops.iam.common.cache.EnhancedCacheManager;
import com.wl4g.devops.iam.common.cache.EnhancedKey;
import com.wl4g.devops.iam.config.properties.IamProperties;
import com.wl4g.devops.iam.configure.ServerSecurityConfigurer;
import com.wl4g.devops.iam.configure.ServerSecurityCoprocessor;
import com.wl4g.devops.tool.common.log.SmartLogger;

/**
 * {@link SimpleRcmRecognizerHandler}
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月25日
 * @since
 */
public class SimpleRcmRecognizerHandler implements InitializingBean {

	final protected SmartLogger log = getLogger(getClass());

	/**
	 * IAM server configuration properties
	 */
	@Autowired
	protected IamProperties config;

	/**
	 * {@link EnhancedCacheManager}
	 */
	@Autowired
	protected EnhancedCacheManager cacheManager;

	/**
	 * IAM security context handler
	 */
	@Autowired
	protected ServerSecurityConfigurer configurer;

	/**
	 * IAM server security processor
	 */
	@Autowired
	protected ServerSecurityCoprocessor coprocessor;

	/**
	 * {@link EnhancedCache}
	 */
	protected EnhancedCache umidCache;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.umidCache = cacheManager.getEnhancedCache(CACHE_SIMPLE_RCM_UMIDTOKEN);
	}

	/**
	 * Apply UMID token string.</br>
	 * [Note]: Simple UMID generate implements, Please use external professional
	 * risk control engine for production environment
	 * 
	 * @param params
	 * @return
	 */
	public String applyUmidToken(@NotNull Map<String, String> params) {
		log.info("Apply UMID token of parameters: {}", params);

		// Generate UMID from scene dimension parameters.
		List<String> values = params.values().stream().collect(toList());
		Collections.sort(values, (v1, v2) -> v1.compareTo(v2));
		StringBuffer valuesStr = new StringBuffer(values.size() * 10);
		values.forEach(v -> valuesStr.append(v));

		// Digest to umid
		String umid = sha256Hex(valuesStr.toString());
		// Generate umidToken.
		String umidToken = randomAlphanumeric(59).toUpperCase(US);
		// Storage umidToken=>umid
		umidCache.put(new EnhancedKey(umidToken, 60_000), umid);

		log.info("Created umidToken: {}, umid: {}", umidToken, umid);
		return umidToken;
	}

	/**
	 * Gets safety evaluation from umidToken.
	 * 
	 * @param umidToken
	 * @return
	 */
	public double getEvaluation(@NotBlank String umidToken) {
		return 9d; // TODO no implements
	}

	/**
	 * User properties items definitions.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020年3月19日
	 * @since
	 * @see <a href=
	 *      "https://github.com/Valve/fingerprintjs2/wiki/Stable-components">Fingerprintjs2-Stable-components</a>
	 */
	public static enum DefaultRegcognizeItems {

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
		KEY_USERAGENT("userAgent", true),

		/**
		 * Native OS platform string. (e.g. platform=win32)
		 */
		KEY_PLATFORM("platform", true),

		/**
		 * Client screen pixel. (e.g. pixelRatio=1366x768)
		 */
		KEY_PIXEL_RATIO("pixelRatio", true),

		/**
		 * Web browser client e.g: fingerprint2.timezone.
		 */
		KEY_TIMEZONE("timezone", true),

		/**
		 * Web browser client e.g: fingerprint2.language.
		 */
		KEY_LANGUAGE("language", true),

		/**
		 * Web browser client e.g: fingerprint2.deviceMemory.
		 */
		KEY_DEVICE_MEMORY("deviceMemory", true),

		/**
		 * Web browser client e.g: fingerprint2.cpuClass.
		 */
		KEY_CPU_CLASS("cpuClass", true),

		/**
		 * Web browser client e.g: fingerprint2.touchSupport.
		 */
		KEY_TOUCHSUPPORT("touchSupport", true),

		/**
		 * Web browser client e.g: fingerprint2.availableScreenResolution.
		 */
		KEY_AVAILABLE_SCREEN_RESOLUTION("availableScreenResolution", true),

		//
		// --- OPTIONAL risk identification control parameters deinition. ---
		//

		/**
		 * Web browser headers.referer
		 */
		KEY_WEB_REFERER("referer", false),

		/**
		 * Web browser client e.g: fingerprint2.canvas.
		 */
		KEY_WEB_CANVAS("canvas", false),

		/**
		 * Web browser client e.g: fingerprint2.webgl.
		 */
		KEY_WEB_WEBGL("webgl", false),

		/**
		 * Web browser client e.g: fingerprint2.indexedDb.
		 */
		KEY_WEB_INDEXEDDB("indexedDb", false),

		/**
		 * Web browser client e.g: fingerprint2.sessionStorage.
		 */
		KEY_WEB_SESSIONSTORAGE("sessionStorage", false),

		/**
		 * Web browser client e.g: fingerprint2.localStorage.
		 */
		KEY_WEB_LOCALSTORAGE("localStorage", false),

		/**
		 * Web browser client e.g: fingerprint2.colorDepth.
		 */
		KEY_COLOR_DEPTH("colorDepth", false);

		/**
		 * Risk identification control dynamic parameters.
		 * 
		 * @see <a href=
		 *      "https://github.com/Valve/fingerprintjs2/wiki/Stable-components">Fingerprintjs2-Stable-components</a>
		 */
		final public static List<String> REQUIRED_PARAMS = unmodifiableList(new ArrayList<String>() {
			private static final long serialVersionUID = -8690288151434386891L;
			{
				for (DefaultRegcognizeItems key : DefaultRegcognizeItems.values()) {
					if (key.isRequired()) {
						add(key.getParamName());
					}
				}
			}
		});

		/**
		 * Risk identification control dynamic parameters.
		 * 
		 * @see <a href=
		 *      "https://github.com/Valve/fingerprintjs2/wiki/Stable-components">Fingerprintjs2-Stable-components</a>
		 */
		final public static List<String> OPTIONAL_PARAMS = unmodifiableList(new ArrayList<String>() {
			private static final long serialVersionUID = -8690288151434386891L;
			{
				for (DefaultRegcognizeItems key : DefaultRegcognizeItems.values()) {
					if (!key.isRequired()) {
						add(key.getParamName());
					}
				}
			}
		});

		/**
		 * User custom properties parameters name.
		 */
		final private String paramName;

		/**
		 * Is required parameter?
		 */
		final private boolean required;

		private DefaultRegcognizeItems(String paramName, boolean required) {
			notNullOf(paramName, "paramName");
			this.paramName = paramName;
			this.required = required;
		}

		public String getParamName() {
			return paramName;
		}

		public boolean isRequired() {
			return required;
		}

	}

}

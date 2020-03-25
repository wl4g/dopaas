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

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.CACHE_SIMPLE_RCM_UMIDTOKEN;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.util.Locale.US;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	 * @param requiredParams
	 * @param optionalParams
	 * @return
	 */
	public String applyUmidToken(Map<String, String> requiredParams, Map<String, String> optionalParams) {
		// Merge parameters
		Map<String, String> merge = new HashMap<>(requiredParams);
		merge.putAll(optionalParams);
		log.info("Apply UMID token of parameters: {}", merge);

		// Generate UMID from scene dimension parameters.
		List<String> values = merge.values().stream().collect(toList());
		Collections.sort(values, (v1, v2) -> v1.compareTo(v2));
		StringBuffer valuesStr = new StringBuffer(values.size() * 10);
		values.forEach(v -> valuesStr.append(v));

		// Digest to umid
		String umid = sha256Hex(valuesStr.toString());
		// Generate umidToken.
		String umidToken = randomAlphanumeric(32).toUpperCase(US);
		// Storage umidToken=>umid
		umidCache.put(new EnhancedKey(umidToken), umid);

		log.info("Generated umidToken: {}, umid: {}", umidToken, umid);
		return umidToken;
	}

}

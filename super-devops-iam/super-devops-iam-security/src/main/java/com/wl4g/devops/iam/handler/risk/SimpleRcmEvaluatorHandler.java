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
package com.wl4g.devops.iam.handler.risk;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.*;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.util.Collections.sort;
import static java.util.Locale.US;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.devops.iam.common.cache.IamCache;
import com.wl4g.devops.iam.common.cache.IamCacheManager;
import com.wl4g.devops.iam.common.cache.CacheKey;
import com.wl4g.devops.iam.config.properties.IamProperties;
import com.wl4g.devops.iam.configure.ServerSecurityConfigurer;
import com.wl4g.devops.iam.configure.ServerSecurityCoprocessor;
import com.wl4g.devops.tool.common.log.SmartLogger;

/**
 * {@link SimpleRcmEvaluatorHandler}
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月25日
 * @since
 */
public class SimpleRcmEvaluatorHandler implements RiskEvaluatorHandler, InitializingBean {

	final protected SmartLogger log = getLogger(getClass());

	/**
	 * IAM server configuration properties
	 */
	@Autowired
	protected IamProperties config;

	/**
	 * {@link IamCacheManager}
	 */
	@Autowired
	protected IamCacheManager cacheManager;

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
	 * {@link IamCache}
	 */
	protected IamCache umidCache;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.umidCache = cacheManager.getIamCache(CACHE_SIMPLE_RCM_UMIDTOKEN);
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
		sort(values, (v1, v2) -> v1.compareTo(v2));
		StringBuffer valuesStr = new StringBuffer(values.size() * 10);
		values.forEach(v -> valuesStr.append(v));

		// Digest to umid
		String umid = sha256Hex(valuesStr.toString());
		// Generate umidToken.
		String umidToken = randomAlphanumeric(59).toUpperCase(US);
		// Storage umidToken=>umid
		umidCache.put(new CacheKey(umidToken, 60_000), umid);

		log.info("Created umidToken: {}, umid: {}", umidToken, umid);
		return umidToken;
	}

	/**
	 * Gets safety evaluation from umidToken.
	 * 
	 * @param umidToken
	 * @return
	 */
	public double getEvaluation(@NotBlank String umidToken) throws SuspiciousRiskException {
		return 9d; // TODO no implements
	}

	@Override
	public void checkEvaluation(@NotBlank String umidToken) throws SuspiciousRiskException {
		// TODO Auto-generated method stub

	}

}

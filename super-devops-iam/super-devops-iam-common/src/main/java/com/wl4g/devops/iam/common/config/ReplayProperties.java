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
package com.wl4g.devops.iam.common.config;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.config.ConfigurationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.devops.components.tools.common.collection.Collections2;
import com.wl4g.devops.components.tools.common.crypto.digest.DigestUtils2;
import com.wl4g.devops.components.tools.common.log.SmartLogger;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_C_BASE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_BASE;
import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.devops.iam.common.config.CorsProperties.CorsRule.DEFAULT_CORS_ALLOW_HEADER_PREFIX;
import static org.apache.shiro.web.filter.mgt.DefaultFilter.anon;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;

/**
 * Replay attacks configuration properties
 *
 * @author wangl.sir
 * @version v1.0 2019年4月26日
 * @since
 */
public class ReplayProperties implements InitializingBean {

	final protected SmartLogger log = getLogger(getClass());

	/**
	 * Default replay attacks header name.
	 */
	private String replayTokenHeaderName = DEFAULT_REPLAY_TOKEN_HEADER_NAME;

	/**
	 * Default replay attacks parameter name.
	 */
	private String replayTokenParamName = DEFAULT_REPLAY_TOKEN_PARAM_NAME;

	/**
	 * Signature digest algorithm.
	 */
	private String signatureAlg = "MD5";

	/**
	 * Signature timestamp term time.
	 */
	private long termTimeMs = DEFAULT_REPLAY_TOKEN_TERM_TIME;

	/**
	 * Ignore replay attacks validation request mappings.
	 */
	private List<String> excludeValidUriPatterns = new ArrayList<String>() {
		private static final long serialVersionUID = 2330951352919056661L;
		{
			add(URI_S_BASE + "/**");
			add(URI_C_BASE + "/**");
		}
	};

	//
	// --- Temporary fields. ---
	//

	/**
	 * Temporary core configuration.
	 */
	@Autowired
	private transient AbstractIamProperties<? extends ParamProperties> cConfig;

	/**
	 * Temporary configuration.
	 */
	@Autowired
	private transient CorsProperties corsConfig;

	@Override
	public void afterPropertiesSet() throws Exception {
		// Apply default settings.
		applyDefaultPropertiesSet();

		// Remove duplicate.
		if (!isEmpty(excludeValidUriPatterns)) {
			Collections2.disDupCollection(excludeValidUriPatterns);
		}

		// Check algorithm.
		try {
			DigestUtils2.getDigest(getSignatureAlg());
		} catch (Exception e) {
			if (e instanceof NoSuchAlgorithmException)
				throw new ConfigurationException("Replay attacks protect config error.", e);
			else
				throw e;
		}

		// Check termTimeMs.
		if (getTermTimeMs() < DEFAULT_REPLAY_TOKEN_TERM_TIME) {
			log.warn("Term time: {} of replay attack token signature is too short, It is recommended to set is > {}ms",
					getTermTimeMs(), DEFAULT_REPLAY_TOKEN_TERM_TIME);
		}

		// Check header name with cors allowed.
		corsConfig.assertCorsHeaders(singletonList(getReplayTokenHeaderName()));

	}

	public String getReplayTokenHeaderName() {
		return replayTokenHeaderName;
	}

	public ReplayProperties setReplayTokenHeaderName(String replayTokenHeaderName) {
		this.replayTokenHeaderName = replayTokenHeaderName;
		return this;
	}

	public String getReplayTokenParamName() {
		return replayTokenParamName;
	}

	public ReplayProperties setReplayTokenParamName(String replayTokenParamName) {
		this.replayTokenParamName = replayTokenParamName;
		return this;
	}

	public List<String> getExcludeValidUriPatterns() {
		return excludeValidUriPatterns;
	}

	public ReplayProperties setExcludeValidUriPatterns(List<String> excludeValidUriPatterns) {
		if (!isNull(excludeValidUriPatterns)) {
			this.excludeValidUriPatterns = excludeValidUriPatterns;
		}
		return this;
	}

	public String getSignatureAlg() {
		return signatureAlg;
	}

	public ReplayProperties setSignatureAlg(String signatureAlg) {
		this.signatureAlg = signatureAlg;
		return this;
	}

	public long getTermTimeMs() {
		return termTimeMs;
	}

	public ReplayProperties setTermTimeMs(long termTimeMs) {
		this.termTimeMs = termTimeMs;
		return this;
	}

	/**
	 * Apply default properties fields settings.
	 */
	private void applyDefaultPropertiesSet() {
		getExcludeValidUriPatterns().add(URI_S_BASE + "/**");
		getExcludeValidUriPatterns().add(URI_C_BASE + "/**");

		// Automatically exclude patterns with filter chains of anon type
		cConfig.getFilterChain().forEach((pattern, filter) -> {
			if (StringUtils.equals(filter, anon.name())) {
				getExcludeValidUriPatterns().add(pattern);
			}
		});

	}

	final public static long DEFAULT_REPLAY_TOKEN_TERM_TIME = 15 * 60 * 1000L;
	final public static String DEFAULT_REPLAY_TOKEN_HEADER_NAME = DEFAULT_CORS_ALLOW_HEADER_PREFIX + "-Replay-Token";
	final public static String DEFAULT_REPLAY_TOKEN_PARAM_NAME = "_replayToken";
	final public static String KEY_REPLAY_PREFIX = "spring.cloud.devops.iam.replay";

}
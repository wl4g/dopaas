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

import org.apache.shiro.config.ConfigurationException;
import org.springframework.beans.factory.InitializingBean;

import com.wl4g.devops.tool.common.collection.Collections2;
import com.wl4g.devops.tool.common.crypto.digest.DigestUtils2;
import com.wl4g.devops.tool.common.log.SmartLogger;

import static com.wl4g.devops.iam.common.config.CorsProperties.CorsRule.DEFAULT_CORS_ALLOW_HEADER_PREFIX;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;

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
	 * Default replay attacks cookie name.
	 */
	private String replayTokenCookieName = DEFAULT_REPLAY_TOKEN_COOKIE_NAME;

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
	private long termTimeMs = 15 * 60 * 1000L;

	/**
	 * Ignore replay attacks validation request mappings.
	 */
	private List<String> excludeValidReplayMapping = new ArrayList<>();

	@Override
	public void afterPropertiesSet() throws Exception {
		// Remove duplicate.
		if (!isEmpty(excludeValidReplayMapping)) {
			Collections2.disDupCollection(excludeValidReplayMapping);
		}

		try {
			// Check algorithm.
			DigestUtils2.getDigest(getSignatureAlg());
		} catch (Exception e) {
			if (e instanceof NoSuchAlgorithmException) {
				throw new ConfigurationException("Replay attacks protect config error.", e);
			} else {
				throw e;
			}
		}

	}

	public String getReplayTokenCookieName() {
		return replayTokenCookieName;
	}

	public ReplayProperties setReplayTokenCookieName(String replayTokenCookieName) {
		this.replayTokenCookieName = replayTokenCookieName;
		return this;
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

	public List<String> getExcludeValidReplayMapping() {
		return excludeValidReplayMapping;
	}

	public ReplayProperties setExcludeValidReplayMapping(List<String> excludeValidReplayMapping) {
		// if (!isEmpty(excludeValidReplayMapping)) {
		// this.excludeValidReplayMapping.addAll(excludeValidReplayMapping);
		// }
		this.excludeValidReplayMapping = excludeValidReplayMapping;
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

	public static final String DEFAULT_REPLAY_TOKEN_COOKIE_NAME = "IAM-REPLAY-TOKEN";
	public static final String DEFAULT_REPLAY_TOKEN_HEADER_NAME = DEFAULT_CORS_ALLOW_HEADER_PREFIX + "-Replay-Token";
	public static final String DEFAULT_REPLAY_TOKEN_PARAM_NAME = "_replayToken";
	final public static String KEY_REPLAY_PREFIX = "spring.cloud.devops.iam.replay";

}
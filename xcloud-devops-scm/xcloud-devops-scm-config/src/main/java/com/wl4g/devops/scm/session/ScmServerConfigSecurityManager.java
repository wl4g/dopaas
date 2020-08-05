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
package com.wl4g.devops.scm.session;

import static java.util.Collections.singletonMap;

import java.security.spec.KeySpec;

import org.springframework.beans.factory.annotation.Autowired;

import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.core.constants.SCMDevOpsConstants.*;

import com.wl4g.components.common.crypto.asymmetric.AsymmetricCryptor;
import com.wl4g.components.common.crypto.symmetric.AES128ECBPKCS5;
import com.wl4g.components.support.redis.jedis.JedisService;
import com.wl4g.devops.scm.session.HandshakeResult;

/**
 * {@link ScmServerConfigSecurityManager}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年5月26日
 * @since
 */
public class ScmServerConfigSecurityManager extends ConfigSecurityManagerSupport {

	@Autowired
	protected JedisService jedisService;

	public ScmServerConfigSecurityManager(AsymmetricCryptor asymmeCryptor, AES128ECBPKCS5 symmeCryptor) {
		super(asymmeCryptor, symmeCryptor);
	}

	/**
	 * Register and create session.
	 * 
	 * @param clientSecretKey
	 * @return
	 */
	public HandshakeResult registerSession(String clientSecretKey) {
		// Generate data key.
		String hexDataKey = generateKey().toHex();
		KeySpec keySpec = generatePubKeySpec(clientSecretKey);
		// Encryption data key.
		String hexCipherDataKey = encrypt(keySpec, hexDataKey);

		// Generate session key.
		String sessionKey = "sk" + generateHumanTokenSuffix(appName);
		// Storage scm connection session.
		jedisService.mapPut(CACHE_SESSIONS, singletonMap(sessionKey, hexCipherDataKey));

		return new HandshakeResult(sessionKey, hexCipherDataKey);
	}

	/**
	 * Generate token suffix by human rules.
	 * 
	 * @param appName
	 * @return
	 */
	private String generateHumanTokenSuffix(String appName) {
		hasTextOf(appName, "appName");
		String appPrefix = (appName.length() > DEFAULT_SUFFIX_LEN) ? appName.substring(0, DEFAULT_SUFFIX_LEN) : appName;
		StringBuffer tokenSuffix = new StringBuffer(appPrefix.substring(0, 1));
		for (char ch : appPrefix.substring(1).toCharArray()) {
			tokenSuffix.append((int) ch);
		}
		return tokenSuffix.toString();
	}

	final public static int DEFAULT_SUFFIX_LEN = 4;

}
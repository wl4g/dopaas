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
package com.wl4g.devops.iam.configure;

import java.io.Serializable;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Securer configuration properties
 *
 * @author wangl.sir
 * @version v1.0 2019年3月20日
 * @since
 */
public final class SecureConfig implements Serializable {
	private static final long serialVersionUID = -6194767776312196342L;

	/**
	 * Encryption hash digest algorithm candidate list, note that this candidate
	 * list is ordered, the safe will calculate one of the hash algorithm to
	 * encrypt.</br>
	 * Note: Internal secure cipher algorithms factory definition. Note: Online
	 * systems should not be easily modified, otherwise users created previously
	 * will not be able to login properly.
	 */
	final private String[] hashAlgorithms;

	/**
	 * Hash private salt
	 */
	final private String privateSalt;

	/**
	 * Cryptic candidate algorithm
	 */
	final private Integer preCryptPoolSize;

	/**
	 * Cryptic candidate algorithm keyPairs expire-ms
	 */
	final private Long cryptosExpireMs;

	/**
	 * The validity period of the publicKey applied by the user who is ready to
	 * login
	 */
	final private Long applyPubkeyExpireMs;

	public SecureConfig(String[] hashAlgorithms, String privateSalt, Integer preCryptPoolSize, Long cryptosExpireMs,
			Long applyPubkeyExpireMs) {
		Assert.isTrue((hashAlgorithms != null && hashAlgorithms.length > 0), "'hashAlgorithms' empty, please check configure");
		Assert.hasText(privateSalt, "'privateSalt' empty, please check configure");
		Assert.isTrue(preCryptPoolSize > 0, "'preCryptPoolSize' must be greater than 0, please check configure");
		Assert.isTrue(cryptosExpireMs > 0, "'cryptosExpireMs' must be greater than 0, please check configure");
		Assert.isTrue(applyPubkeyExpireMs > 30_000, "'applyPubkeyExpireMs' must be greater than 30000, please check configure");
		this.hashAlgorithms = hashAlgorithms;
		this.privateSalt = privateSalt;
		this.preCryptPoolSize = preCryptPoolSize;
		this.cryptosExpireMs = cryptosExpireMs;
		this.applyPubkeyExpireMs = applyPubkeyExpireMs;
	}

	final public String[] getHashAlgorithms() {
		Assert.state(hashAlgorithms != null, "No configuration item 'hashAlgorithms' that is not empty is allowed");
		return hashAlgorithms;
	}

	final public String getPrivateSalt() {
		Assert.state(!StringUtils.isEmpty(privateSalt), "No configuration item 'privateSalt' that is not empty is allowed");
		return privateSalt;
	}

	final public int getPreCryptPoolSize() {
		Assert.state(preCryptPoolSize != null, "No configuration item 'preCryptSize' that is not empty is allowed");
		return preCryptPoolSize;
	}

	final public long getCryptosExpireMs() {
		Assert.state(cryptosExpireMs != null, "No configuration item 'cryptosExpireMs' that is not empty is allowed");
		return cryptosExpireMs;
	}

	final public long getApplyPubkeyExpireMs() {
		Assert.state(!StringUtils.isEmpty(applyPubkeyExpireMs),
				"No configuration item 'applyPubkeyExpireMs' that is not empty is allowed");
		return applyPubkeyExpireMs;
	}

}
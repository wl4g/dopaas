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
package com.wl4g.devops.iam.crypto.keypair;

import com.wl4g.devops.iam.crypto.AbstractCryptographicService;
import com.wl4g.devops.support.concurrent.locks.JedisLockManager;

/**
 * RSA cryptographic service.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019-08-30
 * @since
 */
public final class RSACryptographicService extends AbstractCryptographicService<RSAKeySpecWrapper> {

	/**
	 * Cryptic algorithm.
	 */
	final protected CryptoHolder crypto;

	public RSACryptographicService(JedisLockManager lockManager) {
		super(lockManager);
		this.crypto = CryptoHolder.getInstance("RSA");
	}

	/*
	 * Encryption from hex.
	 * 
	 * @see
	 * com.wl4g.devops.iam.crypto.CryptographicService#encryptWithHex(com.wl4g.
	 * devops.iam.crypto.Cryptos.KeySpecPair, java.lang.String)
	 */
	@Override
	public String encryptWithHex(RSAKeySpecWrapper keySpec, String hexPlain) {
		return crypto.build(keySpec).encrypt(hexPlain);
	}

	@Override
	public String decryptWithHex(RSAKeySpecWrapper keySpec, String hexCipher) {
		return crypto.build(keySpec).decrypt(hexCipher);
	}

	@Override
	protected RSAKeySpecWrapper generateKeySpec() {
		return crypto.generateKeySpecPair();
	}

}
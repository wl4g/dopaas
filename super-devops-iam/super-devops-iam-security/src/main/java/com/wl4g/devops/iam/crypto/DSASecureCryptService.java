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
package com.wl4g.devops.iam.crypto;

import com.wl4g.devops.support.concurrent.locks.JedisLockManager;
import com.wl4g.devops.tool.common.crypto.cipher.DSAAsymCryptor;
import com.wl4g.devops.tool.common.crypto.cipher.spec.DSAKeyPairSpec;
import com.wl4g.devops.tool.common.crypto.cipher.spec.KeyPairSpec;
import com.wl4g.devops.tool.common.crypto.cipher.spec.RSAKeyPairSpec;

/**
 * DSA cryptographic service.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019-08-30
 * @since
 */
public final class DSASecureCryptService extends AbstractAsymmetricCryptService<RSAKeyPairSpec> {

	/**
	 * DSA cryptic algorithm.
	 */
	final protected DSAAsymCryptor dsa = new DSAAsymCryptor();

	public DSASecureCryptService(JedisLockManager lockManager) {
		super(lockManager);
	}

	@Override
	public SecureAlgKind kind() {
		return SecureAlgKind.DSA;
	}

	@Override
	public String encryptWithHex(KeyPairSpec keySpec, String hexPlain) {
		return dsa.build(keySpec).encrypt(hexPlain);
	}

	@Override
	public String decryptWithHex(KeyPairSpec keySpec, String hexCipher) {
		return dsa.build(keySpec).decrypt(hexCipher);
	}

	@Override
	protected DSAKeyPairSpec generateKeySpec() {
		return dsa.generateKeySpecPair();
	}

}
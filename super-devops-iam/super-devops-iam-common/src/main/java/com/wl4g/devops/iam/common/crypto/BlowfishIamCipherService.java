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
package com.wl4g.devops.iam.common.crypto;

/**
 * {@link BlowfishIamCipherService}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年3月29日 v1.0.0
 * @see
 */
public class BlowfishIamCipherService extends AbstractSymmetricCipherService {

	@Override
	public CipherCryptKind kind() {
		return CipherCryptKind.BLOWFISH;
	}

	@Override
	public String encrypt(byte[] key, String plaintext) {
		// TODO
		return null;
	}

	@Override
	public String decrypt(byte[] key, String ciphertext) {
		// TODO
		return null;
	}

}

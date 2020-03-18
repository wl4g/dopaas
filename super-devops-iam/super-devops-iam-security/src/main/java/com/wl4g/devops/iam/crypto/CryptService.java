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

import com.wl4g.devops.tool.common.crypto.cipher.spec.KeyPairSpec;

/**
 * Cryptographic Services.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019-08-30
 * @since
 */
public interface CryptService {

	/**
	 * Encryption with hex plain.
	 *
	 * @param keySpec
	 * @param hexPlain
	 * @return
	 */
	String encryptWithHex(KeyPairSpec keySpec, String hexPlain);

	/**
	 * Decryption with hex cipher.
	 *
	 * @param keySpec
	 * @param hexCipher
	 * @return
	 */
	String decryptWithHex(KeyPairSpec keySpec, String hexCipher);

	/**
	 * Apply keySpec resource.
	 *
	 * @return
	 */
	KeyPairSpec borrow();

	/**
	 * Apply keySpec resource.
	 *
	 * @param index
	 * @return
	 */
	KeyPairSpec borrow(int index);

}
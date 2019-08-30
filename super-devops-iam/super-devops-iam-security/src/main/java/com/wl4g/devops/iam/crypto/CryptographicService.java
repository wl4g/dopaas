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

import java.util.List;

/**
 * Cryptographic Services.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019-08-30
 * @since
 */
public interface CryptographicService {

	/**
	 * Encryption with hex plain.
	 * 
	 * @param keySpecPair
	 * @param hexPlain
	 * @return
	 */
	String encryptWithHex(KeySpecPair keySpecPair, String hexPlain);

	/**
	 * Decryption with hex cipher.
	 * 
	 * @param keySpecPair
	 * @param hexCipher
	 * @return
	 */
	String decryptWithHex(KeySpecPair keySpecPair, String hexCipher);

	/**
	 * Apply keySpec resource.
	 * 
	 * @return
	 */
	KeySpecPair borrow();

	/**
	 * Apply keySpec resource.
	 * 
	 * @param index
	 * @return
	 */
	KeySpecPair borrow(int index);

	/**
	 * Get keySpecPairs.
	 * 
	 * @return
	 */
	List<KeySpecPair> getKeySpecPairs();

}

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

import static com.wl4g.devops.tool.common.lang.Assert2.hasTextOf;

import static com.wl4g.devops.iam.common.crypto.IamCipherService.CipherCryptKind;

import com.wl4g.devops.common.framework.operator.Operator;

/**
 * Data and parameter symmetric crypt service </br>
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年3月29日 v1.0.0
 * @see
 */
public interface IamCipherService extends Operator<CipherCryptKind> {

	/**
	 * Encryption cipher text.
	 * 
	 * @param key
	 * @param plaintext
	 * @return
	 */
	String encrypt(byte[] key, String plaintext);

	/**
	 * Decryption plain text.
	 * 
	 * @param key
	 * @param hexCiphertext
	 * @return
	 */
	String decrypt(byte[] key, String hexCiphertext);

	/**
	 * Iam symmetric secure crypt algorithm definitions.
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version 2020年3月29日 v1.0.0
	 * @see
	 */
	public static enum CipherCryptKind {

		AES("AES/ECB/PKCS5Padding"),

		BLOWFISH("BLOWFISH"), // TODO

		DES3("DES3"); // TODO

		final private String algorithm;

		private CipherCryptKind(String algorithm) {
			hasTextOf(algorithm, "algorithm");
			this.algorithm = algorithm;
		}

		public String getAlgorithm() {
			return algorithm;
		}

		public static CipherCryptKind safeOf(String algorithm) {
			for (CipherCryptKind k : values()) {
				if (String.valueOf(algorithm).equalsIgnoreCase(k.name())
						|| String.valueOf(algorithm).equalsIgnoreCase(k.getAlgorithm())) {
					return k;
				}
			}
			return null;
		}

	}

}

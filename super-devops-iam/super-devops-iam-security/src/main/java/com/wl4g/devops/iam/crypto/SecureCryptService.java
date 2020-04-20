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

import com.wl4g.devops.common.framework.operator.Operator;
import com.wl4g.devops.tool.common.codec.Base58;
import com.wl4g.devops.tool.common.crypto.asymmetric.spec.KeyPairSpec;

import static com.wl4g.devops.iam.crypto.SecureCryptService.SecureAlgKind;
import static com.wl4g.devops.tool.common.lang.Assert2.hasTextOf;
import static com.wl4g.devops.tool.common.lang.Assert2.notNull;

import java.security.spec.KeySpec;

/**
 * Secretkey asymmetric secure crypt service.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019-08-30
 * @since
 */
public interface SecureCryptService extends Operator<SecureAlgKind> {

	/**
	 * Encryption with hex plain.
	 *
	 * @param keySpec
	 * @param plaintext
	 * @return
	 */
	String encrypt(KeySpec keySpec, String plaintext);

	/**
	 * Decryption with hex cipher.
	 *
	 * @param keySpec
	 * @param hexCiphertext
	 * @return
	 */
	String decrypt(KeySpec keySpec, String hexCiphertext);

	/**
	 * Gets borrow keyPairSpec.
	 *
	 * @return
	 */
	default KeyPairSpec borrowKeyPair() {
		return generateKeyBorrow(-1);
	}

	/**
	 * Generate keyPairSpec.
	 *
	 * @param index
	 * @return
	 */
	KeyPairSpec generateKeyBorrow(int index);

	/**
	 * Generate keyPairSpec.
	 * 
	 * @return
	 */
	KeyPairSpec generateKeyPair();

	/**
	 * Generate keyPairSpec by publicKey and privateKey.
	 * 
	 * @param publicKey
	 * @param privateKey
	 * @return
	 */
	KeyPairSpec generateKeyPair(byte[] publicKey, byte[] privateKey);

	/**
	 * Deserialization generate private KeySpec.
	 * 
	 * @param publicKey
	 * @return
	 */
	KeySpec generatePubKeySpec(byte[] publicKey);

	/**
	 * Deserialization generate private KeySpec.
	 * 
	 * @param privateKey
	 * @return
	 */
	KeySpec generateKeySpec(byte[] privateKey);

	/**
	 * Iam asymmetric secure crypt algorithm kind definitions.
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version 2020年3月29日 v1.0.0
	 * @see
	 */
	public static enum SecureAlgKind {

		RSA("RSA/ECB/PKCS1Padding"),

		DSA("DSA"), // TODO

		ECC("ECC"); // TODO

		final private String algorithm;

		private SecureAlgKind(String algorithm) {
			hasTextOf(algorithm, "algorithm");
			this.algorithm = algorithm;
		}

		public String getAlgorithm() {
			return algorithm;
		}

		public static SecureAlgKind of(String encodedAlgKind) {
			return of(true, encodedAlgKind);
		}

		public static SecureAlgKind of(boolean decode, String algKindStr) {
			if (decode) {
				algKindStr = new String(Base58.decode(algKindStr));
			}
			SecureAlgKind kind = safeOf(algKindStr);
			notNull(kind, "Illegal secure algorithm kind: %s", algKindStr);
			return kind;
		}

		private static SecureAlgKind safeOf(String cryptKind) {
			for (SecureAlgKind k : values()) {
				if (String.valueOf(cryptKind).equalsIgnoreCase(k.name())
						|| String.valueOf(cryptKind).equalsIgnoreCase(k.getAlgorithm())) {
					return k;
				}
			}
			return null;
		}

	}

}
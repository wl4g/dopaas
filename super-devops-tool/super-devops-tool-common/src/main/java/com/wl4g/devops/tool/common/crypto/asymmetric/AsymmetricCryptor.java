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
package com.wl4g.devops.tool.common.crypto.asymmetric;

import java.security.spec.KeySpec;

import com.wl4g.devops.tool.common.crypto.CrypticSource;
import com.wl4g.devops.tool.common.crypto.asymmetric.spec.KeyPairSpec;

/**
 * {@link AsymmetricCryptor}
 * 
 * @param <C>
 * @param <K>
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2019年08月29日 v1.0.0
 * @see
 */
public interface AsymmetricCryptor {

	/**
	 * Generate KeyPairSpec
	 *
	 * @return
	 */
	KeyPairSpec generateKeyPair();

	/**
	 * Generate KeyPairSpec.
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
	 * Encrypt plain text based on the built key pair
	 *
	 * @param keySpec
	 * @param plainSource
	 * @return
	 */
	CrypticSource encrypt(final KeySpec keySpec, final CrypticSource plainSource);

	/**
	 * Decryption the hex ciphertext based on the constructed key pair
	 *
	 * @param keySpec
	 * @param cipherSource
	 * @return
	 */
	CrypticSource decrypt(final KeySpec keySpec, final CrypticSource cipherSource);

	/**
	 * Gets algorithm name
	 *
	 * @return
	 */
	String getAlgorithmPrimary();

	/**
	 * Gets algorithm paddings name
	 *
	 * @return
	 */
	String getPadAlgorithm();

	/**
	 * Gets asymmetric key digits
	 *
	 * @return
	 */
	int getKeyBit();

}

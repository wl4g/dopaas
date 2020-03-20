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
package com.wl4g.devops.tool.common.crypto.cipher;

import static com.wl4g.devops.tool.common.lang.Assert2.notEmptyOf;
import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Hex;

import com.wl4g.devops.tool.common.crypto.cipher.spec.KeyPairSpec;

/**
 * Fast Abstract asymmetric algorithm public implementation.
 *
 * @author wangl.sir
 * @version v1.0 2019年1月21日
 * @since
 */
abstract class AbstractFastAsymCryptor<C, K> {

	/*
	 * Current used encryption and decryption cipher
	 */
	final private static ThreadLocal<Cipher[]> currentCipherPairCache = new ThreadLocal<>();

	/**
	 * Specify the key factory of the algorithm instance.
	 */
	final protected KeyFactory keyFactory;

	/**
	 * Secret key constructor
	 *
	 * @param keyWrap
	 */
	public AbstractFastAsymCryptor() {
		try {
			keyFactory = KeyFactory.getInstance(getAlgorithmPrimary());
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Generate keySpecPair
	 *
	 * @return
	 */
	final public K generateKeySpecPair() {
		try {
			// Generate keyPair.
			KeyPairGenerator kpg = KeyPairGenerator.getInstance(getAlgorithmPrimary());
			kpg.initialize(getKeyBit());
			KeyPair keyPair = kpg.generateKeyPair();

			// New create keySpec pair.
			KeySpec pubKeySepc = keyFactory.getKeySpec(keyPair.getPublic(), getPublicKeySpecClass());
			KeySpec privKeySepc = keyFactory.getKeySpec(keyPair.getPrivate(), getPrivateKeySpecClass());
			return newKeySpec(getAlgorithmPrimary(), pubKeySepc, privKeySepc);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Encrypt plain text based on the built key pair
	 *
	 * @param plaintext
	 * @return
	 */
	final public String encrypt(String plaintext) {
		if (isBlank(plaintext)) {
			return null;
		}
		try {
			Cipher[] cipherPair = currentCipherPairCache.get();
			notEmptyOf(cipherPair, "cipherPair");
			byte[] encrypted = cipherPair[0].doFinal(plaintext.getBytes());
			return Hex.encodeHexString(encrypted);
		} catch (Exception e) {
			throw new IllegalStateException(String.format("The plaintext string to be encrypted:[%s]", plaintext), e);
		}
	}

	/**
	 * Solve the cipher text based on the constructed key pair
	 *
	 * @param hexCiphertext
	 * @return
	 */
	final public String decrypt(String hexCiphertext) {
		if (isBlank(hexCiphertext)) {
			return null;
		}
		try {
			Cipher[] cipherPair = currentCipherPairCache.get();
			notEmptyOf(cipherPair, "cipherPair");
			byte[] dec = Hex.decodeHex(hexCiphertext.toCharArray());
			byte[] decrypted = cipherPair[1].doFinal(dec);
			return new String(decrypted, "UTF-8");
		} catch (Exception e) {
			throw new IllegalStateException(String.format("The ciphertext string to be decrypted: [%s]", hexCiphertext), e);
		}
	}

	/**
	 * Initialize the build of a password instance based on the specified key
	 * pair
	 *
	 * @param keyspec
	 * @return
	 */
	@SuppressWarnings("unchecked")
	final public C build(KeyPairSpec keyspec) {
		notNullOf(keyspec, "keyspec");
		try {
			// Get current cache cipherPair.
			Cipher[] cipherPair = currentCipherPairCache.get();
			Cipher encryptCipher = null, decryptCipher = null;
			if (!isNull(cipherPair)) {
				encryptCipher = cipherPair[0];
				decryptCipher = cipherPair[1];
			} else { // Create a cipher instance and initialize it
				encryptCipher = Cipher.getInstance(getPadAlgorithm());
				decryptCipher = Cipher.getInstance(getPadAlgorithm());
			}

			// Generate publicKey/privateKey to cache
			PrivateKey key = keyFactory.generatePrivate(keyspec.getKeySpec());
			PublicKey pubKey = keyFactory.generatePublic(keyspec.getPubKeySpec());
			decryptCipher.init(Cipher.DECRYPT_MODE, key);
			encryptCipher.init(Cipher.ENCRYPT_MODE, pubKey);

			// Save to current thread cache
			currentCipherPairCache.set(new Cipher[] { encryptCipher, decryptCipher });
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		return (C) this;
	}

	/**
	 * Get algorithm name
	 *
	 * @return
	 */
	protected abstract String getAlgorithmPrimary();

	/**
	 * Get algorithm padding
	 *
	 * @return
	 */
	protected abstract String getPadAlgorithm();

	/**
	 * Get asymmetric key digits
	 *
	 * @return
	 */
	protected abstract int getKeyBit();

	/**
	 * Get public key description of asymmetric algorithms
	 *
	 * @return
	 */
	protected abstract Class<? extends KeySpec> getPublicKeySpecClass();

	/**
	 * Get private key description of asymmetric algorithms
	 *
	 * @return
	 */
	protected abstract Class<? extends KeySpec> getPrivateKeySpecClass();

	/**
	 * New create keySpecEntity {@link KeySpec}
	 * 
	 * @param algorithm
	 * @param pubKeySpec
	 * @param keySpec
	 * @return
	 */
	protected abstract K newKeySpec(String algorithm, KeySpec pubKeySpec, KeySpec keySpec);

}
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

import static com.wl4g.devops.tool.common.lang.Assert2.notEmptyOf;
import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Hex;

import com.wl4g.devops.tool.common.crypto.asymmetric.spec.KeyPairSpec;
import com.wl4g.devops.tool.common.log.SmartLogger;

/**
 * Fast Abstract asymmetric algorithm public implementation.
 *
 * @author wangl.sir
 * @version v1.0 2019年1月21日
 * @since
 */
abstract class AbstractFastAsymmCryptor implements AsymmetricCryptor {

	/*
	 * Current used encryption and decryption cipher
	 */
	final private static ThreadLocal<Cipher[]> currentCipherPairCache = new ThreadLocal<>();

	final protected SmartLogger log = getLogger(getClass());

	/**
	 * Specify the key factory of the algorithm instance.
	 */
	final protected KeyFactory keyFactory;

	/**
	 * Secret key constructor
	 *
	 * @param keyWrap
	 */
	public AbstractFastAsymmCryptor() {
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
	@Override
	final public KeyPairSpec generateKeySpecPair() {
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
	 * Deserialization generate keyPair.
	 * 
	 * @param publicKey
	 * @param privateKey
	 * @return
	 */
	@Override
	final public KeyPairSpec generateKeyPair(byte[] publicKey, byte[] privateKey) {
		try {
			// Deserialization generate keyPair.
			PublicKey _publicKey = null;
			if (!isNull(publicKey)) {
				_publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKey));
			}

			PrivateKey _privateKey = null;
			if (!isNull(privateKey)) {
				_privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKey));
			}

			// Generate keySpec by keyPair.
			KeySpec pubKeySepc = keyFactory.getKeySpec(_publicKey, getPublicKeySpecClass());
			KeySpec privKeySepc = keyFactory.getKeySpec(_privateKey, getPrivateKeySpecClass());
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
	@Override
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
	@Override
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
	 * @param keyPairSpec
	 * @return
	 */
	@Override
	final public AsymmetricCryptor getInstance(KeyPairSpec keyPairSpec) {
		notNullOf(keyPairSpec, "keyPairSpec");
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
			PrivateKey key = keyFactory.generatePrivate(keyPairSpec.getKeySpec());
			PublicKey pubKey = keyFactory.generatePublic(keyPairSpec.getPubKeySpec());
			decryptCipher.init(Cipher.DECRYPT_MODE, key);
			encryptCipher.init(Cipher.ENCRYPT_MODE, pubKey);

			// Save to current thread cache
			currentCipherPairCache.set(new Cipher[] { encryptCipher, decryptCipher });
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		return this;
	}

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
	protected abstract KeyPairSpec newKeySpec(String algorithm, KeySpec pubKeySpec, KeySpec keySpec);

}
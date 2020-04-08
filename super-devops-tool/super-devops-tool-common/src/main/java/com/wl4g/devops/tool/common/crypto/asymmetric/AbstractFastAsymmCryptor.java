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

import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
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
			return newKeySpec(getAlgorithmPrimary(), generatePubKeySpec(publicKey), generateKeySpec(privateKey));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public KeySpec generatePubKeySpec(byte[] publicKey) {
		notNullOf(publicKey, "publicKey");
		try {
			PublicKey _publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKey));
			// Generate public KeySepc.
			return keyFactory.getKeySpec(_publicKey, getPublicKeySpecClass());
		} catch (InvalidKeySpecException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public KeySpec generateKeySpec(byte[] privateKey) {
		notNullOf(privateKey, "privateKey");
		try {
			// Generate private KeySepc.
			PrivateKey _privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKey));
			return keyFactory.getKeySpec(_privateKey, getPrivateKeySpecClass());
		} catch (InvalidKeySpecException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	final public String encrypt(KeySpec keySpec, String plaintext) {
		notNullOf(keySpec, "keySpec");
		if (isBlank(plaintext)) {
			return null;
		}
		try {
			Cipher encryptCipher = Cipher.getInstance(getPadAlgorithm());
			// Generate publicKey
			PublicKey pubKey = keyFactory.generatePublic(keySpec);
			encryptCipher.init(Cipher.ENCRYPT_MODE, pubKey);
			byte[] encrypted = encryptCipher.doFinal(plaintext.getBytes());
			return Hex.encodeHexString(encrypted);
		} catch (Exception e) {
			throw new IllegalStateException(format("Failed to encryption plaintext of [%s]", plaintext), e);
		}
	}

	@Override
	final public String decrypt(KeySpec keySpec, String hexCiphertext) {
		notNullOf(keySpec, "keySpec");
		if (isBlank(hexCiphertext)) {
			return null;
		}
		try {
			Cipher decryptCipher = Cipher.getInstance(getPadAlgorithm());
			// Generate privateKey
			PrivateKey key = keyFactory.generatePrivate(keySpec);
			decryptCipher.init(Cipher.DECRYPT_MODE, key);
			byte[] dec = Hex.decodeHex(hexCiphertext.toCharArray());
			byte[] decrypted = decryptCipher.doFinal(dec);
			return new String(decrypted, "UTF-8");
		} catch (Exception e) {
			throw new IllegalStateException(format("Failed to decryption hex ciphertext of [%s]", hexCiphertext), e);
		}
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
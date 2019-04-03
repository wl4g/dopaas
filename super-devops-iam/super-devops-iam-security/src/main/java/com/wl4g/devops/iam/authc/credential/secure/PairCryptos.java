/*
 * Copyright 2015 the original author or authors.
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
package com.wl4g.devops.iam.authc.credential.secure;

import java.io.Serializable;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.Cipher;

import org.apache.shiro.codec.Hex;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

//import com.google.common.base.Charsets;
//import com.wl4g.devops.common.utils.serialize.JacksonUtils;
//import com.wl4g.devops.common.utils.serialize.ProtostuffUtils;

/**
 * Asymmetric cryptic definition
 * 
 * @author wangl.sir
 * @version v1.0 2019年1月21日
 * @since
 */
abstract class PairCryptos {

	/**
	 * List of instances for caching all encryption algorithms.<br/>
	 */
	final private static Map<String, PairCryptos> cryptoInstances = new ConcurrentHashMap<>();

	/*
	 * Current used encrypt/decrypt cipher
	 */
	final private static ThreadLocal<Cipher[]> currentCipherPair = new ThreadLocal<>();

	/**
	 * Specify the key factory of the algorithm instance.
	 */
	final private KeyFactory keyFactory;

	static {
		register(new RSA());
	}

	/**
	 * Register cryptic instance
	 * 
	 * @param crypto
	 */
	final private static void register(PairCryptos crypto) {
		if (null != cryptoInstances.putIfAbsent(crypto.getAlgorithm(), crypto)) {
			throw new IllegalStateException(String.format("Already registed algorithm [%s]", crypto.getAlgorithm()));
		}
	}

	/**
	 * Get cryptic instance
	 * 
	 * @param algorithm
	 * @return
	 */
	final public static PairCryptos getInstance(String algorithm) {
		if (!cryptoInstances.containsKey(algorithm)) {
			throw new IllegalStateException(String.format("No such cryptic algorithm:[%s]", algorithm));
		}
		return cryptoInstances.get(algorithm);
	}

	/**
	 * Secret key constructor
	 * 
	 * @param keyWrap
	 */
	private PairCryptos() {
		try {
			this.keyFactory = KeyFactory.getInstance(getAlgorithm());
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Generate keySpecPair
	 * 
	 * @return
	 */
	final public KeySpecPair generateKeySpecPair() {
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance(getAlgorithm());
			kpg.initialize(this.getKeyBit());
			KeyPair keyPair = kpg.generateKeyPair();
			KeySpec pubKeySepc = keyFactory.getKeySpec(keyPair.getPublic(), getPublicKeySpecClass());
			KeySpec privKeySepc = keyFactory.getKeySpec(keyPair.getPrivate(), getPrivateKeySpecClass());
			return new KeySpecPair(getAlgorithm(), pubKeySepc, privKeySepc);
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
		if (StringUtils.isEmpty(plaintext)) {
			return null;
		}
		try {
			Cipher[] cipherPair = currentCipherPair.get();
			Assert.notEmpty(cipherPair, "'encryptCipher' must not be null");
			byte[] encrypted = cipherPair[0].doFinal(plaintext.getBytes());
			return Hex.encodeToString(encrypted);
		} catch (Exception e) {
			throw new IllegalStateException(String.format("The plaintext string to be encrypted:[%s]", plaintext), e);
		}
	}

	/**
	 * Solve the cipher text based on the constructed key pair
	 * 
	 * @param ciphertext
	 * @return
	 */
	final public String decrypt(String ciphertext) {
		if (StringUtils.isEmpty(ciphertext)) {
			return null;
		}
		try {
			Cipher[] cipherPair = currentCipherPair.get();
			Assert.notEmpty(cipherPair, "'decryptCipher' must not be null");
			byte[] dec = Hex.decode(ciphertext);
			byte[] decrypted = cipherPair[1].doFinal(dec);
			return new String(decrypted, "UTF-8");
		} catch (Exception e) {
			throw new IllegalStateException(String.format("The ciphertext string to be decrypted:[%s]", ciphertext), e);
		}
	}

	/**
	 * Initialize the build of a password instance based on the specified key
	 * pair
	 * 
	 * @param keySpecPair
	 * @return
	 */
	final public PairCryptos build(KeySpecPair keySpecPair) {
		Assert.notNull(keySpecPair, "'keySpecPair' must not be null");
		try {
			// Get keyPair caching by publicKey and privateKey
			PublicKey publicKey = this.keyFactory.generatePublic(keySpecPair.getPublicKeySpec());
			PrivateKey privateKey = this.keyFactory.generatePrivate(keySpecPair.getPrivateKeySpec());

			// Get current use cipherPair
			Cipher[] cipherPair = currentCipherPair.get();
			Cipher encryptCipher = null, decryptCipher = null;
			if (cipherPair != null) {
				encryptCipher = cipherPair[0];
				decryptCipher = cipherPair[1];
			} else { // Create a cipher instance and initialize it
				encryptCipher = Cipher.getInstance(getAlgorithm());
				decryptCipher = Cipher.getInstance(getAlgorithm());
			}
			encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
			decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);

			// Save to current thread cache
			currentCipherPair.set(new Cipher[] { encryptCipher, decryptCipher });
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return this;
	}

	/**
	 * Get algorithm name
	 * 
	 * @return
	 */
	protected abstract String getAlgorithm();

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
	 * RivestShamirAdleman algorithm
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年1月20日
	 * @since
	 */
	final private static class RSA extends PairCryptos {

		@Override
		protected String getAlgorithm() {
			return "RSA";
		}

		@Override
		protected int getKeyBit() {
			return 1024;
		}

		@Override
		protected Class<? extends KeySpec> getPublicKeySpecClass() {
			return RSAPublicKeySpec.class;
		}

		@Override
		protected Class<? extends KeySpec> getPrivateKeySpecClass() {
			return RSAPrivateCrtKeySpec.class;
		}

	}

	/**
	 * Packaging classes of asymmetric algorithmic key pairs
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年1月22日
	 * @since
	 */
	final public static class KeySpecPair implements Serializable {
		final private static long serialVersionUID = -6748188131949785684L;
		final transient private static Map<String, KeyFactory> keyFactoryCache = new ConcurrentHashMap<>();

		final private String algorithm;
		final private KeySpec publicKeySpec;
		final private KeySpec privateKeySpec;

		private transient String publicKeyString;
		private transient String privateKeyString;

		public KeySpecPair(String algorithm, KeySpec publicKeySpec, KeySpec privateKeySpec) {
			Assert.notNull(algorithm, "'algorithm' must not be null");
			Assert.notNull(publicKeySpec, "'publicKeySpec' must not be null");
			Assert.notNull(privateKeySpec, "'privateKeySpec' must not be null");
			this.algorithm = algorithm;
			this.publicKeySpec = publicKeySpec;
			this.privateKeySpec = privateKeySpec;
		}

		public String getAlgorithm() {
			return algorithm;
		}

		public KeySpec getPublicKeySpec() {
			return publicKeySpec;
		}

		public KeySpec getPrivateKeySpec() {
			return privateKeySpec;
		}

		public String getPublicKeyString() {
			if (this.publicKeyString == null) {
				try {
					this.publicKeyString = Hex.encodeToString(getKeyFactory().generatePublic(getPublicKeySpec()).getEncoded());
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
			}
			return publicKeyString;
		}

		public String getPrivateKeyString() {
			if (this.privateKeyString == null) {
				try {
					this.privateKeyString = Hex.encodeToString(getKeyFactory().generatePrivate(getPrivateKeySpec()).getEncoded());
				} catch (Exception e) {
					throw new IllegalStateException(e);
				}
			}
			return privateKeyString;
		}

		private KeyFactory getKeyFactory() {
			KeyFactory kf = keyFactoryCache.get(getAlgorithm());
			if (kf == null) {
				try {
					keyFactoryCache.put(getAlgorithm(), (kf = KeyFactory.getInstance(getAlgorithm())));
				} catch (NoSuchAlgorithmException e) {
					throw new IllegalStateException(e);
				}
			}
			return kf;
		}

		@Override
		public String toString() {
			return "KeySpecPair [algorithm=" + getAlgorithm() + ", publicKeyString=" + getPublicKeyString()
					+ ", privateKeyString=" + getPrivateKeyString() + "]";
		}

	}

	public static void main(String[] args) throws Exception {
		// // Get algorithm instance
		// PairCryptos crypto = PairCryptos.getInstance("RSA");
		//
		// // Create keySpec pair
		// KeySpecPair keySpecPair = crypto.generateKeySpecPair();
		// // Deserialize
		// System.out.println("Create keySpec pair:\t" + keySpecPair);
		//
		// // for (int i = 0; i < 2_000; i++) {
		// // new Thread(() -> {
		// // An instance of serialized key load algorithms
		// crypto.build(keySpecPair);
		//
		// String plainText = "abcd";
		// System.out.println("Origin plain text:\t" + plainText);
		//
		// // Encrypt plain text
		// String cipherText = crypto.encrypt(plainText);
		// System.out.println("Encrypt cipher text:\t" + cipherText);
		//
		// // Decrypt cipher text
		// plainText = crypto.decrypt(cipherText);
		// System.out.println("Decrypt plain text:\t" + plainText);
		// // }).start();
		// // }
		//
		// //
		// // KeySpec serialization testing:
		// //
		//
		// KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		// kpg.initialize(1024);
		// KeyPair keyPair = kpg.generateKeyPair();
		// KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		// KeySpec pubKeySepc = keyFactory.getKeySpec(keyPair.getPublic(),
		// RSAPublicKeySpec.class);
		// KeySpec privKeySepc = keyFactory.getKeySpec(keyPair.getPrivate(),
		// RSAPrivateCrtKeySpec.class);
		//
		// System.out.println("==========serial pub================");
		// System.out.println("pub serial before:" +
		// JacksonUtils.toJSONString(pubKeySepc));
		// byte[] pubKeySpecData = ProtostuffUtils.serialize(pubKeySepc);
		// KeySpec pubKeySpec = ProtostuffUtils.deserialize(pubKeySpecData,
		// RSAPublicKeySpec.class);
		// System.out.println("pub serial after:" +
		// JacksonUtils.toJSONString(pubKeySpec));
		//
		// System.out.println("==========serial priv================");
		// System.out.println("priv serial before:" +
		// JacksonUtils.toJSONString(privKeySepc));
		// byte[] privKeySpecData = ProtostuffUtils.serialize(privKeySepc);
		// KeySpec privKeySpec = ProtostuffUtils.deserialize(privKeySpecData,
		// RSAPrivateCrtKeySpec.class);
		// System.out.println("priv serial after:" +
		// JacksonUtils.toJSONString(privKeySpec));
		//
		// System.out.println("----------cipher testing-----------------");
		// Cipher encryptCipher = Cipher.getInstance("RSA");
		// Cipher decryptCipher = Cipher.getInstance("RSA");
		// encryptCipher.init(Cipher.ENCRYPT_MODE,
		// keyFactory.generatePublic(pubKeySpec));
		// decryptCipher.init(Cipher.DECRYPT_MODE,
		// keyFactory.generatePrivate(privKeySpec));
		// // encrypt
		// String res =
		// Hex.encodeToString(encryptCipher.doFinal("abc".getBytes(Charsets.UTF_8)));
		// System.out.println("encrypt:" + res);
		// // decrypt
		// System.out.println("decrypt:" + new
		// String(decryptCipher.doFinal(Hex.decode(res))));
	}

}
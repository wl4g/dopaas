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
package com.wl4g.devops.components.tools.common.crypto.symmetric;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.devops.components.tools.common.jvm.JvmRuntimeKit.*;
import static com.wl4g.devops.components.tools.common.lang.Assert2.isTrue;
import static com.wl4g.devops.components.tools.common.lang.Assert2.hasText;
import static com.wl4g.devops.components.tools.common.lang.Assert2.hasTextOf;
import static com.wl4g.devops.components.tools.common.lang.Assert2.notNull;
import static com.wl4g.devops.components.tools.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.components.tools.common.lang.StringUtils2.isTrue;
import static com.wl4g.devops.components.tools.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.toJSONString;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.lang.System.arraycopy;
import static java.lang.System.getenv;
import static java.lang.System.out;
import static java.util.Locale.US;
import static java.util.Objects.isNull;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.wl4g.devops.components.tools.common.codec.CodecSource;
import com.wl4g.devops.components.tools.common.log.SmartLogger;

/**
 * Abstract symmetric algorithm bouncycastle wrapper implementation.
 * 
 * @author wangl.sir
 * @version v1.0 2019年1月21日
 * @since
 */
abstract class SymmetricCryptorSupport {

	final protected SmartLogger log = getLogger(getClass());

	/** Symmetric algorithm crypto config. */
	final protected AlgorithmSpec config;

	protected SymmetricCryptorSupport(AlgorithmSpec config) {
		notNullOf(config, "SymmetricConfig");
		this.config = config;
	}

	public AlgorithmSpec getAlgorithmConfig() {
		return config;
	}

	/**
	 * Generate symmetric algorithm key keybits.
	 * 
	 * @return
	 */
	public CodecSource generateKey() {
		return generateKey(config.getKeybits());
	}

	/**
	 * Generate symmetric algorithm key.
	 * 
	 * @param keybits
	 * @return
	 */
	public CodecSource generateKey(int keybits) {
		try {
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			KeyGenerator keyGenerator = KeyGenerator.getInstance(config.getAlgName());
			keyGenerator.init(keybits, random);
			SecretKey secretKey = keyGenerator.generateKey();
			return new CodecSource(secretKey.getEncoded());
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Encryption symmetric cipher source.
	 * 
	 * @param key
	 * @param cipherSource
	 * @return
	 */
	public CodecSource encrypt(byte[] key, CodecSource plainSource) {
		return encrypt(key, null, plainSource);
	}

	/**
	 * Encryption symmetric cipher source.
	 * 
	 * @param key
	 * @param iv
	 * @param cipherSource
	 * @return
	 */
	public CodecSource encrypt(byte[] key, byte[] iv, CodecSource plainSource) {
		// Cleanup
		byte[][] parameters = cleanAlgorithmParameters(key, iv);
		key = parameters[0];
		iv = parameters[1];

		if (isNull(iv)) {
			return doEncrypt(key, null, plainSource);
		}

		return doEncrypt(key, new IvParameterSpec(iv), plainSource);
	}

	/**
	 * Decryption symmetric cipher source.
	 * 
	 * @param key
	 * @param cipherSource
	 * @return
	 */
	public CodecSource decrypt(byte[] key, CodecSource cipherSource) {
		return decrypt(key, null, cipherSource);
	}

	/**
	 * Decryption symmetric cipher source.
	 * 
	 * @param key
	 * @param iv
	 * @param cipherSource
	 * @return
	 */
	public CodecSource decrypt(byte[] key, byte[] iv, CodecSource cipherSource) {
		// Cleanup
		byte[][] parameters = cleanAlgorithmParameters(key, iv);
		key = parameters[0];
		iv = parameters[1];

		if (isNull(iv)) {
			return doDecrypt(key, null, cipherSource);
		}

		return doDecrypt(key, new IvParameterSpec(iv), cipherSource);
	}

	/**
	 * Encryption symmetric plain source.
	 * 
	 * @param key
	 * @param iv
	 * @param plainSource
	 * @return
	 */
	protected CodecSource doEncrypt(byte[] key, IvParameterSpec iv, CodecSource plainSource) {
		notNullOf(key, "key");
		notNullOf(plainSource, "plainSource");
		try {
			SecretKey _key = createSecretKey(key);
			// Create a cipher, PKCS5padding is more efficient than
			// PKCS7padding. PKCS7padding can support IOS encryption and
			// decryption.
			Cipher cipher = Cipher.getInstance(config.getAlgTransformationName());
			// This method can be added in three ways according to the
			// requirements of encryption algorithm. (1) No third parameter; (2)
			// the third parameter is SecureRandom; (not available for
			// AES) (3) uses IVParameterspec.
			if (config.isRequireIv()) {
				notNull(iv, "Init algorithm %s cipher Iv is requires", config.getAlgTransformationName());
				cipher.init(Cipher.ENCRYPT_MODE, _key, iv);
			} else {
				cipher.init(Cipher.ENCRYPT_MODE, _key);
			}
			return new CodecSource(cipher.doFinal(plainSource.getBytes()));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Decryption symmetric cipher source.
	 * 
	 * @param key
	 * @param iv
	 * @param cipherSource
	 * @return
	 */
	protected CodecSource doDecrypt(byte[] key, IvParameterSpec iv, CodecSource cipherSource) {
		notNullOf(key, "key");
		notNullOf(cipherSource, "cipherSource");
		try {
			SecretKey _key = createSecretKey(key);
			// Create a cipher, PKCS5padding is more efficient than
			// PKCS7padding. PKCS7padding can support IOS encryption and
			// decryption.
			Cipher cipher = Cipher.getInstance(config.getAlgTransformationName());
			// This method can be added in three ways according to the
			// requirements of encryption algorithm. (1) No third parameter; (2)
			// the third parameter is SecureRandom; (not available for
			// AES) (3) uses IVParameterspec.
			if (config.isRequireIv()) {
				notNull(iv, "Init algorithm %s cipher Iv is requires", config.getAlgTransformationName());
				cipher.init(Cipher.DECRYPT_MODE, _key, iv);
			} else {
				cipher.init(Cipher.DECRYPT_MODE, _key);
			}
			return new CodecSource(cipher.doFinal(cipherSource.getBytes()));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Create secret key by keydata
	 * 
	 * @param key
	 * @return
	 */
	protected SecretKey createSecretKey(byte[] key) {
		return new SecretKeySpec(key, config.getAlgName());
	}

	/**
	 * Convert hex string to bytes
	 * 
	 * @param strhex
	 * @return
	 */
	protected byte[] hex2byte(String strhex) {
		if (strhex == null)
			return null;

		int l = strhex.length();
		if (l > 0 && l % 2 == 1)
			return null;

		byte[] b = new byte[l / 2];
		for (int i = 0; i < l / 2; i++) {
			// b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 +
			// 2), 16);
			int high = Integer.parseInt(strhex.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(strhex.substring(i * 2 + 1, i * 2 + 2), 16);
			b[i] = (byte) (high * 16 + low);
		}
		return b;
	}

	/**
	 * Convert bytes to hex string.
	 * 
	 * @param b
	 * @return
	 */
	protected String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs.toUpperCase();
	}

	/**
	 * Check filtering & leaning algorithm parameters. (key/iv)
	 * 
	 * @param key
	 * @param iv
	 * @return
	 */
	protected byte[][] cleanAlgorithmParameters(byte[] key, byte[] iv) {
		notNullOf(key, "algorithmSecretKey");

		byte[] tmp = null;
		// Iv
		if (!isNull(iv) && !isNull(config.getIvsize())) {
			// isTrue(iv.length >= config.getIvsize(), "algIv.length >= %s is
			// requires", config.getIvsize());
			if (iv.length > config.getIvsize()) {
				tmp = new byte[config.getIvsize()]; // Must length
				arraycopy(iv, 0, tmp, 0, tmp.length);
				int originLen = iv.length;
				iv = tmp;
				log.warn("Super long iv length: {}, actual length after discard: {}", originLen, iv.length);
			}
		}

		// Key
		if (config.getMinBlocksize() == config.getMaxBlocksize()) {
			if (key.length > config.getMinBlocksize()) {
				tmp = new byte[config.getMinBlocksize()]; // Must length
				arraycopy(key, 0, tmp, 0, tmp.length);
				int originLen = key.length;
				key = tmp;
				log.warn("Super long secret key length: {}, actual length after discard: {}", originLen, key.length);
			} else if (key.length < config.getMinBlocksize()) {
				throw new IllegalArgumentException(
						format("Worng key length of %s, must >= %s", key.length, config.getMinBlocksize()));
			}
		} else {
			isTrue((key.length >= config.getMinBlocksize() && key.length <= config.getMaxBlocksize()),
					"algKey.length between %s and %s is requires", config.getMinBlocksize(), config.getMaxBlocksize());
		}

		return new byte[][] { key, iv };
	}

	/**
	 * Gets cipher key from environment.
	 * 
	 * @param envCipherKey
	 * @return
	 */
	public static byte[] getEnvCipherKey(String envCipherKey) {
		hasTextOf(envCipherKey, "envCipherKey");

		// Gets cipherKey from environment.
		String cipherKey = getenv(envCipherKey);
		hasText(cipherKey, "Could't gets default symmetric algorithm cipherKey from environment: [%s]", envCipherKey);

		// Print cipherKey?
		String envCipherPrintKey = envCipherKey + ".print";
		String print = valueOf(getenv(envCipherPrintKey)).toLowerCase(US);
		if (isJVMDebugging || isTrue(print)) {
			out.println(format("Use default symmetric algorithm cipherKey: [%s]", cipherKey));
		}

		return cipherKey.getBytes(UTF_8);
	}

	/**
	 * {@link AlgorithmSpec}
	 * <p>
	 * Modes: </br>
	 * ECB(Electronic Code Book)</br>
	 * CBC(Cipher Block Chaining, Encryption block chain)</br>
	 * CFB(Cipher FeedBack Mode, Encrypted feedback)</br>
	 * OFB(Output FeedBack, Output feedback)</br>
	 * </p>
	 * Complement modes:
	 * <p>
	 * AES/CBC/NoPadding (128)</br>
	 * AES/CBC/PKCS5Padding (128)</br>
	 * AES/ECB/NoPadding (128) </br>
	 * AES/ECB/PKCS5Padding (128)</br>
	 * DES/CBC/NoPadding (56)</br>
	 * DES/CBC/PKCS5Padding (56) </br>
	 * DES/ECB/NoPadding (56) </br>
	 * DES/ECB/PKCS5Padding (56)</br>
	 * DESede/CBC/NoPadding (168) </br>
	 * DESede/CBC/PKCS5Padding (168)</br>
	 * DESede/ECB/NoPadding (168)</br>
	 * DESede/ECB/PKCS5Padding (168) </br>
	 * RSA/ECB/PKCS1Padding (1024, 2048) </br>
	 * RSA/ECB/OAEPWithSHA-1AndMGF1Padding (1024, 2048) </br>
	 * RSA/ECB/OAEPWithSHA-256AndMGF1Padding (1024, 2048)</br>
	 * </p>
	 * Java default is 'AES/ECB/PKCS5Padding'
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020年5月27日
	 * @since
	 */
	protected static class AlgorithmSpec {

		/** Symmetric algorithm primary name. (e.g: AES) */
		final protected String algName;

		/**
		 * Symmetric algorithm transformation name. (e.g: AES/ECB/PKCS5Padding)
		 */
		final protected String algTransformationName;

		/** Symmetric algorithm Iv is requires. */
		final protected boolean isRequireIv;

		/** Symmetric algorithm key bits. */
		final protected int keybits;

		/** Symmetric algorithm minimum block size. */
		final protected int minBlocksize;

		/** Symmetric algorithm maximum block size. */
		final protected int maxBlocksize;

		/** Symmetric algorithm iv size */
		final protected Integer ivsize;

		public AlgorithmSpec(String algName, String algTransformationName, boolean isRequireIv, int keybits, int minBlocksize,
				int maxBlocksize) {
			this(algName, algTransformationName, isRequireIv, keybits, minBlocksize, maxBlocksize, null);
		}

		public AlgorithmSpec(String algName, String algTransformationName, boolean isRequireIv, int keybits, int minBlocksize,
				int maxBlocksize, Integer ivsize) {
			hasTextOf(algName, "SymmetricAlgorithmName");
			hasTextOf(algTransformationName, "SymmetricAlgTransformationName");
			isTrue(minBlocksize > 0, "SymmetricAlgMinBlockSize > 0 is requires");
			isTrue(maxBlocksize > 0, "SymmetricAlgMaxBlockSize > 0 is requires");
			isTrue(maxBlocksize >= minBlocksize,
					"SymmetricAlgMinBlockSize < SymmetricAlgMaxBlockSize is requires, current they are: %s, %s", minBlocksize,
					maxBlocksize);
			if (!isNull(ivsize)) {
				isTrue(ivsize > 0, "SymmetricAlgorithmIvSize > 0 is requires");
			}
			this.algName = algName;
			this.algTransformationName = algTransformationName;
			this.isRequireIv = isRequireIv;
			this.keybits = keybits;
			this.minBlocksize = minBlocksize;
			this.maxBlocksize = maxBlocksize;
			this.ivsize = ivsize;
		}

		public String getAlgName() {
			return algName;
		}

		public String getAlgTransformationName() {
			return algTransformationName;
		}

		public int getMinBlocksize() {
			return minBlocksize;
		}

		public int getMaxBlocksize() {
			return maxBlocksize;
		}

		/**
		 * Gets symmetric algorithm minimum key size(bits)
		 * 
		 * @return
		 */
		public int getKeybits() {
			return keybits;
		}

		public Integer getIvsize() {
			return ivsize;
		}

		public boolean isRequireIv() {
			return this.isRequireIv;
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + " => " + toJSONString(this);
		}

	}

}
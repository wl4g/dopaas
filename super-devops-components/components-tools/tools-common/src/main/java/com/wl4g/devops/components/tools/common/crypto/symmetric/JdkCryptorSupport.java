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

import static com.wl4g.devops.components.tools.common.lang.Assert2.notNull;
import static com.wl4g.devops.components.tools.common.lang.Assert2.notNullOf;
import static java.util.Objects.isNull;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.wl4g.devops.components.tools.common.codec.CodecSource;

/**
 * Abstract symmetric algorithm implementation.
 * <p>
 * <a href= "http://blog.csdn.net/qq_26188423/article/details/60579773">Solution
 * to the inconsistency of AES encryption algorithm in Windows / Linux</a>
 * </p>
 * 
 * <p>
 * <a href="http://tool.chacuo.net/cryptaes">online crypto1</a> </br>
 * <a href="https://www.keylala.cn/aes">online crypto2</a>
 * </p>
 * 
 * @author wangl.sir
 * @version v1.0 2019年1月21日
 * @since
 */
abstract class JdkCryptorSupport extends SymmetricCryptorSupport {

	protected JdkCryptorSupport(AlgorithmSpec config) {
		super(config);
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

}
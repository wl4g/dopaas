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
package com.wl4g.devops.tool.common.crypto.symmetric;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.devops.tool.common.lang.Assert2.hasText;
import static com.wl4g.devops.tool.common.lang.Assert2.hasTextOf;
import static com.wl4g.devops.tool.common.lang.StringUtils2.isTrue;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.lang.System.getenv;
import static java.lang.System.out;
import static java.util.Locale.US;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static com.wl4g.devops.tool.common.jvm.JvmRuntimeKit.*;

import com.wl4g.devops.tool.common.crypto.CrypticSource;
import com.wl4g.devops.tool.common.log.SmartLogger;

/**
 * Abstract symmetric algorithm implementation.
 *
 * @author wangl.sir
 * @version v1.0 2019年1月21日
 * @since
 */
abstract class AbstractSymmetricCryptor implements SymmetricCryptor {

	final protected SmartLogger log = getLogger(getClass());

	@Override
	public CrypticSource generateKey() {
		return generateKey(getKeyBit());
	}

	@Override
	public CrypticSource generateKey(int keysize) {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(getAlgorithmPrimary());
			keyGenerator.init(keysize);
			SecretKey secretKey = keyGenerator.generateKey();
			return new CrypticSource(secretKey.getEncoded());
		} catch (GeneralSecurityException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public CrypticSource encrypt(byte[] cipherKey, CrypticSource plainSource) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(cipherKey, getAlgorithmPrimary());
			// 创建密码器, PKCS5Padding比PKCS7Padding效率高，PKCS7Padding可支持IOS加解密
			Cipher ecipher = Cipher.getInstance(getPadAlgorithm());
			// 初始化为加密模式的密码器，此方法可以采用三种方式，按加密算法要求来添加。（1）无第三个参数（2）第三个参数为 new
			// SecureRandom(..)对象，随机数。(AES不可采用这种方法)（3）采用IVParameterSpec
			ecipher.init(Cipher.ENCRYPT_MODE, skeySpec);

			return new CrypticSource(ecipher.doFinal(plainSource.getBytes()));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public CrypticSource decrypt(byte[] cipherKey, CrypticSource cipherSource) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(cipherKey, getAlgorithmPrimary());
			// 创建密码器, PKCS5Padding比PKCS7Padding效率高，PKCS7Padding可支持IOS加解密
			Cipher dcipher = Cipher.getInstance(getPadAlgorithm());
			// 初始化为加密模式的密码器，此方法可以采用三种方式，按加密算法要求来添加。（1）无第三个参数（2）第三个参数为 new
			// SecureRandom(..)对象，随机数。(AES不可采用这种方法)（3）采用IVParameterSpec
			dcipher.init(Cipher.DECRYPT_MODE, skeySpec);

			return new CrypticSource(dcipher.doFinal(cipherSource.getBytes()));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
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
	 * Gets default symmetric algorithm cipherKey from environment.
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
	 * Default encoding format.
	 */
	final public static String DEFAULT_ENCODING = "UTF-8";

}
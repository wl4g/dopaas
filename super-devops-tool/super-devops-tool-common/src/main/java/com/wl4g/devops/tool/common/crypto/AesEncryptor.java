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
package com.wl4g.devops.tool.common.crypto;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.Crypt;

/**
 * AES加密算法（解决windows/Linux下加解密不一致问题http://blog.csdn.net/qq_26188423/article/details/60579773）<br/>
 * 
 * 在线加解密工具参考：http://tool.chacuo.net/cryptaes
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年3月15日
 * @since
 */
public class AesEncryptor  extends Crypt{
	final private static String KEY_CIPHER_ENV = "DEVOPS_CIPHER_KEY";
	final private static String KEY_CIPHER_PRINT = "DEVOPS_CIPHER_PRINT";

	final private static String DEFAULT_SPEC_ALGORITHM = "AES";
	/**
	 * 算法模式： ECB（Electronic Code Book，电子密码本）模式<br/>
	 * CBC（Cipher Block Chaining，加密块链）模式<br/>
	 * CFB（Cipher FeedBack Mode，加密反馈）模式<br/>
	 * OFB（Output FeedBack，输出反馈）模式<br/>
	 * 补码方式：<br/>
	 * AES/CBC/NoPadding (128)<br/>
	 * AES/CBC/PKCS5Padding (128)<br/>
	 * AES/ECB/NoPadding (128) <br/>
	 * AES/ECB/PKCS5Padding (128)<br/>
	 * DES/CBC/NoPadding (56)<br/>
	 * DES/CBC/PKCS5Padding (56) <br/>
	 * DES/ECB/NoPadding (56) <br/>
	 * DES/ECB/PKCS5Padding (56)<br/>
	 * DESede/CBC/NoPadding (168) <br/>
	 * DESede/CBC/PKCS5Padding (168)<br/>
	 * DESede/ECB/NoPadding (168)<br/>
	 * DESede/ECB/PKCS5Padding (168) <br/>
	 * RSA/ECB/PKCS1Padding (1024, 2048) <br/>
	 * RSA/ECB/OAEPWithSHA-1AndMGF1Padding (1024, 2048) <br/>
	 * RSA/ECB/OAEPWithSHA-256AndMGF1Padding (1024, 2048)<br/>
	 * 参考：https://zhidao.baidu.com/question/1765750919758817420.html<br/>
	 * http://blog.csdn.net/qq_16371729/article/details/50015481<br/>
	 * http://blog.csdn.net/qq_35973977/article/details/77711669<br/>
	 */
	final private static String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";// 默认的加密算法
	final private static String DEFAULT_ENCODING = "UTF-8";
	final private static int DEFAULT_AES_KEYSIZE = 128;

	private Cipher ecipher, dcipher;

	public AesEncryptor() throws Exception {
		this.initialize(getDefaultCipher());
	}

	public AesEncryptor(String key) throws Exception {
		this.initialize(key);
	}

	public String encrypt(String src) throws Exception {
		return byte2hex(this.ecipher.doFinal(src.getBytes(DEFAULT_ENCODING)));
	}

	public String decrypt(String src) throws Exception {
		return new String(this.dcipher.doFinal(hex2byte(src)), DEFAULT_ENCODING);
	}

	/**
	 * 生成AES密钥,返回字节数组, 默认长度为128位(16字节).
	 * 
	 * @return
	 */
	public String generateAesKeyString() {
		return byte2hex(generateAesKey(DEFAULT_AES_KEYSIZE));
	}

	/**
	 * 生成AES密钥,可选长度为128,192,256位.
	 * 
	 * @param keysize
	 * @return
	 */
	public byte[] generateAesKey(int keysize) {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance(DEFAULT_SPEC_ALGORITHM);
			keyGenerator.init(keysize);
			SecretKey secretKey = keyGenerator.generateKey();
			return secretKey.getEncoded();
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}

	private void initialize(String key) throws Exception {
		if (key == null || key.length() != 16)
			throw new RuntimeException("Illegal secret key, only for 16 bytes.");

		SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(DEFAULT_ENCODING), DEFAULT_SPEC_ALGORITHM);
		// 创建密码器, PKCS5Padding比PKCS7Padding效率高，PKCS7Padding可支持IOS加解密
		this.ecipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
		this.dcipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);

		/*
		 * 初始化为加密模式的密码器，此方法可以采用三种方式，按加密算法要求来添加。（1）无第三个参数（2）第三个参数为 new
		 * SecureRandom(..)对象，随机数。(AES不可采用这种方法)（3）采用IVParameterSpec
		 */
		this.ecipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		this.dcipher.init(Cipher.DECRYPT_MODE, skeySpec);
	}

	private String getDefaultCipher() {
		// Get environment cipher password.
		String cipherPasswd = System.getenv(KEY_CIPHER_ENV);
		if (cipherPasswd == null)
			throw new IllegalStateException(String.format("[%s] -> 'cipherPasswd' must not be null", KEY_CIPHER_ENV));

		// Print cipher key
		String print = String.valueOf(System.getenv(KEY_CIPHER_PRINT)).toLowerCase();
		if (print.equalsIgnoreCase("true") || print.equalsIgnoreCase("yes") || print.equalsIgnoreCase("y")) {
			System.out.println(String.format("Used default cipher key '%s'", cipherPasswd));
		}
		return cipherPasswd;
	}

	private byte[] hex2byte(String strhex) {
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

	private String byte2hex(byte[] b) {
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

	public static void main(String[] args) throws Exception {
		AesEncryptor aes = new AesEncryptor();
		String s = "safecloud@#123";
		System.out.println("cipherText: " + (s = aes.encrypt(s)));
		System.out.println("plainText: " + aes.decrypt(s));
	}

}
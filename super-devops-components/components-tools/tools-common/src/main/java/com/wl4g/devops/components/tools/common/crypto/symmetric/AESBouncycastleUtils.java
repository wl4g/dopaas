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

import java.security.GeneralSecurityException;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/**
 * {@link AESBouncycastleUtils}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年5月30日 v1.0.0
 * @see
 */
public class AESBouncycastleUtils {

	/**
	 * Implementation of AES 128 ECB encryption and decryption algorithm
	 * 
	 * @param key
	 *            16 byte key
	 * @param icv
	 *            16 byte vector
	 * @param src
	 *            To encrypt or decrypt data, the data must be an integer
	 *            multiple of 16 bytes
	 * @param encrypting
	 *            Encrypt or decrypt
	 * @return result
	 * @throws GeneralSecurityException
	 */
	public static byte[] aes128Ecb(byte[] key, byte[] src, boolean encrypting) throws GeneralSecurityException {
		if (key == null || key.length != 16) {
			throw new GeneralSecurityException("AES key should be 16 bytes");
		}
		if (src == null || src.length % 16 != 0) {
			throw new GeneralSecurityException("AES src should be an integer multiple of 16");
		}
		return doAESECB(key, src, encrypting);
	}

	/**
	 * Implementation of encryption and decryption algorithm for AES 192 ECB
	 * 
	 * @param key
	 *            24 byte key, exception thrown if key length is not correct
	 * @param src
	 *            To encrypt or decrypt data, the data must be an integer
	 *            multiple of 16 bytes
	 * @param encrypting
	 *            Encrypt or decrypt
	 * @return
	 * @throws GeneralSecurityException
	 */
	public static byte[] aes192Ecb(byte[] key, byte[] src, boolean encrypting) throws GeneralSecurityException {
		if (key == null || key.length != 24) {
			throw new GeneralSecurityException("AES key should be 24 bytes");
		}
		if (src == null || src.length % 16 != 0) {
			throw new GeneralSecurityException("AES src should be an integer multiple of 16");
		}
		return doAESECB(key, src, encrypting);
	}

	/**
	 * Implementation of AES 256 ECB encryption and decryption algorithm
	 * 
	 * @param key
	 *            32 byte key
	 * @param icv
	 *            16 byte vector
	 * @param src
	 *            To encrypt or decrypt data, the data must be an integer
	 *            multiple of 16 bytes
	 * @param encrypting
	 *            Encrypt or decrypt
	 * @return result
	 * @throws GeneralSecurityException
	 */
	public static byte[] aes256Ecb(byte[] key, byte[] src, boolean encrypting) throws GeneralSecurityException {
		if (key == null || key.length != 32) {
			throw new GeneralSecurityException("AES key should be 32 bytes");
		}
		if (src == null || src.length % 16 != 0) {
			throw new GeneralSecurityException("AES src should be an integer multiple of 16");
		}
		return doAESECB(key, src, encrypting);
	}

	/**
	 * Implementation of AES 128 CBC encryption and decryption algorithm
	 * 
	 * @param key
	 *            16 byte key
	 * @param icv
	 *            16 byte vector
	 * @param src
	 *            To encrypt or decrypt data, the data must be an integer
	 *            multiple of 16 bytes
	 * @param encrypting
	 *            Encrypt or decrypt
	 * @return
	 * @throws GeneralSecurityException
	 */
	public static byte[] aes128Cbc(byte[] key, byte[] icv, byte[] src, boolean encrypting) throws GeneralSecurityException {
		if (key == null || key.length != 16) {
			throw new GeneralSecurityException("AES key should be 16 bytes");
		}
		if (src == null || src.length % 16 != 0) {
			throw new GeneralSecurityException("AES src should be an integer multiple of 16");
		}
		return doAESCBC(key, icv, src, encrypting);
	}

	/**
	 * 
	 * Implementation of AES 192 CBC encryption and decryption algorithm
	 * 
	 * @param key
	 *            24 byte key
	 * @param icv
	 *            16 byte vector
	 * @param src
	 *            To encrypt or decrypt data, the data must be an integer
	 *            multiple of 16 bytes
	 * @param encrypting
	 *            Encrypt or decrypt
	 * @return result
	 * @throws GeneralSecurityException
	 */
	public static byte[] aes192Cbc(byte[] key, byte[] icv, byte[] src, boolean encrypting) throws GeneralSecurityException {
		if (key == null || key.length != 24) {
			throw new GeneralSecurityException("AES key should be 24 bytes");
		}
		if (src == null || src.length % 16 != 0) {
			throw new GeneralSecurityException("AES src should be an integer multiple of 16");
		}
		return doAESCBC(key, icv, src, encrypting);
	}

	/**
	 * Implementation of AES 256 CBC encryption and decryption algorithm
	 * 
	 * @param key
	 *            32 byte key
	 * @param icv
	 *            16 byte vector
	 * @param src
	 *            To encrypt or decrypt data, the data must be an integer
	 *            multiple of 16 bytes
	 * @param encrypting
	 *            Encrypt or decrypt
	 * @return result
	 * @throws GeneralSecurityException
	 */
	public static byte[] aes256Cbc(byte[] key, byte[] icv, byte[] src, boolean encrypting) throws GeneralSecurityException {
		if (key == null || key.length != 32) {
			throw new GeneralSecurityException("AES key should be 32 bytes");
		}
		if (src == null || src.length % 16 != 0) {
			throw new GeneralSecurityException("AES src should be an integer multiple of 16");
		}
		return doAESCBC(key, icv, src, encrypting);
	}

	/**
	 * Method for AES ECB operation, internal call
	 * 
	 * @param key
	 * @param src
	 * @param encrypting
	 * @return
	 * @throws GeneralSecurityException
	 */
	private static byte[] doAESECB(byte[] key, byte[] src, boolean encrypting) throws GeneralSecurityException {
		byte[] result = new byte[src.length];
		try {
			BufferedBlockCipher engine = new BufferedBlockCipher(new AESEngine());
			engine.init(encrypting, new KeyParameter(key));
			int len = engine.processBytes(src, 0, src.length, result, 0);
			engine.doFinal(result, len);
		} catch (InvalidCipherTextException e) {
			throw new GeneralSecurityException(e);
		}
		return result;
	}

	/**
	 * Method for AES CBC operation, internal call
	 * 
	 * @param key
	 * @param icv
	 * @param src
	 * @param encrypting
	 * @return
	 * @throws GeneralSecurityException
	 */
	private static byte[] doAESCBC(byte[] key, byte[] icv, byte[] src, boolean encrypting) throws GeneralSecurityException {
		byte[] result = new byte[src.length];
		try {
			BufferedBlockCipher engine = new BufferedBlockCipher(new CBCBlockCipher(new AESEngine()));
			engine.init(encrypting, new ParametersWithIV(new KeyParameter(key), icv));
			int len = engine.processBytes(src, 0, src.length, result, 0);
			engine.doFinal(result, len);
		} catch (InvalidCipherTextException e) {
			throw new GeneralSecurityException(e);
		}
		return result;
	}

}
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

import com.wl4g.devops.tool.common.crypto.CrypticSource;

/**
 * Symmetric cirptor.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月8日
 * @since
 */
public interface SymmetricCryptor {

	/**
	 * Encryption symmetric plain source.
	 * 
	 * @param cipherKey
	 * @param plainSource
	 * @return
	 */
	CrypticSource encrypt(byte[] cipherKey, CrypticSource plainSource);

	/**
	 * Decryption symmetric cipher source.
	 * 
	 * @param cipherKey
	 * @param cipherSource
	 * @return
	 */
	CrypticSource decrypt(byte[] cipherKey, CrypticSource cipherSource);

	/**
	 * Generate symmetric algorithm key, return byte array, default length is
	 * 128 bits (16 bytes)
	 * 
	 * @return
	 */
	CrypticSource generateKey();

	/**
	 * Generate symmetric algorithm key, return byte array, elg, keybit is 128
	 * bits (16 bytes)
	 * 
	 * @param keybit
	 * @return
	 */
	CrypticSource generateKey(int keybit);

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
	 * Gets symmetric key digits
	 *
	 * @return
	 */
	int getKeyBit();

}

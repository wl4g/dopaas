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

public class DES3CryptorTests {

	public static void main(String[] args) throws Exception {
		SymmetricCryptor des = new DES3Cryptor();
		CrypticSource key = des.generateKey(168);
		System.out.println("key => " + key.toBase64());
		String plainText = "12345";
		CrypticSource cipherText = des.encrypt(key.getBytes(), new CrypticSource(plainText));
		System.out.println("cipherText: " + cipherText.toBase64());
		System.out.println("plainText: " + des.decrypt(key.getBytes(), cipherText));
	}

}

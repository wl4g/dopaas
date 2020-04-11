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

import com.wl4g.devops.tool.common.crypto.CrypticSource;
import com.wl4g.devops.tool.common.crypto.asymmetric.RSACryptor;
import com.wl4g.devops.tool.common.crypto.asymmetric.spec.KeyPairSpec;

public class RSACryptorTests {

	public static void main(String[] args) throws Exception {
		// Gets algorithm instance
		AsymmetricCryptor cryptor = new RSACryptor();

		// Create keyPairSpec
		KeyPairSpec keyPair = cryptor.generateKeyPair();
		System.out.println("Generated keyPair:\t" + keyPair);

		CrypticSource plainSource0 = new CrypticSource("abcdefghijkrmnopqrstuvwxyz");
		System.out.println("Plain text:\t" + plainSource0.toString());

		// Encryption
		CrypticSource cipherSource = cryptor.encrypt(keyPair.getPubKeySpec(), plainSource0);
		System.out.println("Encrypted result:\t" + cipherSource.toBase64());

		// Decryption
		CrypticSource plainSource = cryptor.decrypt(keyPair.getKeySpec(), cipherSource);
		System.out.println("Decrypted result:\t" + plainSource.toString());

	}

}
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

import static java.lang.System.out;

import com.wl4g.devops.components.tools.common.codec.CodecSource;
import com.wl4g.devops.components.tools.common.crypto.symmetric.Blowfish128CBC;

public class Blowfish128CBCTests {

	public static void main(String[] args) throws Exception {
		Blowfish128CBC aes = new Blowfish128CBC();
		CodecSource genKey = aes.generateKey();
		out.println("new generateKey => (" + genKey.toBase64() + ")" + genKey.getBytes().length + "bytes");

		// must multiple of 8 bytes
		String plainText = "abcdefghijklmnopqrstuvwxyz123456";
		CodecSource key = new CodecSource("1234567812345678"); // 16bytes
		CodecSource iv = new CodecSource("12345678"); // must 8bytes
		CodecSource cipherText = aes.encrypt(key.getBytes(), iv.getBytes(), new CodecSource(plainText));
		out.println("plainText => " + plainText);
		out.println("key => " + key);
		out.println("iv => " + iv);
		out.println("encrypt => " + cipherText.toBase64());
		out.println("decrypt => " + aes.decrypt(key.getBytes(), iv.getBytes(), cipherText).toString());

	}

}
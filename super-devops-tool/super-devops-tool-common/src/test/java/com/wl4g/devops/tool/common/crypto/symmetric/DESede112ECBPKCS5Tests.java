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

import static java.lang.System.out;

import com.wl4g.devops.tool.common.codec.CodecSource;

public class DESede112ECBPKCS5Tests {

	public static void main(String[] args) throws Exception {
		DESede112ECBPKCS5 des3 = new DESede112ECBPKCS5();
		CodecSource genKey = des3.generateKey();
		out.println("new generateKey => (" + genKey.toBase64() + ")" + genKey.getBytes().length + "bytes");

		String plainText = "abcdefghijklmnopqrstuvwxyz";
		CodecSource key = new CodecSource("1234567890abcd"); // 14/21bytes
		CodecSource cipherText = des3.encrypt(key.getBytes(), new CodecSource(plainText));
		out.println("plainText => " + plainText);
		out.println("key => " + key);
		out.println("encrypt => " + cipherText.toBase64());
		out.println("decrypt => " + des3.decrypt(key.getBytes(), cipherText).toString());
	}

}

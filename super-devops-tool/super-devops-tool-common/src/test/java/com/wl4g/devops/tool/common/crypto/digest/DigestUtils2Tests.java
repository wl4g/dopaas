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
package com.wl4g.devops.tool.common.crypto.digest;

import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;

public class DigestUtils2Tests {

	public static void main(String[] args) throws Exception {
		sha1Test1();
	}

	public static void sha1Test1() throws Exception {
		byte[] salt = Hex.decodeHex("697db6018316b39c".toCharArray());
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		if (salt != null) {
			digest.update(salt);
		}
		byte[] result = digest.digest("123456".getBytes());
		for (int i = 1; i < 1024; i++) {
			digest.reset();
			result = digest.digest(result);
		}
		System.out.println(Hex.encodeHexString(result));
	}

}

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
package com.wl4g.devops.tool.common.codec;

public class EncodesTests {

	public static void main(String[] args) {
		String base64 = Encodes.encodeBase64("123");
		System.out.println("base64: " + base64);
		String hex = base64ToHexString(base64);
		System.out.println("hex: " + hex);
		System.out.println("base64: " + hexToBase64String(hex));
	}

	public static String base64ToHexString(String base64) {
		return Encodes.encodeHex(Encodes.decodeBase64(base64));
	}

	public static String hexToBase64String(String hex) {
		return Encodes.encodeBase64(Encodes.decodeHex(hex));
	}

}

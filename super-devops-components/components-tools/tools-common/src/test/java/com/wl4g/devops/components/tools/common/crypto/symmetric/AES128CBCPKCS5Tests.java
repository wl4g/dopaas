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

import com.jcraft.jsch.jce.AES128CBC;
import com.wl4g.devops.components.tools.common.codec.CodecSource;
import com.wl4g.devops.components.tools.common.crypto.symmetric.AES128CBCPKCS5;

/**
 * {@link AES128CBCPKCS5Tests}
 * <p>
 * Verified1:
 * <a href="https://www.keylala.cn/aes">https://www.keylala.cn/aes</a>
 * </p>
 * <p>
 * Verified2:
 * <a href="http://tool.chacuo.net/cryptaes">http://tool.chacuo.net/cryptaes</a>
 * </p>
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年5月28日
 * @since
 */
public class AES128CBCPKCS5Tests {

	public static void main(String[] args) throws Exception {
		AES128CBCPKCS5 aes = new AES128CBCPKCS5();
		CodecSource genKey = aes.generateKey();
		out.println("new generateKey => (" + genKey.toBase64() + ")" + genKey.getBytes().length + "bytes");

		String plainText = "abcdefghijklmnopqrstuvwxyz";
		CodecSource key = new CodecSource("1234567812345678"); // must 16bytes
		CodecSource iv = new CodecSource("1234567890123456");
		CodecSource cipherText = aes.encrypt(key.getBytes(), iv.getBytes(), new CodecSource(plainText));
		out.println("plainText => " + plainText);
		out.println("key => " + key);
		out.println("iv => " + iv);
		out.println("encrypt => " + cipherText.toBase64());
		out.println("decrypt => " + aes.decrypt(key.getBytes(), iv.getBytes(), cipherText).toString());

		System.out.println("---------- Jsch AES128 verify ---------");
		jschAES128VerifyTest(key, iv, cipherText);
	}

	public static void jschAES128VerifyTest(CodecSource key, CodecSource iv, CodecSource cipherText) throws Exception {
		AES128CBC aes128 = new AES128CBC();
		aes128.init(AES128CBC.DECRYPT_MODE, key.getBytes(), iv.getBytes());
		byte[] out = new byte[32];
		aes128.update(cipherText.getBytes(), 0, out.length, out, 0);
		System.out.println(new CodecSource(out));
	}

}
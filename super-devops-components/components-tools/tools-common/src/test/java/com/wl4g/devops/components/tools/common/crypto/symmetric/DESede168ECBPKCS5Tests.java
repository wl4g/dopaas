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
import com.wl4g.devops.components.tools.common.crypto.symmetric.DESede168ECBPKCS5;

/**
 * {@link DESede168ECBPKCS5Tests}
 * <p>
 * Verified1:
 * <a href="https://www.keylala.cn/aes">https://www.keylala.cn/aes</a>
 * </p>
 * <p>
 * Verified2:
 * <a href="http://tool.chacuo.net/cryptaes">http://tool.chacuo.net/cryptaes
 * (128bits)</a>
 * </p>
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年5月29日
 * @since
 */
public class DESede168ECBPKCS5Tests {

	public static void main(String[] args) throws Exception {
		DESede168ECBPKCS5 des3 = new DESede168ECBPKCS5();
		CodecSource genKey = des3.generateKey();
		out.println("new generateKey => (" + genKey.toBase64() + ")" + genKey.getBytes().length + "bytes");

		String plainText = "abcdefghijklmnopqrstuvwxyz";
		CodecSource key = new CodecSource("1234abcd1234abcd1234abcd"); // 24bytes
		CodecSource cipherText = des3.encrypt(key.getBytes(), new CodecSource(plainText));
		out.println("plainText => " + plainText);
		out.println("key => " + key);
		out.println("encrypt => " + cipherText.toBase64());
		out.println("decrypt => " + des3.decrypt(key.getBytes(), cipherText).toString());
	}

}
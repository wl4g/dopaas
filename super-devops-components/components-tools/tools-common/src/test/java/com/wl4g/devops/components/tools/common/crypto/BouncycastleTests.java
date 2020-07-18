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
package com.wl4g.devops.components.tools.common.crypto;

import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.params.KeyParameter;

import com.wl4g.devops.components.tools.common.codec.CodecSource;

public class BouncycastleTests {

	public static void main(String[] args) throws Exception {
		des8Test();
		desedeTest();
	}

	public static void des8Test() {
		byte[] key = new CodecSource("12345678").getBytes();
		byte[] input = new CodecSource("12345678").getBytes();
		byte[] out = new byte[8];

		// 使用DESEngine进行加密
		DESEngine desEngine = new DESEngine();
		desEngine.init(true, new KeyParameter(key));
		desEngine.processBlock(input, 0, out, 0);
		System.out.println("des encrypt=" + new CodecSource(out).toBase64());

		// 使用DESEngine进行解密
		desEngine.init(false, new KeyParameter(key));
		desEngine.processBlock(input, 0, out, 0);
		System.out.println("des decrypt=" + new CodecSource(out).toBase64());
	}

	public static void desedeTest() {
		byte[] key = CodecSource.fromHex("010203040506070801020304050607080102030405060708").getBytes();
		byte[] input = new CodecSource("12345678").getBytes();
		byte[] out = new byte[input.length];
		DESedeEngine desEdeEngine = new DESedeEngine();

		// 使用DESedeEngine进行加密
		desEdeEngine.init(true, new KeyParameter(key));
		desEdeEngine.processBlock(input, 0, out, 0);
		System.out.println("des ede encrypt=" + new CodecSource(out).toBase64());

		// 使用DESedeEngine进行解密
		desEdeEngine.init(false, new KeyParameter(key));
		desEdeEngine.processBlock(input, 0, out, 0);
		System.out.println("des ede decrypt=" + new CodecSource(out).toBase64());
	}

}
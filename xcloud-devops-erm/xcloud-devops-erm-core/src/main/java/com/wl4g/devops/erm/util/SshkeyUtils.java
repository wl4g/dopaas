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
package com.wl4g.devops.erm.util;

import com.wl4g.components.common.codec.CodecSource;
import com.wl4g.components.common.crypto.symmetric.AES128ECBPKCS5;

import static com.google.common.base.Charsets.UTF_8;

/**
 * @author vjay
 * @date 2020-04-24 16:13:00
 */
public class SshkeyUtils {

	public static String encryptSshkeyToHex(String cipherKey, String sshKey) {
		AES128ECBPKCS5 aes = new AES128ECBPKCS5();
		return aes.encrypt(cipherKey.getBytes(UTF_8), new CodecSource(sshKey)).toHex();
	}

	public static String decryptSshkeyFromHex(String cipherKey, String hexSshKey) {
		AES128ECBPKCS5 aes = new AES128ECBPKCS5();
		return aes.decrypt(cipherKey.getBytes(UTF_8), CodecSource.fromHex(hexSshKey)).toString();
	}

}
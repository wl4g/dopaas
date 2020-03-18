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
package com.wl4g.devops.tool.common.crypto.cipher;

import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;

import com.wl4g.devops.tool.common.crypto.cipher.spec.RSAKeyPairSpec;

/**
 * RivestShamirAdleman algorithm
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年1月20日
 * @since
 */
public class RSAAsymCryptor extends FastAsymCryptor<RSAAsymCryptor, RSAKeyPairSpec> {

	@Override
	protected String getAlgorithmPrimary() {
		return "RSA";
	}

	@Override
	protected int getKeyBit() {
		return 1024;
	}

	@Override
	protected Class<? extends KeySpec> getPublicKeySpecClass() {
		return RSAPublicKeySpec.class;
	}

	@Override
	public Class<? extends KeySpec> getPrivateKeySpecClass() {
		return RSAPrivateCrtKeySpec.class;
	}

	@Override
	protected String getPadAlgorithm() {
		return "RSA/ECB/PKCS1Padding";
	}

	@Override
	protected RSAKeyPairSpec newKeySpec(String algorithm, KeySpec pubKeySpec, KeySpec keySpec) {
		return new RSAKeyPairSpec(algorithm, pubKeySpec, keySpec);
	}

}
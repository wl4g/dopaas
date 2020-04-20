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

import java.security.spec.DSAPrivateKeySpec;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.KeySpec;

import com.wl4g.devops.tool.common.crypto.asymmetric.spec.DSAKeyPairSpec;

/**
 * Asymmetric algorithm implemented by DSA.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2019年11月9日 v1.0.0
 * @see
 */
public class DSACryptor extends AbstractFastAsymmCryptor {

	@Override
	public String getAlgorithmPrimary() {
		return "DSA";
	}

	@Override
	public String getPadAlgorithm() {
		return "DSA/ECB/PKCS1Padding";
	}

	@Override
	public int getKeyBit() {
		return 1024;
	}

	@Override
	protected Class<? extends KeySpec> getPublicKeySpecClass() {
		return DSAPublicKeySpec.class;
	}

	@Override
	protected Class<? extends KeySpec> getPrivateKeySpecClass() {
		return DSAPrivateKeySpec.class;
	}

	@Override
	protected DSAKeyPairSpec newKeySpec(String algorithm, KeySpec pubKeySpec, KeySpec keySpec) {
		return new DSAKeyPairSpec(algorithm, pubKeySpec, keySpec);
	}

}
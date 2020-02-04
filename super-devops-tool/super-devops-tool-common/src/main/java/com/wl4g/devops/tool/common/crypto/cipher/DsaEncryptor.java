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

import com.wl4g.devops.tool.common.crypto.cipher.spec.KeySpecEntity;

public final class DsaEncryptor extends AsymmetricEncryptor<KeySpecEntity> {

	@Override
	protected String getAlgorithmPrimary() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected String getPadAlgorithm() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected int getKeyBit() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Class<? extends KeySpec> getPublicKeySpecClass() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Class<? extends KeySpec> getPrivateKeySpecClass() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected KeySpecEntity newKeySpecEntity(String algorithm, KeySpec pubKeySpec, KeySpec keySpec) {
		throw new UnsupportedOperationException();
	}

}
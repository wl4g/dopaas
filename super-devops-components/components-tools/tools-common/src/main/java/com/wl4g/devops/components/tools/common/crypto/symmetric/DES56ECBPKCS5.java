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

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * {@link DES56ECBPKCS5}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年5月27日
 * @since
 */
public class DES56ECBPKCS5 extends JdkCryptorSupport {

	public DES56ECBPKCS5() {
		super(new AlgorithmSpec("DES", "DES", false, 56, 16, 16));
	}

	@Override
	protected SecretKey createSecretKey(byte[] key) {
		try {
			DESKeySpec dks = new DESKeySpec(key);
			SecretKeyFactory skf = SecretKeyFactory.getInstance(config.getAlgName());
			return skf.generateSecret(dks);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}
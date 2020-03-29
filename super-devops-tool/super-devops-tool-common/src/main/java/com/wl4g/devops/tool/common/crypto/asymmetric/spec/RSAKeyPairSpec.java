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
package com.wl4g.devops.tool.common.crypto.asymmetric.spec;

import java.security.spec.KeySpec;

/**
 * Generic asymmetric algorithmic RSA keyPair spec.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年1月22日
 * @since
 */
public final class RSAKeyPairSpec extends GenericKeyPairSpec {

	final private static long serialVersionUID = -6748188131119785684L;

	public RSAKeyPairSpec(String algorithm, KeySpec pubKeySpec, KeySpec keySpec) {
		super(algorithm, pubKeySpec, keySpec);
	}

	public RSAKeyPairSpec(String keySpecId, String algorithm, KeySpec pubKeySpec, KeySpec keySpec) {
		super(keySpecId, algorithm, pubKeySpec, keySpec);
	}

}
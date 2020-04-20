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

import static com.wl4g.devops.tool.common.lang.Assert2.notNull;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

/**
 * Generic asymmetric algorithmic keyPair spec.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年1月22日
 * @since
 */
abstract class GenericKeyPairSpec extends KeyPairSpec {
	final private static long serialVersionUID = -6748188131949785684L;
	final transient private static Map<String, KeyFactory> keyFactoryCache = new ConcurrentHashMap<>();

	final private String algorithm;
	final private KeySpec keySpec;
	final private KeySpec pubKeySpec;

	// Temporary hex string.
	private transient String keyHexString;
	private transient String pubKeyHexString;

	// Temporary base64 string.
	private transient String keyBase64String;
	private transient String pubKeyBase64String;

	public GenericKeyPairSpec(String algorithm, KeySpec pubKeySpec, KeySpec keySpec) {
		notNull(algorithm, "'algorithm' must not be null");
		notNull(pubKeySpec, "'publicKeySpec' must not be null");
		notNull(keySpec, "'privateKeySpec' must not be null");
		this.algorithm = algorithm;
		this.pubKeySpec = pubKeySpec;
		this.keySpec = keySpec;
	}

	public GenericKeyPairSpec(String keySpecId, String algorithm, KeySpec pubKeySpec, KeySpec keySpec) {
		super(keySpecId);
		notNull(algorithm, "'algorithm' must not be null");
		notNull(pubKeySpec, "'publicKeySpec' must not be null");
		notNull(keySpec, "'privateKeySpec' must not be null");
		this.algorithm = algorithm;
		this.pubKeySpec = pubKeySpec;
		this.keySpec = keySpec;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	@Override
	public KeySpec getKeySpec() {
		return keySpec;
	}

	@Override
	public KeySpec getPubKeySpec() {
		return pubKeySpec;
	}

	@Override
	public String getHexString() {
		if (isBlank(keyHexString) && !isNull(getKeySpec())) {
			try {
				keyHexString = Hex.encodeHexString(getKeyFactory().generatePrivate(getKeySpec()).getEncoded());
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		return keyHexString;
	}

	@Override
	public String getPubHexString() {
		if (isBlank(pubKeyHexString) && !isNull(getPubKeySpec())) {
			try {
				pubKeyHexString = Hex.encodeHexString(getKeyFactory().generatePublic(getPubKeySpec()).getEncoded());
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		return pubKeyHexString;
	}

	@Override
	public String getBase64String() {
		if (isBlank(keyBase64String) && !isNull(getKeySpec())) {
			try {
				keyBase64String = Base64.encodeBase64String(getKeyFactory().generatePrivate(getKeySpec()).getEncoded());
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		return keyBase64String;
	}

	@Override
	public String getPubBase64String() {
		if (isBlank(pubKeyBase64String) && !isNull(getPubKeySpec())) {
			try {
				pubKeyBase64String = Base64.encodeBase64String(getKeyFactory().generatePublic(getPubKeySpec()).getEncoded());
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		return pubKeyBase64String;
	}

	private KeyFactory getKeyFactory() {
		KeyFactory kf = keyFactoryCache.get(getAlgorithm());
		if (isNull(kf)) {
			try {
				keyFactoryCache.put(getAlgorithm(), (kf = KeyFactory.getInstance(getAlgorithm())));
			} catch (NoSuchAlgorithmException e) {
				throw new IllegalStateException(e);
			}
		}
		return kf;
	}

	@Override
	public String toString() {
		return "KeySpecPair [algorithm=" + getAlgorithm() + ", pubKeyString=" + getPubHexString() + ", keyString="
				+ getHexString() + "]";
	}

}
/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.iam.crypto;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.shiro.codec.Hex;
import org.springframework.util.Assert;

/**
 * Packaging classes of asymmetric algorithmic key pairs
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年1月22日
 * @since
 */
final public class KeySpecPair implements Comparable<KeySpecPair>, Serializable {
	final private static long serialVersionUID = -6748188131949785684L;
	final transient private static Map<String, KeyFactory> keyFactoryCache = new ConcurrentHashMap<>();

	final private int sort;
	final private String algorithm;
	final private KeySpec keySpec;
	final private KeySpec pubKeySpec;

	// Temporary hex string.
	private transient String keyHexString;
	private transient String pubKeyHexString;

	// Temporary base64 string.
	private transient String keyBase64String;
	private transient String pubKeyBase64String;

	public KeySpecPair(String algorithm, KeySpec pubKeySpec, KeySpec keySpec) {
		Assert.notNull(algorithm, "'algorithm' must not be null");
		Assert.notNull(pubKeySpec, "'publicKeySpec' must not be null");
		Assert.notNull(keySpec, "'privateKeySpec' must not be null");
		this.sort = (int) (Math.random() * 10_0000);
		this.algorithm = algorithm;
		this.pubKeySpec = pubKeySpec;
		this.keySpec = keySpec;
	}

	public int getSort() {
		return sort;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public KeySpec getKeySpec() {
		return keySpec;
	}

	public KeySpec getPubKeySpec() {
		return pubKeySpec;
	}

	public String getHexString() {
		if (this.keyHexString == null) {
			try {
				this.keyHexString = Hex.encodeToString(getKeyFactory().generatePrivate(getKeySpec()).getEncoded());
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		return keyHexString;
	}

	public String getPubHexString() {
		if (this.pubKeyHexString == null) {
			try {
				this.pubKeyHexString = Hex.encodeToString(getKeyFactory().generatePublic(getPubKeySpec()).getEncoded());
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		return pubKeyHexString;
	}

	public String getBase64String() {
		if (this.keyBase64String == null) {
			try {
				this.keyBase64String = Base64.encodeBase64String(getKeyFactory().generatePrivate(getKeySpec()).getEncoded());
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		return keyBase64String;
	}

	public String getPubBase64String() {
		if (this.pubKeyBase64String == null) {
			try {
				this.pubKeyBase64String = Base64.encodeBase64String(getKeyFactory().generatePublic(getPubKeySpec()).getEncoded());
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		return pubKeyBase64String;
	}

	private KeyFactory getKeyFactory() {
		KeyFactory kf = keyFactoryCache.get(getAlgorithm());
		if (kf == null) {
			try {
				keyFactoryCache.put(getAlgorithm(), (kf = KeyFactory.getInstance(getAlgorithm())));
			} catch (NoSuchAlgorithmException e) {
				throw new IllegalStateException(e);
			}
		}
		return kf;
	}

	@Override
	public int compareTo(KeySpecPair o) {
		return getSort() - o.getSort();
	}

	@Override
	public String toString() {
		return "KeySpecPair [sort=" + getSort() + "algorithm=" + getAlgorithm() + ", publicKeyString=" + getPubHexString()
				+ ", privateKeyString=" + getHexString() + "]";
	}

}

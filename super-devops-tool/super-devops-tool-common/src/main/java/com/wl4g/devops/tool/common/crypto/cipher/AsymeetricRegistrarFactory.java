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

import static com.wl4g.devops.tool.common.lang.Assert2.state;
import static java.lang.String.format;
import static java.util.Objects.isNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Astmmetric algorithm interface SPI.
 * 
 * @author wanglsir@gmail.com, 983708408@qq.com
 * @version 2019年12月28日 v1.0.0
 * @see
 */
public final class AsymeetricRegistrarFactory {

	/**
	 * List of instances for caching all encryption algorithms.<br/>
	 */
	final private static Map<String, AsymmetricEncryptor<?>> INSTANCES = new ConcurrentHashMap<>();

	/**
	 * Register cryptic instance
	 *
	 * @param encryptor
	 */
	final public static void register(AsymmetricEncryptor<?> encryptor) {
		state(isNull(INSTANCES.putIfAbsent(encryptor.getAlgorithmPrimary(), encryptor)),
				format("Already registed algorithm [%s]", encryptor.getAlgorithmPrimary()));
	}

	/**
	 * Get asymmetric cryptic encryptor instance.
	 * 
	 * @param <T>
	 * @param algorithm
	 * @return
	 */
	@SuppressWarnings("unchecked")
	final public static <T extends AsymmetricEncryptor<?>> T getEncryptor(String algorithm) {
		state(INSTANCES.containsKey(algorithm), format("No such cryptic algorithm: %s", algorithm));
		return (T) INSTANCES.get(algorithm);
	}

}
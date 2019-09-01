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
package com.wl4g.devops.iam.crypto;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.CACHE_CRYPTO;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_KEYPAIRS;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.wl4g.devops.iam.common.cache.EnhancedCacheManager;
import com.wl4g.devops.iam.common.cache.EnhancedKey;
import com.wl4g.devops.iam.config.CryptoProperties;

/**
 * Abstract cryptographic service
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019-08-30
 * @since
 */
public abstract class AbstractCryptographicService<K extends KeySpecWrapper> implements CryptographicService<K> {

	/**
	 * Cryptic properties.
	 */
	@Autowired
	protected CryptoProperties config;

	/**
	 * Using Distributed Cache to Ensure Concurrency Control under multiple-node
	 */
	@Autowired
	protected EnhancedCacheManager cacheManager;

	/**
	 * Get the generated key-pairs allconfig.getKeyPairPools()
	 * 
	 * @param checkCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<K> getKeySpecs() {
		List<K> keySpecs = (List<K>) cacheManager.getEnhancedCache(CACHE_CRYPTO)
				.get(new EnhancedKey(KEY_KEYPAIRS, ArrayList.class));
		if (isEmpty(keySpecs)) {
			// Initialize create generated key pairs.
			keySpecs = initKeySpecPool();
		}
		Assert.notEmpty(keySpecs, "'keySpecs' must not be empty");
		return keySpecs;
	}

	/**
	 * Generate keySpec.
	 * 
	 * @return
	 */
	protected abstract K generateKeySpec();

	/**
	 * Create keySpec pool.
	 * 
	 * @return
	 */
	private synchronized List<K> initKeySpecPool() {
		// Create generate cryptic keyPairs
		List<K> keySpecs = new ArrayList<>(config.getKeyPairPools());
		for (int i = 0; i < config.getKeyPairPools(); i++) {
			keySpecs.add(generateKeySpec());
		}

		// The key pairs of candidate asymmetric algorithms are valid.
		cacheManager.getEnhancedCache(CACHE_CRYPTO).putIfAbsent(new EnhancedKey(KEY_KEYPAIRS, config.getKeyPairExpireMs()),
				keySpecs);
		Assert.notEmpty(keySpecs, "'keySpecs' must not be empty");
		return keySpecs;
	}

}

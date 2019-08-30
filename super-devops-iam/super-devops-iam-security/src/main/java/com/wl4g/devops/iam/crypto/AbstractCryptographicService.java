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
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.Collections;
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
abstract class AbstractCryptographicService implements CryptographicService {

	/**
	 * Cryptic algorithm.
	 */
	final protected Cryptos crypto;

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

	public AbstractCryptographicService(Cryptos crypto) {
		this.crypto = crypto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wl4g.devops.iam.crypto.CryptographicService#encryptWithHex(com.wl4g.
	 * devops.iam.crypto.Cryptos.KeySpecPair, java.lang.String)
	 */
	@Override
	public String encryptWithHex(KeySpecPair keySpecPair, String hexPlain) {
		return crypto.build(keySpecPair).encrypt(hexPlain);
	}

	@Override
	public String decryptWithHex(KeySpecPair keySpecPair, String hexCipher) {
		return crypto.build(keySpecPair).decrypt(hexCipher);
	}

	@Override
	public KeySpecPair borrow() {
		return borrow(nextInt(0, config.getKeyPairPools()));
	}

	@Override
	public KeySpecPair borrow(int index) throws IndexOutOfBoundsException {
		return getKeySpecPairs().get(index);
	}

	/**
	 * Get the generated key-pairs allconfig.getKeyPairPools()
	 * 
	 * @param checkCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<KeySpecPair> getKeySpecPairs() {
		List<KeySpecPair> keyPairs = (List<KeySpecPair>) cacheManager.getEnhancedCache(CACHE_CRYPTO)
				.get(new EnhancedKey(KEY_KEYPAIRS, ArrayList.class));
		if (isEmpty(keyPairs)) {
			// Initialize create generated key pairs.
			keyPairs = initKeySpecPairPool();
		}
		Assert.notEmpty(keyPairs, "'keyPairs' must not be empty");

		// By keySpecPair.sort
		Collections.sort(keyPairs);
		return keyPairs;
	}

	/**
	 * Create keySpec pair pool.
	 * 
	 * @return
	 */
	private synchronized List<KeySpecPair> initKeySpecPairPool() {
		// Create generate cryptic keyPairs
		List<KeySpecPair> keyPairs = new ArrayList<>(config.getKeyPairPools());
		for (int i = 0; i < config.getKeyPairPools(); i++) {
			keyPairs.add(crypto.generateKeySpecPair());
		}

		// The key pairs of candidate asymmetric algorithms are valid.
		cacheManager.getEnhancedCache(CACHE_CRYPTO).putIfAbsent(new EnhancedKey(KEY_KEYPAIRS, config.getKeyPairExpireMs()),
				keyPairs);
		Assert.notEmpty(keyPairs, "'keyPairs' must not be empty");
		return keyPairs;
	}

}

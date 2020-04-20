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
import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.devops.tool.common.serialize.JacksonUtils.toJSONString;
import static io.netty.util.internal.ThreadLocalRandom.current;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.exception.ExceptionUtils.wrapAndThrow;
import static org.springframework.util.Assert.notNull;

import static java.util.concurrent.TimeUnit.*;

import java.security.spec.KeySpec;
import java.util.concurrent.locks.Lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;

import static com.wl4g.devops.tool.common.crypto.CrypticSource.*;
import com.wl4g.devops.tool.common.crypto.CrypticSource;
import com.wl4g.devops.iam.common.cache.CacheKey;
import com.wl4g.devops.iam.common.cache.IamCache;
import com.wl4g.devops.iam.common.cache.IamCacheManager;
import com.wl4g.devops.iam.config.properties.CryptoProperties;
import com.wl4g.devops.support.concurrent.locks.JedisLockManager;
import com.wl4g.devops.tool.common.crypto.asymmetric.AsymmetricCryptor;
import com.wl4g.devops.tool.common.crypto.asymmetric.spec.KeyPairSpec;
import com.wl4g.devops.tool.common.log.SmartLogger;

/**
 * Abstract secretKey asymmetric secure crypt service.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019-08-30
 * @since
 */
public abstract class AbstractAymmetricSecureCryptService<K extends KeyPairSpec> implements SecureCryptService {

	final protected SmartLogger log = getLogger(getClass());

	/**
	 * KeySpec class.
	 */
	final protected Class<K> keySpecClass;

	/**
	 * AsymmetricCryptor
	 */
	final protected AsymmetricCryptor cryptor;

	/**
	 * Simple lock manager.
	 */
	final protected Lock lock;

	/**
	 * Cryptic properties.
	 */
	@Autowired
	protected CryptoProperties config;

	/**
	 * Iam cache manager.
	 */
	@Autowired
	protected IamCacheManager cacheManager;

	@SuppressWarnings("unchecked")
	public AbstractAymmetricSecureCryptService(JedisLockManager lockManager, AsymmetricCryptor cryptor) {
		notNullOf(lockManager, "lockManager");
		notNullOf(cryptor, "cryptor");
		this.cryptor = cryptor;
		this.lock = lockManager.getLock(getClass().getSimpleName(), DEFAULT_LOCK_EXPIRE_MS, MILLISECONDS);

		ResolvableType resolveType = ResolvableType.forClass(getClass());
		this.keySpecClass = (Class<K>) resolveType.getSuperType().getGeneric(0).resolve();
		notNull(keySpecClass, "KeySpecClass must not be null.");
	}

	@Override
	public String encrypt(KeySpec keySpec, String plaintext) {
		return cryptor.encrypt(keySpec, new CrypticSource(plaintext)).toHex();
	}

	@Override
	public String decrypt(KeySpec keySpec, String hexCiphertext) {
		return cryptor.decrypt(keySpec, fromHex(hexCiphertext)).toString();
	}

	@Override
	public KeyPairSpec generateKeyBorrow(int index) {
		if (index < 0 || index >= config.getKeyPairPools()) {
			int _index = current().nextInt(config.getKeyPairPools());
			log.debug("Borrow keySpec index '{}' of out bound, used random index '{}'", index, _index);
			index = _index;
		}

		// Load keySpec by index.
		IamCache cryptoCache = cacheManager.getIamCache(CACHE_CRYPTO);
		KeyPairSpec keySpec = cryptoCache.getMapField(new CacheKey(index, keySpecClass));
		if (isNull(keySpec)) { // Expired?
			try {
				if (lock.tryLock(DEFAULT_TRYLOCK_TIMEOUT_MS, MILLISECONDS)) {
					doInitializingKeyPairSpecAll();
				}
			} catch (Exception e) {
				wrapAndThrow(e);
			} finally {
				lock.unlock();
			}
			// Retry get.
			keySpec = cryptoCache.getMapField(new CacheKey(index, keySpecClass));
		}

		notNull(keySpec, "Unable to borrow keySpec resource.");
		return keySpec;
	}

	@Override
	public KeyPairSpec generateKeyPair() {
		return cryptor.generateKeyPair();
	}

	@Override
	public KeyPairSpec generateKeyPair(byte[] publicKey, byte[] privateKey) {
		return cryptor.generateKeyPair(publicKey, privateKey);
	}

	@Override
	public KeySpec generatePubKeySpec(byte[] publicKey) {
		return cryptor.generatePubKeySpec(publicKey);
	}

	@Override
	public KeySpec generateKeySpec(byte[] privateKey) {
		return cryptor.generateKeySpec(privateKey);
	}

	/**
	 * Initializing keyPairSpec pool.
	 *
	 * @return
	 */
	private synchronized void doInitializingKeyPairSpecAll() {
		// Create generate cryptic keyPairs
		for (int index = 0; index < config.getKeyPairPools(); index++) {
			// Generate keyPairSpec.
			KeyPairSpec keySpec = generateKeyPair();
			// Storage to cache.
			cacheManager.getIamCache(CACHE_CRYPTO).mapPut(new CacheKey(index, config.getKeyPairExpireMs()), keySpec);
			log.debug("Puts keySpec to cache for index {}, keySpec => {}", index, toJSONString(keySpec));
		}
		log.info("Initialized keySpec total: {}", config.getKeyPairPools());
	}

	/**
	 * Default JIGSAW initializing mutex image timeoutMs
	 */
	final public static long DEFAULT_LOCK_EXPIRE_MS = 60_000L;

	/**
	 * Try lock mutex timeoutMs.
	 */
	final public static long DEFAULT_TRYLOCK_TIMEOUT_MS = DEFAULT_LOCK_EXPIRE_MS / 2;

}
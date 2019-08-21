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
package com.wl4g.devops.iam.authc.credential.secure;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import org.apache.shiro.authc.CredentialsException;
import org.apache.shiro.codec.CodecSupport;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.CACHE_SECURER;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.BEAN_DELEGATE_MSG_SOURCE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.CACHE_PUBKEY_IDX;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_KEYPAIRS;
import static com.wl4g.devops.common.utils.codec.CheckSums.*;

import com.wl4g.devops.iam.authc.credential.secure.Cryptos.KeySpecPair;
import com.wl4g.devops.iam.common.cache.EnhancedCache;
import com.wl4g.devops.iam.common.cache.EnhancedCacheManager;
import com.wl4g.devops.iam.common.cache.EnhancedKey;
import com.wl4g.devops.iam.common.i18n.SessionDelegateMessageBundle;
import com.wl4g.devops.iam.configure.SecurerConfig;

/**
 * Abstract credentials securer adapter
 * @see {@link org.apache.shiro.crypto.hash.DefaultHashService}
 * 
 * @author wangl.sir
 * @version v1.0 2019年1月16日
 * @since
 */
/**
 * @author wangl.sir
 * @version v1.0 2019年3月11日
 * @since
 */
abstract class AbstractCredentialsSecurerSupport extends CodecSupport implements IamCredentialsSecurer {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Pre-asymmetric cryptic count size.
	 */
	final private SecurerConfig config;

	/**
	 * Using Distributed Cache to Ensure Concurrency Control under multiple-node
	 */
	final protected EnhancedCacheManager cacheManager;

	/**
	 * Cryptic algorithm
	 */
	final protected Cryptos crypto;

	/**
	 * The 'private' part of the hash salt.
	 */
	final private ByteSource privateSalt;

	/**
	 * Delegate message source.
	 */
	@Resource(name = BEAN_DELEGATE_MSG_SOURCE)
	protected SessionDelegateMessageBundle bundle;

	/**
	 * IAM delegate credentials securer.
	 */
	@Autowired(required = false)
	protected CredentialsSecurerAdapter delegate;

	protected AbstractCredentialsSecurerSupport(SecurerConfig config, EnhancedCacheManager cacheManager) {
		Assert.notNull(config, "'config' must not be null");
		Assert.notNull(config.getPrivateSalt(), "'privateSalt' must not be null");
		Assert.notNull(config.getPreCryptPoolSize(), "'cryptSize' must not be null");
		Assert.notNull(config.getCryptosExpireMs() > 0, "'cryptExpireMs' must greater than 0");
		Assert.notNull(config.getApplyPubkeyExpireMs() > 0, "'applyPubKeyExpireMs' must greater than 0");
		Assert.notNull(cacheManager, "'cacheManager' must not be null");

		this.privateSalt = ByteSource.Util.bytes(config.getPrivateSalt());
		this.config = config;
		this.cacheManager = cacheManager;
		this.crypto = Cryptos.getInstance("RSA");
		Assert.notNull(this.crypto, "'crypto' must not be null");
	}

	@Override
	public String signature(@NotNull CredentialsToken token) {
		// Delegate signature
		if (delegate != null && !token.isResolved()) {
			// Resolve request credentials
			return delegate.signature(resolves(token));
		}

		// When the delegate is null, it is unresolved.
		if (!token.isResolved()) {
			token = resolves(token); // It is necessary to resolve
		}

		// Hashing signature
		return doCredentialsHash(token,
				(algorithm, source, salt, hashIters) -> new SimpleHash(algorithm, source, salt, hashIters));
	}

	@Override
	public boolean validate(@NotNull CredentialsToken token, @NotNull String storedCredentials)
			throws CredentialsException, RuntimeException {
		/*
		 * Password is a string that may be set to empty.
		 * See:xx.realm.GeneralAuthorizingRealm#doAuthenticationInfo
		 */
		Assert.notNull(storedCredentials, "Stored credentials is null, please check configure");

		// Delegate validate
		if (delegate != null && !token.isResolved()) {
			return delegate.validate(resolves(token), storedCredentials);
		}

		// Compare request credentials with storage credentials
		return MessageDigest.isEqual(toBytes(signature(token)), toBytes(storedCredentials));
	}

	@Override
	public String applySecret(@NotNull String authCode) {
		Assert.notNull(authCode, "'authCode' must not be null");

		// Load secret keySpecPairs
		List<KeySpecPair> keyPairs = loadSecretKeySpecPairs();

		EnhancedCache pubIdxCache = cacheManager.getEnhancedCache(CACHE_PUBKEY_IDX);
		Integer index = (Integer) pubIdxCache.get(new EnhancedKey(authCode, Integer.class));
		if (index == null) {
			index = (int) (Math.random() * keyPairs.size());
		}
		KeySpecPair keyPair = keyPairs.get(index);

		// Save the applied keyPair to the cache
		pubIdxCache.put(new EnhancedKey(authCode, config.getApplyPubkeyExpireMs()), index);

		if (log.isInfoEnabled()) {
			log.info("Apply secret key is principal:{}, index:{}, publicKeyHexString:{}, privateKeyHexString:{}", authCode, index,
					keyPair.getPublicHexString(), keyPair.getPrivateHexString());
		}
		return keyPair.getPublicHexString();
	}

	/**
	 * Combines the specified 'private' salt bytes with the specified additional
	 * extra bytes to use as the total salt during hash computation.
	 * {@code privateSaltBytes} will be {@code null} }if no private salt has
	 * been configured.
	 * 
	 * @param privateSalt
	 *            the (possibly {@code null}) 'private' salt to combine with the
	 *            specified extra bytes
	 * @param publicSalt
	 *            the extra bytes to use in addition to the given private salt.
	 * @return a combination of the specified private salt bytes and extra bytes
	 *         that will be used as the total salt during hash computation.
	 * @see {@link org.apache.shiro.crypto.hash.DefaultHashService#combine()}
	 */
	protected abstract ByteSource merge(ByteSource privateSalt, ByteSource publicSalt);

	/**
	 * Get public salt
	 * 
	 * @param principal
	 * @return
	 */
	protected abstract ByteSource getPublicSalt(@NotNull String principal);

	/**
	 * Execute hashing
	 * 
	 * @param token
	 *            Resolved parameter token
	 * @param hasher
	 * @return
	 */
	protected String doCredentialsHash(@NotNull CredentialsToken token, @NotNull Hasher hasher) {
		// Merge salt
		ByteSource salt = merge(privateSalt, getPublicSalt(token.getPrincipal()));
		if (log.isDebugEnabled()) {
			log.debug("Merge salt. principal:[{}], salt:[{}]", token.getPrincipal(), salt);
		}

		// Determine which hashing algorithm to use
		final String[] hashAlgorithms = config.getHashAlgorithms();
		final int size = hashAlgorithms.length;
		final long index = crc32(salt.getBytes()) % size & (size - 1);
		final String algorithm = hashAlgorithms[(int) index];
		final int hashIters = (int) (Integer.MAX_VALUE % (index + 1)) + 1;

		// Hashing signature
		return hasher.hashing(algorithm, ByteSource.Util.bytes(token.getCredentials()), salt, hashIters).toHex();
	}

	/**
	 * Corresponding to the front end, RSA1 encryption is used by default.
	 * 
	 * @param token
	 * @return
	 */
	protected CredentialsToken resolves(@NotNull CredentialsToken token) {
		// Determine keyPairSpec
		KeySpecPair keySpecPair = determineSecretKeySpecPair(token.getPrincipal());

		if (log.isInfoEnabled()) {
			String publicBase64String = keySpecPair.getPublicHexString();
			String pattern = "The determined key pair is principal:[{}], publicKey:[{}], privateKey:[{}]";
			String privateBase64String = "Not output";
			boolean output = true;

			if (log.isDebugEnabled() || output) {
				privateBase64String = keySpecPair.getPrivateBase64String();
				log.debug(pattern, token.getPrincipal(), publicBase64String, privateBase64String);
			} else {
				log.info(pattern, token.getPrincipal(), publicBase64String, privateBase64String);
			}
		}

		// Mysterious decrypt them
		return new CredentialsToken(token.getPrincipal(), crypto.build(keySpecPair).decrypt(token.getCredentials()), true);
	}

	/**
	 * Determine asymmetric algorithms keyPair
	 * 
	 * @param checkCode
	 * @return
	 */
	private KeySpecPair determineSecretKeySpecPair(@NotNull String principal) {
		// Get the generated key pair
		List<KeySpecPair> keyPairs = loadSecretKeySpecPairs();

		EnhancedCache pubIdxCache = cacheManager.getEnhancedCache(CACHE_PUBKEY_IDX);
		try {
			// Choose the best one from the candidate key pair
			Integer index = (Integer) pubIdxCache.get(new EnhancedKey(principal, Integer.class));
			if (index != null) {
				return keyPairs.get(index);
			}
			throw new IllegalStateException(
					String.format("The applied publicKey does not exist and may have expired. principal:[%s]", principal));
		} finally { // Clean-up
			pubIdxCache.remove(new EnhancedKey(principal));
		}
	}

	/**
	 * Get the generated key-pairs all
	 * 
	 * @param checkCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<KeySpecPair> loadSecretKeySpecPairs() {
		List<KeySpecPair> keyPairs = (List<KeySpecPair>) cacheManager.getEnhancedCache(CACHE_SECURER)
				.get(new EnhancedKey(KEY_KEYPAIRS, ArrayList.class));

		if (keyPairs == null) {
			// Create a list of newly generated key pairs
			keyPairs = createKeySpecPairs();
		}
		Assert.notEmpty(keyPairs, "'keyPairs' must not be empty");

		// By keySpecPair.sort
		Collections.sort(keyPairs);
		return keyPairs;
	}

	/**
	 * Re-create key spec pairs
	 * 
	 * @return
	 */
	private synchronized List<KeySpecPair> createKeySpecPairs() {
		// Re-create cryptic keyPairs
		List<KeySpecPair> keyPairs = new ArrayList<>(config.getPreCryptPoolSize());

		// Generate keySpec pairs
		for (int i = 0; i < config.getPreCryptPoolSize(); i++) {
			keyPairs.add(crypto.generateKeySpecPair());
		}

		// The key pairs of candidate asymmetric algorithms are valid.
		cacheManager.getEnhancedCache(CACHE_SECURER).putIfAbsent(new EnhancedKey(KEY_KEYPAIRS, config.getCryptosExpireMs()),
				keyPairs);

		Assert.notEmpty(keyPairs, "'keyPairs' must not be empty");
		return keyPairs;
	}

	/**
	 * Hasher
	 * 
	 * @author wangl.sir
	 * @version v1.0 2019年1月21日
	 * @since
	 */
	private interface Hasher {
		Hash hashing(String algorithm, ByteSource source, ByteSource salt, int hashIterations);
	}

}
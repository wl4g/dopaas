/*
 * Copyright 2015 the original author or authors.
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

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import org.apache.shiro.codec.CodecSupport;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.CACHE_SECURER;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.BEAN_DELEGATE_MESSAGE_SOURCE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.CACHE_PUBKEY_INDEX;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_KEYPAIRS;

import com.wl4g.devops.common.utils.CheckSums;
import com.wl4g.devops.iam.authc.credential.secure.PairCryptos.KeySpecPair;
import com.wl4g.devops.iam.common.cache.EnhancedKey;
import com.wl4g.devops.iam.common.cache.JedisCacheManager;
import com.wl4g.devops.iam.common.i18n.DelegateBoundleMessageSource;
import com.wl4g.devops.iam.configure.SecurerConfig;

/**
 * Abstract credentials securer adapter
 * 
 * @author wangl.sir
 * @version v1.0 2019年1月16日
 * @since
 * @see {@link org.apache.shiro.crypto.hash.DefaultHashService}
 */
/**
 * @author wangl.sir
 * @version v1.0 2019年3月11日
 * @since
 */
public abstract class AbstractCredentialsSecurerAdapter extends CodecSupport implements IamCredentialsSecurer {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * The 'private' part of the hash salt.
	 */
	final private ByteSource privateSalt;

	/**
	 * Pre-asymmetric cryptic count size.
	 */
	final private SecurerConfig config;

	/**
	 * Using Distributed Cache to Ensure Concurrency Control under multiple-node
	 */
	final protected JedisCacheManager cacheManager;

	/**
	 * Cryptic algorithm
	 */
	final protected PairCryptos crypto;

	/**
	 * Delegate message source.
	 */
	@Resource(name = BEAN_DELEGATE_MESSAGE_SOURCE)
	protected DelegateBoundleMessageSource delegate;

	protected AbstractCredentialsSecurerAdapter(SecurerConfig config, JedisCacheManager cacheManager) {
		Assert.notNull(config, "'config' must not be null");
		Assert.notNull(config.getPrivateSalt(), "'privateSalt' must not be null");
		Assert.notNull(config.getPreCryptPoolSize(), "'cryptSize' must not be null");
		Assert.notNull(config.getCryptosExpireMs() > 0, "'cryptExpireMs' must greater than 0");
		Assert.notNull(config.getApplyPubkeyExpireMs() > 0, "'applyPubKeyExpireMs' must greater than 0");
		Assert.notNull(cacheManager, "'cacheManager' must not be null");

		this.privateSalt = ByteSource.Util.bytes(config.getPrivateSalt());
		this.config = config;
		this.cacheManager = cacheManager;
		this.crypto = PairCryptos.getInstance("RSA");
		Assert.notNull(this.crypto, "'crypto' must not be null");
	}

	@Override
	public String signature(@NotNull String principal, @NotNull String credentials) {
		Assert.isTrue(StringUtils.hasText(principal), "'principal' must not be null");
		Assert.isTrue(StringUtils.hasText(credentials), "'credentials' must not be null");

		// Get hashing public salt
		ByteSource publicSalt = this.getPublicSalt(principal);

		// Merge salt
		ByteSource salt = this.merge(this.privateSalt, publicSalt);
		if (log.isDebugEnabled()) {
			log.debug("Merge salt. principal:[{}], salt:[{}]", principal, salt);
		}

		// Hashing signature
		return this.doExecuteHash(principal, credentials, salt, (ByteSource source, String algorithm,
				int hashIterations) -> new SimpleHash(algorithm, source, salt, hashIterations)).toHex();
	}

	@Override
	public boolean validate(@NotNull String principal, @NotNull String credentials, @NotNull String storedCredentials) {
		Assert.isTrue(StringUtils.hasText(principal), "'principal' must not be null");
		Assert.isTrue(StringUtils.hasText(credentials), "'credentials' must not be null");
		Assert.isTrue(StringUtils.hasText(storedCredentials), "'storedCredentials' must not be null");

		// Compare request credentials with storage credentials
		return MessageDigest.isEqual(toBytes(signature(principal, credentials)), toBytes(storedCredentials));
	}

	@Override
	public String applySecretKey(@NotNull String principal) {
		Assert.notNull(principal, "'principal' must not be null");

		KeySpecPair[] keySpecPairs = this.getSecretKeyPairs();
		int index = (int) (Math.random() * keySpecPairs.length);
		KeySpecPair keySpecPair = keySpecPairs[index];

		// Save the applied keyPair to the cache
		this.cacheManager.getEnhancedCache(CACHE_PUBKEY_INDEX).put(new EnhancedKey(principal, config.getApplyPubkeyExpireMs()),
				index);

		if (log.isInfoEnabled()) {
			log.info("Apply secret key is principal[{}], index[{}], publicKeyString[{}], privateKeyString[{}]", principal, index,
					keySpecPair.getPublicKeyString(), keySpecPair.getPrivateKeyString());
		}
		return keySpecPair.getPublicKeyString();
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
	protected abstract ByteSource getPublicSalt(String principal);

	/**
	 * Do execute hash
	 * 
	 * @param credentials
	 * @param salt
	 * @param hasher
	 * @return
	 */
	protected Hash doExecuteHash(String principal, String credentials, ByteSource salt, Hasher hasher) {
		// Resolving cryptic credentials
		final String plainCredentials = this.resolvingCrypto(principal, credentials);

		// Determine which hashing algorithm to use
		final String[] hashAlgorithms = this.config.getHashAlgorithms();
		final int size = hashAlgorithms.length;
		final long index = CheckSums.crc32(salt.getBytes()) % size & (size - 1);
		final String algorithm = hashAlgorithms[(int) index];
		final int hashIterations = (int) (Integer.MAX_VALUE % (index + 1)) + 1;

		// Hashing signature
		return hasher.execute(ByteSource.Util.bytes(plainCredentials), algorithm, hashIterations);
	}

	/**
	 * Corresponding to the front end, RSA1 encryption is used by default.
	 * 
	 * @param ciphertext
	 * @return
	 */
	protected String resolvingCrypto(String principal, String ciphertext) {
		// Determine keyPairSpec
		KeySpecPair keySpecPair = this.determineSecretKeySpecPair(principal);

		if (log.isInfoEnabled()) {
			String publicKeyString = keySpecPair.getPublicKeyString();
			String pattern = "The determined key pair is principal:[{}], publicKey:[{}], privateKey:[{}]";
			String privateKeyString = "Not output";

			boolean output = true;
			if (log.isDebugEnabled() || output) {
				privateKeyString = keySpecPair.getPrivateKeyString();
				log.debug(pattern, principal, publicKeyString, privateKeyString);
			} else {
				log.info(pattern, principal, publicKeyString, privateKeyString);
			}
		}

		// Get mysterious instances and decrypt them
		return this.crypto.build(keySpecPair).decrypt(ciphertext);
	}

	/**
	 * Determine asymmetric algorithms keyPair
	 * 
	 * @param checkCode
	 * @return
	 */
	private KeySpecPair determineSecretKeySpecPair(String principal) {
		// Get the generated key pair
		KeySpecPair[] keySpecPairs = this.getSecretKeyPairs();

		// Choose the best one from the candidate key pair
		Integer index = (Integer) this.cacheManager.getEnhancedCache(CACHE_PUBKEY_INDEX)
				.get(new EnhancedKey(principal, Integer.class));
		if (index != null) {
			return keySpecPairs[index];
		}
		throw new IllegalStateException(
				String.format("The applied publicKey does not exist and may have expired. principal:[%s]", principal));
	}

	/**
	 * Get the generated key pairs all
	 * 
	 * @param checkCode
	 * @return
	 */
	private KeySpecPair[] getSecretKeyPairs() {
		KeySpecPair[] keySpecPairs = (KeySpecPair[]) this.cacheManager.getEnhancedCache(CACHE_SECURER)
				.get(new EnhancedKey(KEY_KEYPAIRS, KeySpecPair[].class));

		if (keySpecPairs == null) {
			// Create a list of newly generated key pairs
			keySpecPairs = this.createKeyPairs();
		}
		Assert.notEmpty(keySpecPairs, "'keyPairs' must not be empty");
		return keySpecPairs;
	}

	/**
	 * Re-create key spec pairs
	 * 
	 * @return
	 */
	private KeySpecPair[] createKeyPairs() {
		// Re-create cryptic keyPairs
		KeySpecPair[] keySpecPairs = new KeySpecPair[config.getPreCryptPoolSize()];

		// Generate keySpec pairs
		for (int i = 0; i < config.getPreCryptPoolSize(); i++) {
			keySpecPairs[i] = this.crypto.generateKeySpecPair();
		}

		// The key pairs of candidate asymmetric algorithms are valid.
		this.cacheManager.getEnhancedCache(CACHE_SECURER).put(new EnhancedKey(KEY_KEYPAIRS, config.getCryptosExpireMs()),
				keySpecPairs);

		Assert.notEmpty(keySpecPairs, "'keySpecPairs' must not be empty");
		return keySpecPairs;
	}

	/**
	 * Hasher
	 * 
	 * @author wangl.sir
	 * @version v1.0 2019年1月21日
	 * @since
	 */
	interface Hasher {
		Hash execute(ByteSource source, String algorithm, int hashIterations);
	}

}
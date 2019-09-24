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

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.CredentialsException;
import org.apache.shiro.codec.CodecSupport;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.BEAN_DELEGATE_MSG_SOURCE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_SECRET_INDEX;
import static com.wl4g.devops.common.utils.codec.CheckSums.*;
import static com.wl4g.devops.iam.common.utils.SessionBindings.bind;
import static com.wl4g.devops.iam.common.utils.SessionBindings.getBindValue;
import static com.wl4g.devops.iam.common.utils.Sessions.getSessionId;
import static io.netty.util.internal.ThreadLocalRandom.current;

import com.wl4g.devops.iam.common.cache.EnhancedCacheManager;
import com.wl4g.devops.iam.common.i18n.SessionDelegateMessageBundle;
import com.wl4g.devops.iam.configure.SecureConfig;
import com.wl4g.devops.iam.crypto.keypair.RSACryptographicService;
import com.wl4g.devops.iam.crypto.keypair.RSAKeySpecWrapper;

/**
 * Abstract credentials securer adapter
 * 
 * @see {@link org.apache.shiro.crypto.hash.DefaultHashService}
 * 
 * @author wangl.sir
 * @version v1.0 2019年3月11日
 * @since
 */
abstract class AbstractCredentialsSecurerSupport extends CodecSupport implements IamCredentialsSecurer {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Secure configuration.
	 */
	final private SecureConfig config;

	/**
	 * Using Distributed Cache to Ensure Concurrency Control under multiple-node
	 */
	final protected EnhancedCacheManager cacheManager;

	/**
	 * The 'private' part of the hash salt.
	 */
	final private ByteSource privateSalt;

	/**
	 * Cryptic service.
	 */
	@Autowired
	protected RSACryptographicService rsaCryptoService;

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

	protected AbstractCredentialsSecurerSupport(SecureConfig config, EnhancedCacheManager cacheManager) {
		Assert.notNull(config, "'config' must not be null");
		Assert.notNull(config.getPrivateSalt(), "'privateSalt' must not be null");
		Assert.notNull(config.getPreCryptPoolSize(), "'cryptSize' must not be null");
		Assert.notNull(config.getCryptosExpireMs() > 0, "'cryptExpireMs' must greater than 0");
		Assert.notNull(config.getApplyPubkeyExpireMs() > 0, "'applyPubKeyExpireMs' must greater than 0");
		Assert.notNull(cacheManager, "'cacheManager' must not be null");

		this.privateSalt = ByteSource.Util.bytes(config.getPrivateSalt());
		this.config = config;
		this.cacheManager = cacheManager;
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
	public boolean validate(@NotNull CredentialsToken token, @NotNull AuthenticationInfo info)
			throws CredentialsException, RuntimeException {
		/*
		 * Password is a string that may be set to empty.
		 * See:xx.realm.GeneralAuthorizingRealm#doAuthenticationInfo
		 */
		Assert.notNull(info, "Stored credentials info is null, please check configure");
		Assert.notNull(info.getCredentials(), "Stored credentials is null, please check configure");

		// Delegate validate
		if (delegate != null && !token.isResolved()) {
			return delegate.validate(resolves(token), info);
		}

		// Compare request credentials with storage credentials
		return MessageDigest.isEqual(toBytes(signature(token)), toBytes(info.getCredentials()));
	}

	@Override
	public String applySecret() {
		// Load secret keySpecPairs
		Integer index = getBindValue(KEY_SECRET_INDEX);
		if (index == null) {
			index = current().nextInt(0, config.getPreCryptPoolSize());
		}
		if (log.isDebugEnabled()) {
			log.debug("Apply secret for index: {}", index);
		}

		RSAKeySpecWrapper keyPair = rsaCryptoService.borrow(index);
		// Save the applied keyPair index.
		bind(KEY_SECRET_INDEX, index, config.getApplyPubkeyExpireMs());
		System.out.println((int)getBindValue(KEY_SECRET_INDEX));

		if (log.isInfoEnabled()) {
			log.info("Apply secret key is sessionId:{}, index:{}, publicKeyHexString:{}, privateKeyHexString:{}", getSessionId(),
					index, keyPair.getPubHexString(), keyPair.getHexString());
		}
		return keyPair.getPubHexString();
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
		RSAKeySpecWrapper keySpec = determineSecretKeySpecPair(token.getPrincipal());

		if (log.isInfoEnabled()) {
			String publicBase64String = keySpec.getPubHexString();
			String pattern = "The determined key pair is principal:[{}], publicKey:[{}], privateKey:[{}]";
			String privateBase64String = "Not output";
			boolean output = true;

			if (log.isDebugEnabled() || output) {
				privateBase64String = keySpec.getBase64String();
				log.debug(pattern, token.getPrincipal(), publicBase64String, privateBase64String);
			} else {
				log.info(pattern, token.getPrincipal(), publicBase64String, privateBase64String);
			}
		}

		// Mysterious DECRYPT them.
		final String plainCredentials = rsaCryptoService.decryptWithHex(keySpec, token.getCredentials());
		return new CredentialsToken(token.getPrincipal(), plainCredentials, true);
	}

	/**
	 * Determine asymmetric algorithms keyPair
	 * 
	 * @param checkCode
	 * @return
	 */
	private RSAKeySpecWrapper determineSecretKeySpecPair(@NotNull String principal) {
		// Choose the best one from the candidate key pair
		Integer index = getBindValue(KEY_SECRET_INDEX, true);
		if (index != null) {
			return rsaCryptoService.borrow(index);
		}

		if (log.isWarnEnabled()) {
			log.warn("Failed to decrypt, secretKey expired. seesionId:[{}], principal:[{}]", getSessionId(), principal);
		}
		throw new IllegalStateException(String.format("Invalid applied secretKey or expired. principal:[%s]", principal));
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
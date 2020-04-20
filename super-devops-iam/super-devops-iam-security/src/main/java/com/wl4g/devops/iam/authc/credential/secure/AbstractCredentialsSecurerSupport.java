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
package com.wl4g.devops.iam.authc.credential.secure;

import static java.security.MessageDigest.*;
import static java.util.Objects.isNull;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.CredentialsException;
import org.apache.shiro.codec.CodecSupport;
import org.apache.shiro.crypto.hash.Hash;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.ByteSource.Util;
import org.springframework.beans.factory.annotation.Autowired;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.*;
import static com.wl4g.devops.iam.common.utils.IamSecurityHolder.*;
import static com.wl4g.devops.tool.common.codec.CheckSums.*;
import static com.wl4g.devops.tool.common.lang.Assert2.*;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static io.netty.util.internal.ThreadLocalRandom.current;

import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.iam.common.cache.IamCacheManager;
import com.wl4g.devops.iam.common.i18n.SessionDelegateMessageBundle;
import com.wl4g.devops.iam.configure.SecureConfig;
import com.wl4g.devops.iam.crypto.SecureCryptService;
import com.wl4g.devops.iam.crypto.SecureCryptService.SecureAlgKind;
import com.wl4g.devops.tool.common.crypto.asymmetric.spec.KeyPairSpec;
import com.wl4g.devops.tool.common.log.SmartLogger;

/**
 * Abstract credentials securer adapter
 *
 * @author wangl.sir
 * @version v1.0 2019年3月11日
 * @see {@link org.apache.shiro.crypto.hash.DefaultHashService}
 * @since
 */
abstract class AbstractCredentialsSecurerSupport extends CodecSupport implements IamCredentialsSecurer {

	final protected SmartLogger log = getLogger(getClass());

	/**
	 * Secure configuration.
	 */
	final protected SecureConfig config;

	/**
	 * Credential cache manager.
	 */
	final protected IamCacheManager cacheManager;

	/**
	 * The 'private' part of the hash salt.
	 */
	final protected ByteSource privateSalt;

	/**
	 * Secure asymmetric cryptic service.
	 */
	@Autowired
	protected GenericOperatorAdapter<SecureAlgKind, SecureCryptService> cryptAdapter;

	/**
	 * I18n message source.
	 */
	@Resource(name = BEAN_DELEGATE_MSG_SOURCE)
	protected SessionDelegateMessageBundle bundle;

	/**
	 * Iam delegate credentials securer. (Extension: optional)
	 */
	@Autowired(required = false)
	protected CredentialsSecurerAdapter delegate;

	protected AbstractCredentialsSecurerSupport(SecureConfig config, IamCacheManager cacheManager) {
		notNullOf(config, "secureConfig");
		notNullOf(config.getPrivateSalt(), "privateSalt");
		notNullOf(config.getPreCryptPoolSize(), "cryptSize");
		notNullOf(config.getCryptosExpireMs() > 0, "cryptExpireMs");
		notNullOf(config.getApplyPubkeyExpireMs() > 0, "applyPubKeyExpireMs");
		notNullOf(cacheManager, "cacheManager");
		this.privateSalt = Util.bytes(config.getPrivateSalt());
		this.config = config;
		this.cacheManager = cacheManager;
	}

	@Override
	public String signature(@NotNull CredentialsToken token) {
		// Delegate signature
		if (!isNull(delegate) && !token.isSolved()) {
			// Resolving request credentials token.
			return delegate.signature(resolves(token));
		}

		// When the delegate is null, it is unresolved.
		if (!token.isSolved()) {
			token = resolves(token); // It is necessary to resolving
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
		notNullOf(info, "storedCredentials");
		notNullOf(info.getCredentials(), "storedCredentials");

		// Delegate validate.
		if (!isNull(delegate) && !token.isSolved()) {
			return delegate.validate(resolves(token), info);
		}

		// # Assertion compare request credentials & storage credentials.
		return isEqual(toBytes(signature(token)), toBytes(info.getCredentials()));
	}

	/**
	 * {@link super#applySecret(String)}
	 * 
	 * @param principal
	 * @return
	 * @see {@link com.wl4g.devops.iam.web.LoginAuthenticatorEndpoint#handhake()}
	 */
	@Override
	public String applySecret(@NotNull SecureAlgKind kind, @NotNull String principal) {
		// Check required sessionKey.
		checkSession();

		// Gets secretKey(publicKey) index.
		Integer index = getBindValue(KEY_SECRET_INFO);
		if (isNull(index)) {
			index = current().nextInt(0, config.getPreCryptPoolSize());
		}
		log.debug("Applied secretKey index: {}", index);

		// Gets applySecret keyPair index.
		KeyPairSpec keyPair = cryptAdapter.forOperator(kind).generateKeyBorrow(index);
		// Storage applied securet.
		bind(KEY_SECRET_INFO, index, config.getApplyPubkeyExpireMs());

		log.info("Applied secretKey of sessionId: {}, index: {}, pubKeyHexString: {}, privKeyHexString: {}", getSessionId(),
				index, keyPair.getPubHexString(), keyPair.getHexString());
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
	protected abstract ByteSource getPublicSalt(@NotBlank String principal);

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
		log.debug("Merge salt of principal: {}, salt: {}", token.getPrincipal(), salt);

		// Determine which hashing algorithm to use
		final String[] hashAlgorithms = config.getHashAlgorithms();
		final int size = hashAlgorithms.length;
		final long index = crc32(salt.getBytes()) % size & (size - 1);
		final String algorithm = hashAlgorithms[(int) index];
		final int hashIters = (int) (Integer.MAX_VALUE % (index + 1)) + 1;

		// Hashing signature
		return hasher.hashing(algorithm, Util.bytes(token.getCredentials()), salt, hashIters).toHex();
	}

	/**
	 * Corresponding to the front end, RSA1/RSA2/DSA/ECC/... encryption is used
	 * by default.
	 *
	 * @param cryptKind
	 * @param token
	 * @return
	 */
	protected CredentialsToken resolves(@NotNull CredentialsToken token) {
		// Determine keyPairSpec
		KeyPairSpec keyPairSpec = determineSecretKeySpecPair(token.getKind(), token.getPrincipal());

		if (log.isInfoEnabled()) {
			String publicBase64String = keyPairSpec.getPubHexString();

			String pattern = "Determined keypair is principal: {}, publicKey: {}, privateKey: {}";
			String privateBase64String = "******";
			if (log.isDebugEnabled()) {
				privateBase64String = keyPairSpec.getBase64String();
				log.debug(pattern, token.getPrincipal(), publicBase64String, privateBase64String);
			}
		}

		// Mysterious decryption them.
		final String plainCredentials = cryptAdapter.forOperator(token.getKind()).decrypt(keyPairSpec.getKeySpec(), token.getCredentials());
		return new CredentialsToken(token.getPrincipal(), plainCredentials, token.getKind(), true);
	}

	/**
	 * Determine asymmetric algorithms keyPair
	 *
	 * @param cryptKind
	 * @param principal
	 * @return
	 */
	private KeyPairSpec determineSecretKeySpecPair(@NotNull SecureAlgKind kind, @NotBlank String principal) {
		// Gets the best one from the candidate keyPair.
		Integer index = getBindValue(KEY_SECRET_INFO, true);
		if (!isNull(index)) {
			return cryptAdapter.forOperator(kind).generateKeyBorrow(index);
		}

		log.warn("Failed to decrypt, secretKey expired of seesionId: {}, principal: {}", getSessionId(), principal);
		throw new IllegalStateException(bundle.getMessage("AbstractCredentialsSecurerSupport.secretKey.expired"));
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
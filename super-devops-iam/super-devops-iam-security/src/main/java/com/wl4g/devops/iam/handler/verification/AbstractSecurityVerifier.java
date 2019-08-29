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
package com.wl4g.devops.iam.handler.verification;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.BEAN_DELEGATE_MSG_SOURCE;
import static com.wl4g.devops.iam.common.utils.SessionBindings.bind;
import static com.wl4g.devops.iam.common.utils.SessionBindings.getBindValue;
import static com.wl4g.devops.iam.common.utils.SessionBindings.unbind;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

import com.wl4g.devops.common.exception.iam.VerificationException;
import com.wl4g.devops.iam.common.cache.EnhancedCacheManager;
import com.wl4g.devops.iam.common.i18n.SessionDelegateMessageBundle;
import com.wl4g.devops.iam.config.IamProperties;
import com.wl4g.devops.iam.configure.ServerSecurityConfigurer;

/**
 * Abstract IAM verification handler
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月28日
 * @since
 */
public abstract class AbstractSecurityVerifier<T extends Serializable> implements SecurityVerifier<T> {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Server configuration properties
	 */
	@Autowired
	protected IamProperties config;

	/**
	 * IAM security configure handler
	 */
	@Autowired
	protected ServerSecurityConfigurer configurer;

	/**
	 * Enhanced cache manager.
	 */
	@Autowired
	protected EnhancedCacheManager cacheManager;

	/**
	 * Delegate message source.
	 */
	@Resource(name = BEAN_DELEGATE_MSG_SOURCE)
	protected SessionDelegateMessageBundle bundle;

	@Override
	public String verify(@NotNull List<String> factors, @NotNull T reqCode) throws VerificationException {
		Assert.isTrue(!CollectionUtils.isEmpty(factors), "Verify factors must not be empty");

		VerifyCodeWrapper<T> storedCode = null;
		try {
			/*
			 * If required is true, the forced verification policy is executed,
			 * otherwise the maximum retry policy check is performed (that is,
			 * the verification needs to be started only when the number of
			 * times is retried).
			 */
			if (!isEnabled(factors)) {
				return null; // not enabled
			}

			// Verification
			storedCode = getVerifyCode(true);
			if (!doMatch(storedCode, reqCode)) {
				log.error("Verification mismatched. {} => {}", reqCode, storedCode);
				throw new VerificationException(bundle.getMessage("AbstractVerification.verify.mismatch", reqCode));
			}

			// Storage verified token.
			String verifiedToken = randomAlphabetic(128);
			bind(getVerifiedTokenStoredKey(), verifiedToken, getVerifiedTokenExpireMs());
			if (log.isInfoEnabled()) {
				log.info("Saving to verified token: {}", verifiedToken);
			}
		} finally {
			if (storedCode != null) {
				postVerifyProperties(storedCode.getOwner());
			}
		}

		return null;
	}

	@Override
	public void validate(@NotNull List<String> factors, @NotNull String verifiedToken, boolean required)
			throws VerificationException {
		String storedVerifiedToken = getBindValue(getVerifiedTokenStoredKey(), true);
		Assert.hasText(storedVerifiedToken, bundle.getMessage("General.parameter.invalid"));
		Assert.state(StringUtils.equals(storedVerifiedToken, verifiedToken), bundle.getMessage("General.parameter.illegal"));
	}

	/**
	 * Post verification properties.
	 * 
	 * @param owner
	 *            Validate code owner(Optional).
	 */
	protected void postVerifyProperties(String owner) {
		reset(owner, false); // Reset or create
	}

	/**
	 * Reset the validate code to indicate a new generation when create is true
	 * 
	 * @param owner
	 *            Validate code owner(Optional).
	 * @param renew
	 *            is new create.
	 */
	protected void reset(String owner, boolean renew) {
		unbind(getVerifyCodeStoredKey());
		if (renew) {
			// Store verify-code in the session
			bind(getVerifyCodeStoredKey(), new VerifyCodeWrapper<T>(owner, generateCode()), getVerifyCodeExpireMs());
		}
	}

	/**
	 * Get stored verify code of session
	 * 
	 * @param assertion
	 *            Do you need to assertion
	 * @return Returns the currently valid verify-code (if create = true, the
	 *         newly generated value or the old value)
	 */
	protected VerifyCodeWrapper<T> getVerifyCode(boolean assertion) {
		// Already created verify-code
		VerifyCodeWrapper<T> code = getBindValue(getVerifyCodeStoredKey());
		if (code != null && code.getCode() != null) { // Assertion
			return code;
		}

		if (assertion) {
			long now = System.currentTimeMillis();
			log.warn("Assertion verifyCode expired. now: {}, createTime: {}, expireMs: {}", now,
					(code != null ? code.getCreateTime() : null), getVerifyCodeExpireMs());
			throw new VerificationException(bundle.getMessage("AbstractVerification.verify.expired"));
		}
		return null;
	}

	/**
	 * Match submitted validation code
	 * 
	 * @param storedCode
	 * @param reqCode
	 * @return
	 */
	protected boolean doMatch(VerifyCodeWrapper<T> storedCode, T reqCode) {
		if (Objects.isNull(reqCode)) {
			return false;
		}
		return String.valueOf(storedCode.getCode()).equalsIgnoreCase(String.valueOf(reqCode));
	}

	/**
	 * Generate verify code
	 * 
	 * @return Verify code object
	 */
	@SuppressWarnings("unchecked")
	protected T generateCode() {
		return (T) randomAlphabetic(5); // By-default
	}

	/**
	 * Check the number of attempts to apply.
	 * 
	 * @param request
	 * @param response
	 * @param factors
	 *            Safety limiting factor(e.g. Client remote IP and login
	 *            user-name)
	 */
	protected abstract void checkApplyAttempts(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
			@NotNull List<String> factors);

	/**
	 * Validity of the verification code (in milliseconds).
	 * 
	 * @return
	 */
	protected abstract long getVerifyCodeExpireMs();

	/**
	 * Validity of the verified token (in milliseconds).
	 * 
	 * @return
	 */
	protected abstract long getVerifiedTokenExpireMs();

	/**
	 * Get verification code stored sessionKey.
	 * 
	 * @return
	 */
	private String getVerifyCodeStoredKey() {
		return "VERIFY_CODE." + verifyType().name();
	}

	/**
	 * Get verification code stored sessionKey.
	 * 
	 * @return
	 */
	private String getVerifiedTokenStoredKey() {
		return "VERIFIED_TOKEN." + verifyType().name();
	}

	/**
	 * Wrapper verify code
	 * 
	 * @author wangl.sir
	 * @version v1.0 2019年4月18日
	 * @since
	 */
	public static class VerifyCodeWrapper<T> implements Serializable {
		private static final long serialVersionUID = -7643664591972701966L;

		/**
		 * Authentication code owners, i.e. applicants, such as UUID, session
		 * Id, principal
		 */
		private String owner;

		/**
		 * Value of verification code data.
		 */
		private T code;

		/**
		 * Verification code creation time.
		 */
		private Long createTime;

		public VerifyCodeWrapper(T code) {
			this(null, code, System.currentTimeMillis());
		}

		public VerifyCodeWrapper(String owner, T code) {
			this(owner, code, System.currentTimeMillis());
		}

		public VerifyCodeWrapper(String owner, T code, Long createTime) {
			Assert.notNull(code, "Verify code is null, please check configure");
			Assert.notNull(createTime, "CreateTime is null, please check configure");
			this.owner = owner;
			this.code = code;
			this.createTime = createTime;
		}

		public String getOwner() {
			return owner;
		}

		public void setOwner(String owner) {
			this.owner = owner;
		}

		public T getCode() {
			return code;
		}

		public void setCode(T code) {
			this.code = code;
		}

		public Long getCreateTime() {
			return createTime;
		}

		public void setCreateTime(Long createTime) {
			this.createTime = createTime;
		}

		@Override
		public String toString() {
			return "VerifyCodeWrapper [owner=" + owner + ", code=" + code + ", createTime=" + createTime + "]";
		}

	}

}
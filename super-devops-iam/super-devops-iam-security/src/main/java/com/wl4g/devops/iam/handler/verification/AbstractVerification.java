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
package com.wl4g.devops.iam.handler.verification;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.BEAN_DELEGATE_MSG_SOURCE;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;

import com.wl4g.devops.common.exception.iam.VerificationException;
import com.wl4g.devops.iam.common.cache.EnhancedCacheManager;
import com.wl4g.devops.iam.common.i18n.SessionDelegateMessageBundle;
import com.wl4g.devops.iam.common.utils.Sessions;
import com.wl4g.devops.iam.config.IamProperties;
import com.wl4g.devops.iam.config.BasedContextConfiguration.IamContextManager;
import com.wl4g.devops.iam.context.ServerSecurityContext;

/**
 * Abstract IAM verification handler
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月28日
 * @since
 */
public abstract class AbstractVerification implements Verification {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * IAM security context handler
	 */
	final protected ServerSecurityContext context;

	/**
	 * Server configuration properties
	 */
	@Autowired
	protected IamProperties config;

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

	public AbstractVerification(IamContextManager manager) {
		Assert.notNull(manager, "'manager' must not be null");
		this.context = manager.getServerSecurityContext();
	}

	@Override
	public void validate(@NotNull List<String> factors, String verifyCodeReq, boolean required) throws VerificationException {
		Assert.isTrue(!CollectionUtils.isEmpty(factors), "factors must not be empty");

		try {
			/*
			 * If required is true, the forced verification policy is executed,
			 * otherwise the maximum retry policy check is performed (that is,
			 * the verification needs to be started only when the number of
			 * times is retried).
			 */
			if (!(required || isEnabled(factors))) {
				return; // not enabled
			}
			// Store the verify-code
			Object verifyCode = getVerifyCode(true);
			if (!doMatching(verifyCode, verifyCodeReq)) {
				if (log.isErrorEnabled()) {
					log.error("verification mismatch. {} => {}", verifyCodeReq, verifyCode);
				}
				throw new VerificationException(bundle.getMessage("AbstractVerification.verify.mismatch", verifyCodeReq));
			}

		} finally {
			postValidateFinallySet();
		}
	}

	/**
	 * Post validation finally processed.
	 * 
	 */
	protected void postValidateFinallySet() {
		reset(false); // Reset or create
	}

	/**
	 * Reset the verify-code to indicate a new generation when create is true
	 * 
	 * @param create
	 */
	protected void reset(boolean create) {
		getSession().removeAttribute(storageSessionKey());
		if (create) {
			// Store verify-code in the session
			getSession().setAttribute(storageSessionKey(), new VerifyCode(generateCode()));
		}
	}

	/**
	 * Get stored verify-code of session
	 * 
	 * @param assertion
	 *            Do you need to assertion
	 * @return Returns the currently valid verify-code (if create = true, the
	 *         newly generated value or the old value)
	 */
	protected VerifyCode getVerifyCode(boolean assertion) {
		// Already created verify-code
		VerifyCode code = (VerifyCode) getSession().getAttribute(storageSessionKey());

		// Assertion
		long now = System.currentTimeMillis();
		if (code != null && !isBlank(code.getText())) {
			if (Math.abs(now - code.getCreateTime()) < getExpireMs()) { // Expired?
				return code;
			}
		}

		if (assertion) {
			log.warn("Assertion verify-code expired. now: {}, createTime: {}, expireMs: {}", now,
					(code != null ? code.getCreateTime() : null), getExpireMs());
			throw new VerificationException(bundle.getMessage("AbstractVerification.verify.expired"));
		}

		return null;
	}

	/**
	 * Match submitted verification code
	 * 
	 * @param verifyCode
	 * @param verifyCodeReq
	 * @return
	 */
	/**
	 * @param verifyCode
	 * @param verifyCodeReq
	 * @return
	 */
	protected boolean doMatching(Object verifyCode, String verifyCodeReq) {
		if (StringUtils.isEmpty(verifyCodeReq)) {
			return false;
		}

		if (verifyCode instanceof VerifyCode) {
			VerifyCode vc = (VerifyCode) verifyCode;
			return equalsIgnoreCase(vc.getText(), (CharSequence) verifyCodeReq);
		}

		return equalsIgnoreCase(String.valueOf(verifyCode), verifyCodeReq);
	}

	/**
	 * Generate verify-code text
	 * 
	 * @return
	 */
	protected String generateCode() {
		return randomAlphabetic(5);
	}

	/**
	 * Stored verification code sessionKey.
	 * 
	 * @return
	 */
	protected abstract String storageSessionKey();

	/**
	 * Validity of the verification code (in milliseconds).
	 * 
	 * @return
	 */
	protected abstract long getExpireMs();

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
	 * Get SHIRO session
	 * 
	 * @return
	 */
	private Session getSession() {
		return Sessions.getSession();
	}

	/**
	 * Wrap verification code
	 * 
	 * @author wangl.sir
	 * @version v1.0 2019年4月18日
	 * @since
	 */
	public static class VerifyCode implements Serializable {
		private static final long serialVersionUID = -7643664591972701966L;

		private String text;

		private Long createTime;

		public VerifyCode(String text) {
			this(text, System.currentTimeMillis());
		}

		public VerifyCode(String text, Long createTime) {
			Assert.hasText(text, "Verify-code text is empty, please check configure");
			Assert.notNull(createTime, "CreateTime is null, please check configure");
			this.text = text;
			this.createTime = createTime;
		}

		public String getText() {
			return text;
		}

		public void setText(String verifyText) {
			this.text = verifyText;
		}

		public Long getCreateTime() {
			return createTime;
		}

		public void setCreateTime(Long timestamp) {
			this.createTime = timestamp;
		}

		@Override
		public String toString() {
			return "VerifyCodeWrap [verifyText=" + text + ", timestamp=" + createTime + "]";
		}

	}

}
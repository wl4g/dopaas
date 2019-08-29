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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

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
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.shiro.web.util.WebUtils.getCleanParam;

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
public abstract class AbstractSecurityVerifier implements SecurityVerifier {

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
	public String verify(@NotNull List<String> factors, @NotNull HttpServletRequest request) throws VerificationException {
		Assert.isTrue(!CollectionUtils.isEmpty(factors), "factors must not be empty");

		VerifyCode storedAttachCode = null;
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

			// Request attach code
			String reqAttachCode = getCleanParam(request, config.getParam().getAttachCodeName());
			// Store validate code
			storedAttachCode = getVerifyCode(true);
			if (!doMatch(storedAttachCode, reqAttachCode)) {
				if (log.isErrorEnabled()) {
					log.error("Verification mismatched. {} => {}", reqAttachCode, storedAttachCode);
				}
				throw new VerificationException(bundle.getMessage("AbstractVerification.verify.mismatch", reqAttachCode));
			}
		} finally {
			postVerifyProperties((storedAttachCode != null) ? storedAttachCode.getText() : null);
		}

		return null;
	}

	@Override
	public void validate(@NotNull List<String> factors, @NotNull String verifyToken, boolean required)
			throws VerificationException {

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
		unbind(storedSessionKey());
		if (renew) {
			// Store verify-code in the session
			bind(storedSessionKey(), new VerifyCode(owner, generateCode()));
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
	protected VerifyCode getVerifyCode(boolean assertion) {
		// Already created verify-code
		VerifyCode code = getBindValue(storedSessionKey());

		// Assertion
		long now = System.currentTimeMillis();
		if (code != null && isNotBlank(code.getText())) {
			if ((now - code.getCreateTime()) < getExpireMs()) { // Expired?
				return code;
			}
		}
		if (assertion) {
			log.warn("Assertion verifyCode expired. now: {}, createTime: {}, expireMs: {}", now,
					(code != null ? code.getCreateTime() : null), getExpireMs());
			throw new VerificationException(bundle.getMessage("AbstractVerification.verify.expired"));
		}
		return null;
	}

	/**
	 * Match submitted validation code
	 * 
	 * @param storedAttachCode
	 * @param reqAttachCode
	 * @return
	 */
	protected boolean doMatch(Object storedAttachCode, String reqAttachCode) {
		if (isBlank(reqAttachCode)) {
			return false;
		}
		if (storedAttachCode instanceof VerifyCode) {
			VerifyCode vc = (VerifyCode) storedAttachCode;
			return equalsIgnoreCase(vc.getText(), (CharSequence) reqAttachCode);
		}
		return equalsIgnoreCase(String.valueOf(storedAttachCode), reqAttachCode);
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
	protected abstract String storedSessionKey();

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
	 * Wrap validation code
	 * 
	 * @author wangl.sir
	 * @version v1.0 2019年4月18日
	 * @since
	 */
	public static class VerifyCode implements Serializable {
		private static final long serialVersionUID = -7643664591972701966L;

		private Object owner;

		private String text;

		private Long createTime;

		public VerifyCode(String text) {
			this(null, text, System.currentTimeMillis());
		}

		public VerifyCode(Object owner, String text) {
			this(owner, text, System.currentTimeMillis());
		}

		public VerifyCode(Object owner, String text, Long createTime) {
			Assert.hasText(text, "Validte code value is empty, please check configure");
			Assert.notNull(createTime, "CreateTime is null, please check configure");
			this.owner = owner;
			this.text = text;
			this.createTime = createTime;
		}

		public Object getOwner() {
			return owner;
		}

		public void setOwner(Object owner) {
			this.owner = owner;
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
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
package com.wl4g.devops.iam.verification;

import static org.apache.shiro.web.util.WebUtils.getCleanParam;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.shiro.util.Assert;
import com.wl4g.devops.common.exception.iam.VerificationException;
import com.wl4g.devops.common.framework.operator.Operator;
import static com.wl4g.devops.iam.verification.SecurityVerifier.VerifyKind;

/**
 * Verification handler
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月28日
 * @since
 */
public interface SecurityVerifier extends Operator<VerifyKind> {

	/**
	 * New create verification meta information.
	 *
	 * @param owner
	 *            Validate code owner(Optional).
	 * @param factors
	 *            Safety limiting factor(e.g. Client remote IP and login
	 *            user-name)
	 * @param request
	 *            HttpServletRequest
	 * @return apply meta information.
	 * @throws IOException
	 */
	Object apply(String owner, @NotNull List<String> factors, @NotNull HttpServletRequest request) throws IOException;

	/**
	 * Check whether validation code is turned on
	 *
	 * @param factors
	 *            Safety limiting factor(e.g. Client remote IP and login
	 *            user-name)
	 * @return Return true if the current login account name or principal needs
	 *         to login with authentication number
	 */
	boolean isEnabled(@NotNull List<String> factors);

	/**
	 * Get verification code
	 *
	 * @param assertion
	 *            If assertion is enabled, an exception is thrown when the
	 *            authentication code is not obtained
	 * @return
	 */
	VerifyCodeWrapper getVerifyCode(boolean assertion);

	/**
	 * Analyze and verification.
	 *
	 * @param params
	 *            parameter DTO model
	 * @param request
	 *            HttpServletRequest
	 * @param factors
	 *            Safety limiting factor(e.g. Client remote IP and login name)
	 * @return If the check is successful, return <b>verifiedToken</b>. If no
	 *         validation is required, the token credentials will be returned to
	 *         null, otherwise the exception will be thrown.
	 * @throws VerificationException
	 */
	String verify(@NotBlank String params, @NotNull HttpServletRequest request, @NotNull List<String> factors)
			throws VerificationException;

	/**
	 * Validation verified token.
	 *
	 * @param factors
	 *            Safety limiting factor(e.g. Client remote IP and login
	 *            user-name)
	 * @param verifiedToken
	 *            The token verified in the previous step. See:
	 *            {@link #verify(List, HttpServletRequest, boolean)}
	 * @param required
	 *            Whether it is necessary to validation (ignore the retry failed
	 *            cumulative amount)
	 * @throws VerificationException
	 */
	void validate(@NotNull List<String> factors, @NotNull String verifiedToken, boolean required) throws VerificationException;

	/**
	 * Verification type definition.
	 *
	 * @author Wangl.sir
	 * @version v1.0 2019年8月29日
	 * @since
	 */
	public static enum VerifyKind {

		GRAPH_SIMPLE("VerifyWithSimpleGraph"),

		GRAPH_GIF("VerifyWithGifGraph"),

		GRAPH_JIGSAW("VerifyWithJigsawGraph"),

		TEXT_SMS("VerifyWithSmsText");

		/**
		 * Request verify type parameter name.
		 */
		final public static String PARAM_VERIFYTYPE = "verifyType";

		/**
		 * Support verify type.
		 */
		final public static String SUPPORT_ALL = supportAsString();

		/**
		 * Verifier type alias value.
		 */
		final private String alias;

		private VerifyKind(String alias) {
			this.alias = alias;
		}

		public String getAlias() {
			return alias;
		}

		public static VerifyKind of(String type) {
			Assert.hasText(type, String.format("Parameter '%s' is required.", PARAM_VERIFYTYPE));
			for (VerifyKind t : values()) {
				if (t.getAlias().equals(type) || t.name().equals(type)) {
					return t;
				}
			}
			throw new IllegalArgumentException(String.format("Invalid verify type '%s'", type));
		}

		public static VerifyKind of(HttpServletRequest request) {
			return of(getCleanParam(request, PARAM_VERIFYTYPE));
		}

		public static VerifyKind of(HttpServletRequest request, String paramName) {
			return of(getCleanParam(request, paramName));
		}

		/**
		 * Get support verify type.
		 *
		 * @return
		 */
		private static String supportAsString() {
			StringBuffer support = new StringBuffer();
			Iterator<VerifyKind> it = Arrays.asList(values()).iterator();
			while (it.hasNext()) {
				VerifyKind v = it.next();
				if (v == TEXT_SMS) {
					continue;
				}
				support.append(v.getAlias());
				if (it.hasNext()) {
					support.append(",");
				}
			}
			return support.toString();
		}

	}

	/**
	 * Wrapper verify code
	 *
	 * @author wangl.sir
	 * @version v1.0 2019年4月18日
	 * @since
	 */
	public static class VerifyCodeWrapper implements Serializable {
		private static final long serialVersionUID = -7643664591972701966L;

		/**
		 * (Optional) Authentication code owners, i.e. applicants, such as UUID,
		 * session Id, principal
		 */
		private String owner;

		/**
		 * Value of verification code data.
		 */
		private Object code;

		/**
		 * Verification code creation time.
		 */
		private Long createTime;

		public VerifyCodeWrapper(Object code) {
			this(null, code, System.currentTimeMillis());
		}

		public VerifyCodeWrapper(String owner, Object code) {
			this(owner, code, System.currentTimeMillis());
		}

		public VerifyCodeWrapper(String owner, Object code, Long createTime) {
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

		@SuppressWarnings({ "unchecked" })
		public <T> T getCode() {
			return (T) code;
		}

		public void setCode(Object code) {
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

		/**
		 * Get remaining delay.
		 *
		 * @param code
		 * @return
		 */
		public long getRemainDelay(long delayMs) {
			// remainMs = NowTime - CreateTime - DelayTime
			long now = System.currentTimeMillis();
			return Math.max(delayMs - (now - getCreateTime()), 0);
		}

	}

}
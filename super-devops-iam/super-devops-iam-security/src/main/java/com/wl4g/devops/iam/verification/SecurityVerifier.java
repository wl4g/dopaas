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
package com.wl4g.devops.iam.verification;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.apache.shiro.util.Assert;

import com.wl4g.devops.common.exception.iam.VerificationException;

/**
 * Verification handler
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月28日
 * @since
 */
public abstract interface SecurityVerifier<T extends Serializable> {

	/**
	 * Verifier type definition.
	 * 
	 * @return
	 */
	VerifyType verifyType();

	/**
	 * New apply and output a verification code
	 * 
	 * @param owner
	 *            Validate code owner(Optional).
	 * @param factors
	 *            Safety limiting factor(e.g. Client remote IP and login
	 *            user-name)
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @throws IOException
	 */
	void apply(String owner, @NotNull List<String> factors, @NotNull HttpServletRequest request,
			@NotNull HttpServletResponse response) throws IOException;

	VerifyCodeWrapper<T> getVerifyCode(boolean assertion);

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
	 * PreCheck and verification of additional code.
	 * 
	 * @param factors
	 *            Safety limiting factor(e.g. Client remote IP and login
	 *            user-name)
	 * @param reqCode
	 *            Request verify code.
	 * @return If the check is successful, the token credentials will be
	 *         returned. If no validation is required, the token credentials
	 *         will be returned to null, otherwise the exception will be thrown.
	 * @throws VerificationException
	 */
	String verify(@NotNull List<String> factors, @NotNull T reqCode) throws VerificationException;

	/**
	 * Validation front-end verified token.
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
	public static enum VerifyType {

		GRAPH_DEFAULT("VerifyWithDefaultGraph"),

		GRAPH_SIMPLE("VerifyWithSimpleGraph"),

		GRAPH_GIF("VerifyWithGifGraph"),

		GRAPH_JIGSAW("VerifyWithJigsawGraph"),

		TEXT_SMS("VerifyWithSmsText");

		final private String alias;

		private VerifyType(String alias) {
			this.alias = alias;
		}

		public String getAlias() {
			return alias;
		}

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
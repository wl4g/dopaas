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

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import com.wl4g.devops.common.exception.iam.VerificationException;

/**
 * Verification code handler
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月28日
 * @since
 */
public abstract interface Verification {

	/**
	 * Check Front-end verification code
	 * 
	 * @param factors
	 *            Safety limiting factor(e.g. Client remote IP and login
	 *            user-name)
	 * @param verifyCodeReq
	 *            Submitted authentication code token
	 * @param required
	 *            Whether it is necessary to validation (ignore the retry failed
	 *            cumulative amount)
	 * @throws VerificationException
	 */
	default void validate(@NotNull List<String> factors, String verifyCodeReq, boolean required) throws VerificationException {
		throw new UnsupportedOperationException();
	}

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
	default void apply(Object owner, @NotNull List<String> factors, @NotNull HttpServletRequest request,
			@NotNull HttpServletResponse response) throws IOException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Check whether validation code is turned on
	 * 
	 * @param factors
	 *            Safety limiting factor(e.g. Client remote IP and login
	 *            user-name)
	 * @return Return true if the current login account name or principal needs
	 *         to login with authentication number
	 */
	default boolean isEnabled(@NotNull List<String> factors) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Verification type definition.
	 * 
	 * @author Wangl.sir
	 * @version v1.0 2019年8月29日
	 * @since
	 */
	public static enum VerifyType {

		GRAPH_JPEG,

		GRAPH_GIF,

		GRAPH_JIGSAW,

		SMS;

	}

}
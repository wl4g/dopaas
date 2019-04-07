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
package com.wl4g.devops.iam.handler;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.wl4g.devops.common.exception.iam.CaptchaException;

/**
 * Captcha handler
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月28日
 * @since
 */
public abstract interface CaptchaHandler {

	/**
	 * Check Front-end Verification Code
	 * 
	 * @param conditions
	 * @param captchaReq
	 *            Submitted authentication code token
	 * @throws CaptchaException
	 */
	default void validate(@NotNull List<String> conditions, String captchaReq) throws CaptchaException {
		throw new UnsupportedOperationException();
	}

	/**
	 * New apply and output a verification code
	 * 
	 * @param response
	 *            HttpServletResponse
	 * @throws IOException
	 */
	default void apply(HttpServletResponse response) throws IOException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Reset the captcah to indicate a new generation when create is true
	 * 
	 * @param create
	 * @return Returns the currently valid captcha (if create = true, the newly
	 *         generated value or the old value)
	 */
	default String reset(boolean create) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Number of cumulative processing (e.g. to limit the number of login
	 * failures or the number of short messages sent by homologous IP requests)
	 * 
	 * @param conditions
	 *            e.g. Client remote IP and login username.
	 * @param incrementBy
	 *            Step increment value
	 */
	default Long accumulative(@NotNull List<String> conditions, long incrementBy) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Get the cumulative number of failures for the specified condition
	 * 
	 * @param condition
	 *            e.g. Client remote IP or login username.
	 */
	default Long getCumulative(@NotBlank String condition) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets the cumulative number of failures for the specified condition
	 * 
	 * @param conditions
	 *            e.g. Client remote IP and login username.
	 */
	default Long getCumulatives(@NotNull List<String> conditions) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Cancel captcha validation
	 * 
	 * @param conditions
	 */
	default void cancel(@NotNull List<String> conditions) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Check whether validation code is turned on
	 * 
	 * @param conditions
	 * @return Return true if the current login account name or principal needs
	 *         to login with authentication number
	 */
	default boolean isEnabled(@NotNull List<String> conditions) {
		throw new UnsupportedOperationException();
	}

}
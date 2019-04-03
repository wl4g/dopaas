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

import javax.servlet.http.HttpServletResponse;

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
	 * @param principal
	 * @param requestCaptcha
	 *            Submitted authentication code token
	 * @throws CaptchaException
	 */
	void validate(String principal, String requestCaptcha) throws CaptchaException;

	/**
	 * New apply and output a verification code
	 * 
	 * @param resp
	 *            HttpServletResponse
	 * @throws IOException
	 */
	void apply(HttpServletResponse resp) throws IOException;

	/**
	 * Reset the captcah to indicate a new generation when create is true
	 * 
	 * @param create
	 * @return Returns the currently valid captcha (if create = true, the newly
	 *         generated value or the old value)
	 */
	String reset(boolean create);

	/**
	 * Number of cumulative processing (e.g. to limit the number of login
	 * failures or the number of short messages sent by homologous IP requests)
	 * 
	 * @param principal
	 * @param value
	 */
	Long accumulative(String principal, long value);

	/**
	 * Get the amount of accumulated processing
	 * 
	 * @param principal
	 */
	Long getCumulative(String principal);

	/**
	 * Cancel captcha validation
	 * 
	 * @param principal
	 */
	void cancel(String principal);

	/**
	 * Check whether validation code is turned on
	 * 
	 * @param principal
	 * @return Return true if the current login account name or principal needs
	 *         to login with authentication number
	 */
	boolean isEnabled(String principal);

}
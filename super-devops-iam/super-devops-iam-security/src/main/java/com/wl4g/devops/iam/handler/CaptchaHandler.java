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

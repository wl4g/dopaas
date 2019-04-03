package com.wl4g.devops.iam.authc;

/**
 * Captcha authentication token
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月19日
 * @since
 */
public interface CaptchaAuthenticationToken {

	/**
	 * Get submission captcha
	 * 
	 * @return
	 */
	String getCaptcha();

}

package org.springframework.social.wechat.api;

/**
 * spring-social-wechat
 * 
 * @author <a href="mailto:larry7696@gmail.com">Larry</a>
 * @version 18.6.27
 */
public class AuthReq {

	public String accessToken;
	public String openid;

	public AuthReq() {
		super();
	}

	public AuthReq(String accessToken, String openid) {
		super();
		this.accessToken = accessToken;
		this.openid = openid;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

}

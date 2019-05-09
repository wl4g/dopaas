package org.springframework.social.wechat.api;

/**
 * spring-social-wechat
 * 
 * @author <a href="mailto:larry7696@gmail.com">Larry</a>
 * @version 18.6.27
 */
public class AccessTokenRes {

	private String accessToken;
	private String expiresIn;
	private String refreshToken;
	private String openid;
	private String scope;
	private String unionid;

	public AccessTokenRes() {
		super();
	}

	public AccessTokenRes(String accessToken, String expiresIn, String refreshToken, String openid, String scope,
			String unionid) {
		super();
		this.accessToken = accessToken;
		this.expiresIn = expiresIn;
		this.refreshToken = refreshToken;
		this.openid = openid;
		this.scope = scope;
		this.unionid = unionid;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(String expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

}

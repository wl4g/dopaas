package org.springframework.social.wechat.connect;

import org.springframework.social.oauth2.AccessGrant;

/**
 * spring-social-wechat
 * 
 * @author <a href="mailto:larry7696@gmail.com">Larry</a>
 * @version 18.6.27
 */
public class WechatAccessGrant extends AccessGrant {

	private static final long serialVersionUID = 1L;

	private String openid;

	private String unionid;

	public WechatAccessGrant(String accessToken, String scope, String refreshToken, Long expiresIn, String openId,
			String unionId) {
		super(accessToken, scope, refreshToken, expiresIn);
		this.openid = openId;
		this.unionid = unionId;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

}

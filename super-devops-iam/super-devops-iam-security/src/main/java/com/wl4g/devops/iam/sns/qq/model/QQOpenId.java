package com.wl4g.devops.iam.sns.qq.model;

import org.apache.shiro.util.Assert;

import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.iam.sns.support.Oauth2OpenId;

public class QQOpenId implements Oauth2OpenId {

	private String client_id;
	private String openid;

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	@Override
	public String openId() {
		return getOpenid();
	}

	@Override
	public String unionId() {
		// throw new UnsupportedOperationException();
		return null;
	}

	@Override
	public String toString() {
		return "OpenId [" + (client_id != null ? "client_id=" + client_id + ", " : "")
				+ (openid != null ? "openid=" + openid : "") + "]";
	}

	@SuppressWarnings("unchecked")
	public QQOpenId build(String message) {
		Assert.notNull(message, "'message' must not be null");
		return JacksonUtils.parseJSON(cleanStringToken(message), QQOpenId.class);
	}

	private String cleanStringToken(String msg) {
		// callback(
		// {"client_id":"101525381","openid":"6725F3D4CCC904450110FBE01D4A6667"}
		// );
		String cleanPrefix = msg.substring(9, msg.length());
		return cleanPrefix.substring(0, cleanPrefix.length() - 3);
	}

}

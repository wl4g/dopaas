package com.wl4g.devops.iam.sns.wechat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wl4g.devops.iam.sns.support.Oauth2OpenId;

public class WxBasedOpenId extends WxBasedResponse implements Oauth2OpenId {
	private static final long serialVersionUID = 7684131680589315985L;

	@JsonProperty("openid")
	private String openId;

	@JsonProperty("unionid")
	private String unionId;

	public WxBasedOpenId() {
		super();
	}

	public WxBasedOpenId(String openId, String unionId) {
		super();
		this.setOpenId(openId);
		this.setUnionId(unionId);
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getUnionId() {
		return unionId;
	}

	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}

	@Override
	public String openId() {
		return this.getOpenId();
	}

	@Override
	public String unionId() {
		return this.getUnionId();
	}

	@Override
	public <O extends Oauth2OpenId> O build(String message) {
		throw new UnsupportedOperationException();
	}

}

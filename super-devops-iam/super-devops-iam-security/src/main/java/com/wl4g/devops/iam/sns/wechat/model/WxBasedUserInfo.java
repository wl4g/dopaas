package com.wl4g.devops.iam.sns.wechat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.iam.sns.support.Oauth2UserProfile;

public class WxBasedUserInfo extends WxBasedResponse implements Oauth2UserProfile {
	private static final long serialVersionUID = 843944424065492261L;

	@JsonProperty("openid")
	private String openid;

	@JsonProperty("nickname")
	private String nickname;

	@JsonProperty("sex")
	private String sex;

	@JsonProperty("province")
	private String province;

	@JsonProperty("language")
	private String language;

	@JsonProperty("city")
	private String city;

	@JsonProperty("country")
	private String country;

	@JsonProperty("headimgurl")
	private String headimgUrl;

	@JsonProperty("unionid")
	private String unionId;

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getHeadimgUrl() {
		return headimgUrl;
	}

	public void setHeadimgUrl(String headimgurl) {
		this.headimgUrl = headimgurl;
	}

	public String getUnionId() {
		return unionId;
	}

	public void setUnionId(String unionid) {
		this.unionId = unionid;
	}

	@SuppressWarnings("unchecked")
	@Override
	public WxBasedUserInfo build(String message) {
		return JacksonUtils.parseJSON(message, WxBasedUserInfo.class);
	}

}

/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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
package com.wl4g.devops.iam.sns.wechat.model;

import static com.wl4g.devops.tool.common.serialize.JacksonUtils.parseJSON;
import static java.lang.String.format;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wl4g.devops.common.exception.iam.SnsApiBindingException;
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
		return parseJSON(message, WxBasedUserInfo.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public WxBasedUserInfo validate() {
		if (getErrcode() != DEFAULT_WX_OK) {
			throw new SnsApiBindingException(format("[Assertion failed] - WeChat userinfo of %s", toString()));
		}
		return this;
	}

}
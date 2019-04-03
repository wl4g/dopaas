package org.springframework.social.wechat.api;

import java.util.List;

/**
 * spring-social-wechat
 * 
 * @author <a href="mailto:larry7696@gmail.com">Larry</a>
 * @version 18.6.27
 */
public class User {

	private String openid;
	private String nickname;
	private Integer sex; // 1是男性，2是女性，0是未知
	private String language;
	private String province;
	private String city;
	private String country;
	private String headimgurl;
	private List<String> privilege;
	private String unionid;

	public User() {
		super();
	}

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

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
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

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public List<String> getPrivilege() {
		return privilege;
	}

	public void setPrivilege(List<String> privilege) {
		this.privilege = privilege;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

	@Override
	public String toString() {
		return "User [" + (openid != null ? "openid=" + openid + ", " : "")
				+ (nickname != null ? "nickname=" + nickname + ", " : "") + (sex != null ? "sex=" + sex + ", " : "")
				+ (language != null ? "language=" + language + ", " : "")
				+ (province != null ? "province=" + province + ", " : "") + (city != null ? "city=" + city + ", " : "")
				+ (country != null ? "country=" + country + ", " : "")
				+ (headimgurl != null ? "headimgurl=" + headimgurl + ", " : "")
				+ (privilege != null ? "privilege=" + privilege + ", " : "") + (unionid != null ? "unionid=" + unionid : "")
				+ "]";
	}

}

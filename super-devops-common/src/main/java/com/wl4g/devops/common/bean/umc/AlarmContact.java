package com.wl4g.devops.common.bean.umc;

import com.wl4g.devops.common.bean.BaseBean;

import java.io.Serializable;

public class AlarmContact extends BaseBean implements Serializable {
	private static final long serialVersionUID = 381411777614066880L;

	private String name;

	private String email;

	private String phone;

	private String dingtalk;

	private String wechat;

	private String twitter;

	private String facebook;

	private Integer emailEnable;

	private Integer phoneEnable;

	private Integer dingtalkEnable;

	private Integer wechatEnable;

	private Integer twitterEnable;

	private Integer facebookEnable;

	private Integer phoneTimeOfFreq;

	private Integer dingtalkTimeOfFreq;

	private Integer wechatTimeOfFreq;

	private Integer twitterTimeOfFreq;

	private Integer facebookTimeOfFreq;

	private Integer phoneNumOfFreq;

	private Integer dingtalkNumOfFreq;

	private Integer wechatNumOfFreq;

	private Integer twitterNumOfFreq;

	private Integer facebookNumOfFreq;

	private Integer[] groups;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email == null ? null : email.trim();
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone == null ? null : phone.trim();
	}

	public String getDingtalk() {
		return dingtalk;
	}

	public void setDingtalk(String dingtalk) {
		this.dingtalk = dingtalk == null ? null : dingtalk.trim();
	}

	public String getWechat() {
		return wechat;
	}

	public void setWechat(String wechat) {
		this.wechat = wechat == null ? null : wechat.trim();
	}

	public String getTwitter() {
		return twitter;
	}

	public void setTwitter(String twitter) {
		this.twitter = twitter == null ? null : twitter.trim();
	}

	public String getFacebook() {
		return facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook == null ? null : facebook.trim();
	}

	public Integer getEmailEnable() {
		return emailEnable;
	}

	public void setEmailEnable(Integer emailEnable) {
		this.emailEnable = emailEnable;
	}

	public Integer getPhoneEnable() {
		return phoneEnable;
	}

	public void setPhoneEnable(Integer phoneEnable) {
		this.phoneEnable = phoneEnable;
	}

	public Integer getDingtalkEnable() {
		return dingtalkEnable;
	}

	public void setDingtalkEnable(Integer dingtalkEnable) {
		this.dingtalkEnable = dingtalkEnable;
	}

	public Integer getWechatEnable() {
		return wechatEnable;
	}

	public void setWechatEnable(Integer wechatEnable) {
		this.wechatEnable = wechatEnable;
	}

	public Integer getTwitterEnable() {
		return twitterEnable;
	}

	public void setTwitterEnable(Integer twitterEnable) {
		this.twitterEnable = twitterEnable;
	}

	public Integer getFacebookEnable() {
		return facebookEnable;
	}

	public void setFacebookEnable(Integer facebookEnable) {
		this.facebookEnable = facebookEnable;
	}

	public Integer getPhoneTimeOfFreq() {
		return phoneTimeOfFreq;
	}

	public void setPhoneTimeOfFreq(Integer phoneTimeOfFreq) {
		this.phoneTimeOfFreq = phoneTimeOfFreq;
	}

	public Integer getDingtalkTimeOfFreq() {
		return dingtalkTimeOfFreq;
	}

	public void setDingtalkTimeOfFreq(Integer dingtalkTimeOfFreq) {
		this.dingtalkTimeOfFreq = dingtalkTimeOfFreq;
	}

	public Integer getWechatTimeOfFreq() {
		return wechatTimeOfFreq;
	}

	public void setWechatTimeOfFreq(Integer wechatTimeOfFreq) {
		this.wechatTimeOfFreq = wechatTimeOfFreq;
	}

	public Integer getTwitterTimeOfFreq() {
		return twitterTimeOfFreq;
	}

	public void setTwitterTimeOfFreq(Integer twitterTimeOfFreq) {
		this.twitterTimeOfFreq = twitterTimeOfFreq;
	}

	public Integer getFacebookTimeOfFreq() {
		return facebookTimeOfFreq;
	}

	public void setFacebookTimeOfFreq(Integer facebookTimeOfFreq) {
		this.facebookTimeOfFreq = facebookTimeOfFreq;
	}

	public Integer getPhoneNumOfFreq() {
		return phoneNumOfFreq;
	}

	public void setPhoneNumOfFreq(Integer phoneNumOfFreq) {
		this.phoneNumOfFreq = phoneNumOfFreq;
	}

	public Integer getDingtalkNumOfFreq() {
		return dingtalkNumOfFreq;
	}

	public void setDingtalkNumOfFreq(Integer dingtalkNumOfFreq) {
		this.dingtalkNumOfFreq = dingtalkNumOfFreq;
	}

	public Integer getWechatNumOfFreq() {
		return wechatNumOfFreq;
	}

	public void setWechatNumOfFreq(Integer wechatNumOfFreq) {
		this.wechatNumOfFreq = wechatNumOfFreq;
	}

	public Integer getTwitterNumOfFreq() {
		return twitterNumOfFreq;
	}

	public void setTwitterNumOfFreq(Integer twitterNumOfFreq) {
		this.twitterNumOfFreq = twitterNumOfFreq;
	}

	public Integer getFacebookNumOfFreq() {
		return facebookNumOfFreq;
	}

	public void setFacebookNumOfFreq(Integer facebookNumOfFreq) {
		this.facebookNumOfFreq = facebookNumOfFreq;
	}

	public Integer[] getGroups() {
		return groups;
	}

	public void setGroups(Integer[] groups) {
		this.groups = groups;
	}

	@Override
	public String toString() {
		return "AlarmContact{" +
				"name='" + name + '\'' +
				", email='" + email + '\'' +
				", id='" + getId() + '\'' +
				", phone='" + phone + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		AlarmContact other = (AlarmContact) obj;
		if(this.getId().equals(other.getId())){
			return true;
		}
		return false;
	}


}
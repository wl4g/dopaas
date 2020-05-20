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
package com.wl4g.devops.common.bean.iam;

import com.wl4g.devops.common.bean.BaseBean;

import java.io.Serializable;
import java.util.List;

public class User extends BaseBean implements Serializable {
	private static final long serialVersionUID = 381411777614066880L;

	private String userName;

	private String displayName;

	private String password;

	private Integer userType;

	private Integer status;

	private String email;

	private String phone;

	private String wechatOpenId;

	private String wechatUnionId;

	private String facebookId;

	private String googleId;

	private String twitterId;

	private String linkedinId;

	private String alipayId;

	private String githubId;

	private String awsId;

	public User() {
	}

	public User(String userName) {
		this.userName = userName;
	}

	// other
	private List<Integer> roleIds;

	private String roleStrs;

	private List<Integer> groupIds;

	private String groupNameStrs;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName == null ? null : userName.trim();
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName == null ? null : displayName.trim();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password == null ? null : password.trim();
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public String getWechatOpenId() {
		return wechatOpenId;
	}

	public void setWechatOpenId(String wechatOpenId) {
		this.wechatOpenId = wechatOpenId == null ? null : wechatOpenId.trim();
	}

	public String getWechatUnionId() {
		return wechatUnionId;
	}

	public void setWechatUnionId(String wechatUnionId) {
		this.wechatUnionId = wechatUnionId == null ? null : wechatUnionId.trim();
	}

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId == null ? null : facebookId.trim();
	}

	public String getGoogleId() {
		return googleId;
	}

	public void setGoogleId(String googleId) {
		this.googleId = googleId == null ? null : googleId.trim();
	}

	public String getTwitterId() {
		return twitterId;
	}

	public void setTwitterId(String twitterId) {
		this.twitterId = twitterId == null ? null : twitterId.trim();
	}

	public String getLinkedinId() {
		return linkedinId;
	}

	public void setLinkedinId(String linkedinId) {
		this.linkedinId = linkedinId == null ? null : linkedinId.trim();
	}

	public String getAlipayId() {
		return alipayId;
	}

	public void setAlipayId(String alipayId) {
		this.alipayId = alipayId == null ? null : alipayId.trim();
	}

	public String getGithubId() {
		return githubId;
	}

	public void setGithubId(String githubId) {
		this.githubId = githubId == null ? null : githubId.trim();
	}

	public String getAwsId() {
		return awsId;
	}

	public void setAwsId(String awsId) {
		this.awsId = awsId == null ? null : awsId.trim();
	}

	public List<Integer> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<Integer> roleIds) {
		this.roleIds = roleIds;
	}

	public String getRoleStrs() {
		return roleStrs;
	}

	public void setRoleStrs(String roleStrs) {
		this.roleStrs = roleStrs;
	}

	public String getGroupNameStrs() {
		return groupNameStrs;
	}

	public void setGroupNameStrs(String groupNameStrs) {
		this.groupNameStrs = groupNameStrs;
	}

	public List<Integer> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(List<Integer> groupIds) {
		this.groupIds = groupIds;
	}
}
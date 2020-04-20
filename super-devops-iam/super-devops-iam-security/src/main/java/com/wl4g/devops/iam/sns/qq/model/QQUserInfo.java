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
package com.wl4g.devops.iam.sns.qq.model;

import com.wl4g.devops.iam.sns.support.Oauth2UserProfile;
import com.wl4g.devops.tool.common.serialize.JacksonUtils;

public class QQUserInfo implements Oauth2UserProfile {
	private static final long serialVersionUID = 843944424065492261L;

	private String ret; // 返回码
	private String msg; // 如果ret<0，会有相应的错误信息提示，返回数据全部用UTF-8编码。
	private String nickname; // 用户在QQ空间的昵称。
	private String figureurl; // 大小为30×30像素的QQ空间头像URL。
	private String figureurl_1; // 大小为50×50像素的QQ空间头像URL。
	private String figureurl_2; // 大小为100×100像素的QQ空间头像URL。
	private String figureurl_qq_1; // 大小为40×40像素的QQ头像URL。
	private String figureurl_qq_2; // 大小为100×100像素的QQ头像URL。需要注意，不是所有的用户都拥有QQ的100x100的头像，但40x40像素则是一定会有。
	private String gender; // 性别。 如果获取不到则默认返回"男"

	public String getRet() {
		return ret;
	}

	public void setRet(String ret) {
		this.ret = ret;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getFigureurl() {
		return figureurl;
	}

	public void setFigureurl(String figureurl) {
		this.figureurl = figureurl;
	}

	public String getFigureurl_1() {
		return figureurl_1;
	}

	public void setFigureurl_1(String figureurl_1) {
		this.figureurl_1 = figureurl_1;
	}

	public String getFigureurl_2() {
		return figureurl_2;
	}

	public void setFigureurl_2(String figureurl_2) {
		this.figureurl_2 = figureurl_2;
	}

	public String getFigureurl_qq_1() {
		return figureurl_qq_1;
	}

	public void setFigureurl_qq_1(String figureurl_qq_1) {
		this.figureurl_qq_1 = figureurl_qq_1;
	}

	public String getFigureurl_qq_2() {
		return figureurl_qq_2;
	}

	public void setFigureurl_qq_2(String figureurl_qq_2) {
		this.figureurl_qq_2 = figureurl_qq_2;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	@Override
	public String toString() {
		return "UserInfo [" + (ret != null ? "ret=" + ret + ", " : "") + (msg != null ? "msg=" + msg + ", " : "")
				+ (nickname != null ? "nickname=" + nickname + ", " : "")
				+ (figureurl != null ? "figureurl=" + figureurl + ", " : "")
				+ (figureurl_1 != null ? "figureurl_1=" + figureurl_1 + ", " : "")
				+ (figureurl_2 != null ? "figureurl_2=" + figureurl_2 + ", " : "")
				+ (figureurl_qq_1 != null ? "figureurl_qq_1=" + figureurl_qq_1 + ", " : "")
				+ (figureurl_qq_2 != null ? "figureurl_qq_2=" + figureurl_qq_2 + ", " : "")
				+ (gender != null ? "gender=" + gender : "") + "]";
	}

	@SuppressWarnings("unchecked")
	@Override
	public QQUserInfo build(String message) {
		return JacksonUtils.parseJSON(message, QQUserInfo.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public QQUserInfo validate() {
		// TODO
		return this;
	}

}
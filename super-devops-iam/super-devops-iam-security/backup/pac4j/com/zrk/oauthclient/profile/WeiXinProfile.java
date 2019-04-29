/*
 * Copyright 2015 the original author or authors.
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
package com.zrk.oauthclient.profile;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oauth.profile.OAuth20Profile;

import com.zrk.oauthclient.definition.WeiXinAttributesDefinition;

/**
 * 微信用户信息
 * 
 * @author zrk
 * @date 2016年4月15日 下午5:44:24
 */
public class WeiXinProfile extends OAuth20Profile implements ClientProfile {

	private static final long serialVersionUID = -7486869356444327782L;

	@Override
	protected AttributesDefinition getAttributesDefinition() {
		return new WeiXinAttributesDefinition();
	}

	public String getOpenid() {
		return (String) getAttribute(WeiXinAttributesDefinition.OPEN_ID);
	}

	public String getNickname() {
		return (String) getAttribute(WeiXinAttributesDefinition.NICK_NAME);
	}

	public Integer getSex() {
		return (Integer) getAttribute(WeiXinAttributesDefinition.SEX);
	}

	public String getIcon() {
		return (String) getAttribute(WeiXinAttributesDefinition.HEADIMGURL);
	}

}
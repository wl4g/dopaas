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

import com.zrk.oauthclient.definition.SinaWeiboAttributesDefinition;

/**
 * 微博用户信息
 * 
 * @author zrk
 * @date 2016年4月15日 下午5:44:10
 */
public class SinaWeiboProfile extends OAuth20Profile implements ClientProfile {

	private static final long serialVersionUID = -7486869356444327783L;

	@Override
	protected AttributesDefinition getAttributesDefinition() {
		return new SinaWeiboAttributesDefinition();
	}

	public String getOpenid() {
		return (String) getAttribute(SinaWeiboAttributesDefinition.IDSTR);
	}

	public String getNickname() {
		return (String) getAttribute(SinaWeiboAttributesDefinition.NAME);
	}

	// 性别，m：男、f：女、n：未知
	public Integer getSex() {
		String sex = (String) getAttribute(SinaWeiboAttributesDefinition.GENDER);
		if ("m".equals(sex))
			return 1;
		if ("f".equals(sex))
			return 0;
		return null;
	}

	public String getIcon() {
		return (String) getAttribute(SinaWeiboAttributesDefinition.AVATAR_HD);
	}

}
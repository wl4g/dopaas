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

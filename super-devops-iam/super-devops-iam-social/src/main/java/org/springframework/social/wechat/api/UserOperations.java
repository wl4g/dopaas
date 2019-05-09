package org.springframework.social.wechat.api;

import org.springframework.social.wechat.WechatLangEnum;

/**
 * spring-social-wechat
 * 
 * @author <a href="mailto:larry7696@gmail.com">Larry</a>
 * @version 18.6.27
 */
public interface UserOperations {

	User getUserProfile(String openId);

	User getUserProfile(String openId, WechatLangEnum lang);

}

package org.springframework.social.wechat.autoconfigurer;

import org.springframework.boot.autoconfigure.social.SocialProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * spring-social-wechat
 * 
 * @author <a href="mailto:larry7696@gmail.com">Larry</a>
 * @version 18.10.18
 */
@ConfigurationProperties(prefix = "spring.social.wechatmp")
public class WechatMpProperties extends SocialProperties {
}

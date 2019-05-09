package org.springframework.social.qq.autoconfigure;

import org.springframework.boot.autoconfigure.social.SocialProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.social.qq")
public class QQProperties extends SocialProperties {

}

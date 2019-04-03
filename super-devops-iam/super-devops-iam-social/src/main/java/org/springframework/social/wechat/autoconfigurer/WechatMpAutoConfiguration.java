package org.springframework.social.wechat.autoconfigurer;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.social.SocialAutoConfigurerAdapter;
import org.springframework.boot.autoconfigure.social.SocialWebAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.wechat.api.WechatMp;
import org.springframework.social.wechat.connect.WechatMpConnectionFactory;

/**
 * spring-social-wechat
 * 
 * @author <a href="mailto:larry7696@gmail.com">Larry</a>
 * @version 18.10.18
 */
@Configuration
@ConditionalOnClass({ SocialConfigurerAdapter.class, WechatMpConnectionFactory.class })
@ConditionalOnProperty(prefix = "spring.social.wechatmp", name = "app-id")
@AutoConfigureBefore(SocialWebAutoConfiguration.class)
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class WechatMpAutoConfiguration {

	@Configuration
	@EnableSocial
	@EnableConfigurationProperties(WechatMpProperties.class)
	@ConditionalOnWebApplication
	protected static class WechatConfigurerAdapter extends SocialAutoConfigurerAdapter {

		private final WechatMpProperties properties;

		protected WechatConfigurerAdapter(WechatMpProperties properties) {
			this.properties = properties;
		}

		@Override
		protected ConnectionFactory<WechatMp> createConnectionFactory() {
			return new WechatMpConnectionFactory(this.properties.getAppId(), this.properties.getAppSecret());
		}

	}

}

package com.wl4g.devops.iam.captcha.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.iam.captcha.handler.GifCaptchaHandler;
import com.wl4g.devops.iam.common.cache.JedisCacheManager;
import com.wl4g.devops.iam.config.IamConfiguration;
import com.wl4g.devops.iam.config.IamProperties;
import com.wl4g.devops.iam.handler.CaptchaHandler;
import com.wl4g.devops.iam.handler.DefaultJdkRandomCaptchaHandler;

@Configuration
@ConditionalOnProperty(value = "spring.cloud.devops.iam.captcha.enabled", matchIfMissing = false)
public class KaptchaConfiguration {

	/**
	 * {@link DefaultJdkRandomCaptchaHandler}
	 * {@link IamConfiguration#captchaHandler}
	 * 
	 * @return
	 */
	@Bean
	public CaptchaHandler captchaHandler(IamProperties config, JedisCacheManager cacheManager) {
		return new GifCaptchaHandler(config, cacheManager);
	}

	/**
	 * {@link DefaultJdkRandomCaptchaHandler}
	 * {@link IamConfiguration#captchaHandler}
	 * 
	 * @return
	 */
	// @Bean
	// public CaptchaHandler kaptchaCaptchaHandler(IamProperties config,
	// JedisCacheManager cacheManager,
	// DefaultKaptcha kaptchaProducer) {
	// return new KaptchaCaptchaHandler(config, cacheManager, kaptchaProducer);
	// }

	// @Bean
	// public DefaultKaptcha kaptchaProducer(KaptchaProperties properties) {
	// DefaultKaptcha kaptcha = new DefaultKaptcha();
	// kaptcha.setConfig(new Config(properties.getProperties()));
	// return kaptcha;
	// }

	@Bean
	public KaptchaProperties kaptchaProperties() {
		return new KaptchaProperties();
	}

}

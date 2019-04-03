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
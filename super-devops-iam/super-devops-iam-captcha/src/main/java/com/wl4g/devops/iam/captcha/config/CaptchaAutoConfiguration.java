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
package com.wl4g.devops.iam.captcha.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.wl4g.devops.iam.captcha.verification.GifSecurityVerifier;
import com.wl4g.devops.iam.captcha.verification.KaptchaSecurityVerifier;
import com.wl4g.devops.iam.verification.DefaultJdkJPEGSecurityVerifier;

@Configuration
@ConditionalOnProperty(value = "spring.cloud.devops.iam.captcha.enabled", matchIfMissing = false)
public class CaptchaAutoConfiguration {

	@Bean
	public CaptchaProperties captchaProperties() {
		return new CaptchaProperties();
	}

	/**
	 * {@link DefaultJdkJPEGSecurityVerifier}
	 * {@link IamAutoConfiguration#captchaHandler}
	 * 
	 * @return
	 */
	@Bean
	public GifSecurityVerifier gifSecurityVerifier() {
		return new GifSecurityVerifier();
	}

	/**
	 * {@link DefaultJdkImgVerification} {@link IamConfiguration#verification}
	 *
	 * @return
	 */
	@Bean
	public KaptchaSecurityVerifier kaptchaSecurityVerifier(DefaultKaptcha kaptchaProducer) {
		return new KaptchaSecurityVerifier(kaptchaProducer);
	}

	@Bean
	public DefaultKaptcha kaptchaProducer(CaptchaProperties properties) {
		DefaultKaptcha kaptcha = new DefaultKaptcha();
		kaptcha.setConfig(new Config(properties.getProperties()));
		return kaptcha;
	}

}
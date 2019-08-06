/*
 * Copyright 2017 ~ 2025 the original author or authors.
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

import static com.wl4g.devops.iam.config.IamConfiguration.BEAN_GRAPH_VERIFICATION;

//import com.google.code.kaptcha.impl.DefaultKaptcha;
//import com.google.code.kaptcha.util.Config;
//import com.wl4g.devops.iam.captcha.handler.KaptchaVerification;
import com.wl4g.devops.iam.captcha.handler.GifVerification;
import com.wl4g.devops.iam.config.BasedContextConfiguration.IamContextManager;
import com.wl4g.devops.iam.handler.verification.DefaultJdkImgVerification;
import com.wl4g.devops.iam.handler.verification.GraphBasedVerification;

@Configuration
@ConditionalOnProperty(value = "spring.cloud.devops.iam.captcha.enabled", matchIfMissing = false)
public class CaptchaConfiguration {

	/**
	 * {@link DefaultJdkImgVerification} {@link IamConfiguration#captchaHandler}
	 * 
	 * @return
	 */
	@Bean(BEAN_GRAPH_VERIFICATION)
	public GraphBasedVerification graphBasedVerification(IamContextManager manager) {
		return new GifVerification(manager);
	}

	// /**
	// * {@link DefaultJdkImgVerification} {@link IamConfiguration#verification}
	// *
	// * @return
	// */
	// @Bean(BEAN_GRAPH_VERIFICATION)
	// public GraphBasedVerification graphBasedVerification(DefaultKaptcha
	// kaptchaProducer, IamContextManager manager) {
	// return new KaptchaVerification(kaptchaProducer, manager);
	// }
	//
	// @Bean
	// public DefaultKaptcha kaptchaProducer(CaptchaProperties properties) {
	// DefaultKaptcha kaptcha = new DefaultKaptcha();
	// kaptcha.setConfig(new Config(properties.getProperties()));
	// return kaptcha;
	// }

	@Bean
	public CaptchaProperties kaptchaProperties() {
		return new CaptchaProperties();
	}

}
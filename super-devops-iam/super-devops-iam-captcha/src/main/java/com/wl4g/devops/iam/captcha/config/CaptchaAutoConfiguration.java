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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.iam.captcha.jigsaw.JigsawImageManager;
import com.wl4g.devops.iam.captcha.verification.GifSecurityVerifier;
import com.wl4g.devops.iam.captcha.verification.JigsawSecurityVerifier;
import com.wl4g.devops.iam.common.cache.IamCacheManager;
import com.wl4g.devops.iam.verification.SimpleJPEGSecurityVerifier;
import com.wl4g.devops.support.concurrent.locks.JedisLockManager;

@Configuration
public class CaptchaAutoConfiguration {

	@Bean
	public CaptchaProperties captchaProperties() {
		return new CaptchaProperties();
	}

	/**
	 * {@link SimpleJPEGSecurityVerifier}
	 * {@link IamAutoConfiguration#captchaHandler}
	 * 
	 * @return
	 */
	@Bean
	public GifSecurityVerifier gifSecurityVerifier() {
		return new GifSecurityVerifier();
	}

	// --- Jigsaw ---

	@Bean
	public JigsawImageManager jigsawImageManager(CaptchaProperties config, IamCacheManager cacheManager,
			JedisLockManager lockManager) {
		return new JigsawImageManager(config, cacheManager, lockManager);
	}

	@Bean
	public JigsawSecurityVerifier jigsawSecurityVerifier() {
		return new JigsawSecurityVerifier();
	}

}
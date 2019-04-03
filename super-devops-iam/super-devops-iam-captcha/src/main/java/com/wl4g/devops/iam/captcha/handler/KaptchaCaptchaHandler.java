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
package com.wl4g.devops.iam.captcha.handler;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.wl4g.devops.iam.common.cache.JedisCacheManager;
import com.wl4g.devops.iam.config.IamProperties;
import com.wl4g.devops.iam.handler.AbstractCaptchaHandler;

/**
 * Google KAPTCHA handler.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月28日
 * @since
 */
public class KaptchaCaptchaHandler extends AbstractCaptchaHandler {

	private DefaultKaptcha kaptchaProducer;

	public KaptchaCaptchaHandler(IamProperties config, JedisCacheManager cacheManager, DefaultKaptcha kaptchaProducer) {
		super(config, cacheManager);
		Assert.notNull(kaptchaProducer, "'kaptchaProducer' must not be null");
		this.kaptchaProducer = kaptchaProducer;
	}

	@Override
	protected String createText() {
		return this.kaptchaProducer.createText();
	}

	@Override
	protected void out(HttpServletResponse response, String capText) throws IOException {
		ServletOutputStream out = response.getOutputStream();
		// Write the data out
		ImageIO.write(this.kaptchaProducer.createImage(capText), "JPEG", out);
	}

}
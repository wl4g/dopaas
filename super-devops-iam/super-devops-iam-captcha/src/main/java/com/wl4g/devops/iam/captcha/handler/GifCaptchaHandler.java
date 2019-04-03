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

import javax.servlet.http.HttpServletResponse;

import com.wl4g.devops.iam.captcha.gif.Captcha;
import com.wl4g.devops.iam.captcha.gif.GifCaptcha;
import com.wl4g.devops.iam.captcha.gif.Randoms;
import com.wl4g.devops.iam.common.cache.JedisCacheManager;
import com.wl4g.devops.iam.config.IamProperties;
import com.wl4g.devops.iam.handler.AbstractCaptchaHandler;

/**
 * GIF captcha handler.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年12月28日
 * @since
 */
public class GifCaptchaHandler extends AbstractCaptchaHandler {

	public GifCaptchaHandler(IamProperties config, JedisCacheManager cacheManager) {
		super(config, cacheManager);
	}

	@Override
	protected String createText() {
		StringBuffer alpha = new StringBuffer();
		for (int i = 0; i < 5; i++) {
			alpha.append(Randoms.alpha());
		}
		return alpha.toString();
	}

	@Override
	protected void out(HttpServletResponse response, String capText) throws IOException {
		Captcha captcha = new GifCaptcha(capText);
		captcha.out(response.getOutputStream());
	}

}
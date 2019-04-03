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

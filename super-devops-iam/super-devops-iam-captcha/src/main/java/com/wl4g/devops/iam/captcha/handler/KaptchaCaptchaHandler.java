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

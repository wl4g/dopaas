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
package com.wl4g.devops.iam.captcha.jigsaw;

import static org.apache.commons.lang3.RandomStringUtils.random;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.Assert;

import com.wl4g.devops.iam.captcha.config.CaptchaProperties;

/**
 * Jigsaw image manager.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019-09-02
 * @since
 */
public class JigsawImageManager implements ApplicationRunner, Serializable {
	private static final long serialVersionUID = -4133013721883654349L;

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * CAPTCHA configuration properties.
	 */
	final protected CaptchaProperties config;

	/**
	 * Jigsaw image cache pool.
	 */
	final protected Map<Integer, JigsawImgCode> cachePool = new ConcurrentHashMap<>();

	public JigsawImageManager(CaptchaProperties config) {
		Assert.notNull(config, "Captcha properties must not be null.");
		this.config = config;
	}

	@Override
	public void run(ApplicationArguments arg0) throws Exception {
		if (log.isInfoEnabled()) {
			log.info("Initialize jigsaw image cache pool...");
		}
		initJigsawImageCache();
	}

	/**
	 * Clear cache.
	 */
	public void clearCache() {
		this.cachePool.clear();
	}

	/**
	 * Get random borrow jigsaw image code.
	 * 
	 * @return
	 */
	public JigsawImgCode borrow() {
		Assert.state(cachePool.size() > 0, "Unable to borrow jigsaw image resource.");
		return cachePool.get(random(cachePool.size()));
	}

	/**
	 * Initialize jigsaw image buffer cache.
	 * 
	 * @throws Exception
	 */
	private void initJigsawImageCache() throws Exception {
		for (int i = 0; i < config.getJigsaw().getPoolSize(); i++) {
			ImageTailor tailor = new ImageTailor();
			this.cachePool.put(i, tailor.cutImageFile("f:\\a.png"));
		}
	}

}

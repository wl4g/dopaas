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

import static io.netty.util.internal.ThreadLocalRandom.current;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
		initJigsawImageCache();
	}

	/**
	 * Clear cache.
	 */
	public void clearCache() {
		if (log.isInfoEnabled()) {
			log.info("Clear jigsaw image pool for {} ...", cachePool.size());
		}
		this.cachePool.clear();
	}

	/**
	 * Get random borrow jigsaw image code.
	 * 
	 * @return
	 */
	public JigsawImgCode borrow() {
		Assert.state(!cachePool.isEmpty(), "Unable to borrow jigsaw image resource.");
		return cachePool.get(current().nextInt(cachePool.size()));
	}

	/**
	 * Initialize jigsaw image buffer cache.
	 * 
	 * @throws Exception
	 */
	private void initJigsawImageCache() throws IOException {
		if (log.isInfoEnabled()) {
			log.info("Initializing jigsaw image buffer pool...");
		}

		File srcDir = new File(config.getJigsaw().getSourceDir());
		Assert.state((srcDir.canRead() && srcDir.exists()),
				String.format("Failed to initialize jigsaw images, please check the path: %s is correct and has read permission",
						srcDir.getAbsolutePath()));
		// Read files.
		File[] files = srcDir.listFiles();
		Assert.state((files != null && files.length > 0),
				String.format("Failed to initialize jigsaw images, path: %s material is empty", srcDir.getAbsolutePath()));

		// Statistic use material.
		Set<Integer> indexs = new HashSet<>();
		// Initialize jigsaw images.
		ImageTailor tailor = new ImageTailor();
		for (int i = 0; i < config.getJigsaw().getPoolSize(); i++) {
			int index = i;
			if (index >= files.length) { // Inadequate material, random reuse.
				index = current().nextInt(files.length);
			}
			indexs.add(index);
			String path = files[index].getAbsolutePath();
			if (log.isDebugEnabled()) {
				log.debug("Generate jigsaw image from material: {}", path);
			}
			this.cachePool.put(i, tailor.getJigsawImageFile(path));
		}

		if (log.isInfoEnabled()) {
			log.info("Initialized jigsaw images buffer total: {}, expend material: {}", config.getJigsaw().getPoolSize(),
					indexs.size());
		}

	}

}

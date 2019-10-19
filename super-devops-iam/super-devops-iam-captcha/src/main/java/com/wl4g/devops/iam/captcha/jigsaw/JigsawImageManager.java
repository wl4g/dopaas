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

import com.wl4g.devops.iam.captcha.config.CaptchaProperties;
import com.wl4g.devops.iam.captcha.jigsaw.model.JigsawImgCode;
import com.wl4g.devops.support.cache.JedisService;
import com.wl4g.devops.support.lock.SimpleRedisLockManager;

import redis.clients.jedis.JedisCluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.CACHE_VERIFY_JIGSAW_IMG;
import static com.wl4g.devops.common.utils.codec.Encodes.toBytes;
import static com.wl4g.devops.common.utils.serialize.ProtostuffUtils.deserialize;
import static com.wl4g.devops.common.utils.serialize.ProtostuffUtils.serialize;
import static io.netty.util.internal.ThreadLocalRandom.current;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.commons.lang3.exception.ExceptionUtils.wrapAndThrow;

/**
 * JIGSAW image manager.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019-09-02
 * @since
 */
public class JigsawImageManager implements ApplicationRunner, Serializable {
	private static final long serialVersionUID = -4133013721883654349L;

	/**
	 * Default JIGSAW source image path.
	 */
	final public static String DEFAULT_JIGSAW_SOURCE_CLASSPATH = "classpath:static/jigsaw/*.*";

	/**
	 * Default JIGSAW initialize image timeoutMs
	 */
	final public static long DEFAULT_JIGSAW_INIT_TIMEOUTMS = 60_000L;

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * CAPTCHA configuration properties.
	 */
	final protected CaptchaProperties config;

	/**
	 * Simple lock manager.
	 */
	final protected Lock lock;

	/**
	 * REDIS service.
	 */
	@Autowired
	protected JedisService jedisService;

	public JigsawImageManager(CaptchaProperties config, SimpleRedisLockManager lockManager) {
		Assert.notNull(config, "Captcha properties must not be null.");
		Assert.notNull(lockManager, "Captcha properties must not be null.");
		this.config = config;
		this.lock = lockManager.getLock(getClass().getSimpleName(), DEFAULT_JIGSAW_INIT_TIMEOUTMS, TimeUnit.MILLISECONDS);
	}

	@Override
	public void run(ApplicationArguments arg0) throws Exception {
		initializeJigsawImagePool();
	}

	/**
	 * Get random borrow JIGSAW image code.
	 * 
	 * @return
	 */
	public JigsawImgCode borrow() {
		return borrow(-1);
	}

	/**
	 * Get random borrow JIGSAW image code.
	 * 
	 * @param index
	 * @return
	 */
	public JigsawImgCode borrow(int index) {
		if (index < 0 || index >= config.getJigsaw().getPoolImgSize()) {
			int _index = current().nextInt(config.getJigsaw().getPoolImgSize());
			if (log.isDebugEnabled()) {
				log.debug("Borrow jigsaw index '{}' of out bound, used random index '{}'", index, _index);
			}
			index = _index;
		}

		// Load JIGSAW image by index.
		JedisCluster jdsCluster = jedisService.getJedisCluster();
		byte[] data = jdsCluster.hget(CACHE_VERIFY_JIGSAW_IMG, toBytes(String.valueOf(index)));
		if (Objects.isNull(data)) { // Expired?
			try {
				if (lock.tryLock(DEFAULT_JIGSAW_INIT_TIMEOUTMS / 2, TimeUnit.MILLISECONDS)) {
					initializeJigsawImagePool();
				}
			} catch (Exception e) {
				wrapAndThrow(e);
			} finally {
				lock.unlock();
			}
			// Retry get.
			data = jdsCluster.hget(CACHE_VERIFY_JIGSAW_IMG, toBytes(String.valueOf(index)));
		}
		JigsawImgCode code = deserialize(data, JigsawImgCode.class);
		Assert.notNull(code, "Unable to borrow jigsaw image resource.");

		// UnCompression primary block image.
		return code/* .uncompress() */;
	}

	/**
	 * Clear cache.
	 */
	public void clearCache() {
		if (log.isInfoEnabled()) {
			log.info("Clear jigsaw image pool ...");
		}
		jedisService.getJedisCluster().del(CACHE_VERIFY_JIGSAW_IMG);
	}

	/**
	 * Initializing JIGSAW image buffer cache.
	 * 
	 * @throws Exception
	 */
	private synchronized void initializeJigsawImagePool() throws IOException {
		if (log.isInfoEnabled()) {
			log.info("Initializing jigsaw image buffer pool...");
		}

		if (isBlank(config.getJigsaw().getSourceDir())) {
			PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
			Resource[] resources = resolver.getResources(DEFAULT_JIGSAW_SOURCE_CLASSPATH);
			storageJigsawImageCache(resources);
		} else {
			File srcDir = new File(config.getJigsaw().getSourceDir());
			Assert.state((srcDir.canRead() && srcDir.exists()),
					String.format(
							"Failed to initialize jigsaw images, please check the path: %s is correct and has read permission",
							srcDir.getAbsolutePath()));
			// Read files.
			File[] files = srcDir.listFiles(f -> !startsWith(f.getName(), "."));
			Assert.state((files != null && files.length > 0),
					String.format("Failed to initialize jigsaw images, path: %s material is empty", srcDir.getAbsolutePath()));
			storageJigsawImageCache(files);
		}

	}

	/**
	 * Storage put buffer image to cache.
	 * 
	 * @param sources
	 * @throws IOException
	 */
	private void storageJigsawImageCache(Object[] sources) throws IOException {
		// Statistic use material.
		Set<Integer> indexs = new HashSet<>();

		// Initialize JIGSAW images.
		ImageTailor tailor = new ImageTailor();
		for (int i = 0; i < config.getJigsaw().getPoolImgSize(); i++) {
			int index = i;
			if (index >= sources.length) { // Inadequate material, random reuse.
				index = current().nextInt(sources.length);
			}
			indexs.add(index); // For statistic

			// Generate image.
			Object source = sources[index];
			if (log.isDebugEnabled()) {
				log.debug("Generate jigsaw image from material: {}", source);
			}

			if (source instanceof File) {
				String path = ((File) sources[index]).getAbsolutePath();
				putJigsawImage(tailor.getJigsawImageFile(path), i);
			} else if (source instanceof Resource) {
				Resource resource = (Resource) source;
				putJigsawImage(tailor.getJigsawImageInputStream(resource.getInputStream()), i);
			} else {
				throw new IllegalStateException(String.format("Unsupported jigsaw image source: %s", source));
			}
		}

		if (log.isInfoEnabled()) {
			log.info("Initialized jigsaw images buffer total: {}, expend material: {}", config.getJigsaw().getPoolImgSize(),
					indexs.size());
		}
	}

	/**
	 * Put image to cache.
	 * 
	 * @param code
	 * @param index
	 */
	private void putJigsawImage(JigsawImgCode code, int index) {
		// Compression primary block image.
		byte[] data = serialize(code/* .compress() */);
		// Storage to cache.
		jedisService.getJedisCluster().hset(CACHE_VERIFY_JIGSAW_IMG, toBytes(String.valueOf(index)), data);
		jedisService.getJedisCluster().expire(CACHE_VERIFY_JIGSAW_IMG, config.getJigsaw().getPoolImgExpireSec());
		if (log.isDebugEnabled()) {
			log.debug("Put jigsaw image to cache, index {}, jigsawImage(x:{}, y:{})", index, code.getX(), code.getY());
		}
	}

}
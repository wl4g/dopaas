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
package com.wl4g.devops.scm.publish;

import java.util.Collection;
import java.util.List;

import com.wl4g.devops.scm.config.ScmProperties;
import com.wl4g.devops.support.cache.JedisService;
import com.wl4g.devops.support.cache.ScanCursor;

/**
 * SCM config soruce server publisher implements
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月27日
 * @since
 */
public class DefaultRedisConfigSourcePublisher extends AbstractConfigSourcePublisher {

	final public static String KEY_PUB_GROUP_PREFIX = "scm_pub_";

	final private ThreadLocal<ScanCursor<PublishConfigWrapper>> cursorCache = new InheritableThreadLocal<>();

	final private JedisService jedisService;

	public DefaultRedisConfigSourcePublisher(ScmProperties config, JedisService jedisService) {
		super(config);
		this.jedisService = jedisService;
	}

	@Override
	protected Collection<PublishConfigWrapper> pollNextPublishedConfig() {

		// Create published config scanner
		ScanCursor<PublishConfigWrapper> cursor = cursorCache.get();
		if (cursor == null) {
			String pattern = KEY_PUB_GROUP_PREFIX + "*";
			cursorCache.set((cursor = jedisService.scan(pattern, 1)));
		}

		// Extract published config
		List<PublishConfigWrapper> list = null;
		if (cursor.hasNext()) {
			list = cursor.readItem();
		} else {
			cursorCache.remove();
		}

		if (log.isDebugEnabled()) {
			log.debug("Scan published config for size: {}, {}", (list != null ? list.size() : 0), list);
		}
		return list;
	}

	@Override
	protected void putPublishConfig(PublishConfigWrapper wrap) {
		if (log.isDebugEnabled()) {
			log.debug("Put published config for {}", wrap);
		}

		String key = KEY_PUB_GROUP_PREFIX + wrap.asIdentify();
		jedisService.setObjectT(key, wrap, 0);
	}

}

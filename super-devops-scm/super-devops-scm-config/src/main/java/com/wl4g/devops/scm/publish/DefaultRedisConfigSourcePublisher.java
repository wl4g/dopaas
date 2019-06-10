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

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.wl4g.devops.scm.config.ScmProperties;
import com.wl4g.devops.support.cache.JedisService;

/**
 * SCM configuration source server publisher implements
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月27日
 * @since
 */
public class DefaultRedisConfigSourcePublisher extends AbstractConfigSourcePublisher {

	/** SCM published group. */
	final public static String CACHE_PUB_GROUPS = "scm_pub_groups";

	/** SCM published CONFIG prefix. */
	final public static String KEY_PUB_PREFIX = "scm_pub_config_";

	final private JedisService jedisService;

	public DefaultRedisConfigSourcePublisher(ScmProperties config, JedisService jedisService) {
		super(config);
		this.jedisService = jedisService;
	}

	@Override
	protected Collection<PublishConfigWrapper> pollNextPublishedConfig() {
		// Extract published
		List<PublishConfigWrapper> list = new ArrayList<>(4);

		Set<Object> groups = jedisService.getObjectSet(CACHE_PUB_GROUPS);
		if (!isEmpty(groups)) {
			for (Object group : groups) {
				String key = KEY_PUB_PREFIX + group;
				PublishConfigWrapper wrap = jedisService.getObjectT(key, PublishConfigWrapper.class);
				if (wrap != null) {
					list.add(wrap);
				}
				jedisService.del(key);
			}
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

		// Save group name.
		jedisService.setSetObjectAdd(CACHE_PUB_GROUPS, wrap.getGroup());

		// Save group published.
		String key = KEY_PUB_PREFIX + wrap.getGroup();
		jedisService.setObjectT(key, wrap, 0);
	}

}

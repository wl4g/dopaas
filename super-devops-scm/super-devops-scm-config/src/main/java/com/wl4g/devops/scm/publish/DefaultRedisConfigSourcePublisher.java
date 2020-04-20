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
package com.wl4g.devops.scm.publish;

import com.wl4g.devops.scm.config.ScmProperties;
import com.wl4g.devops.support.redis.JedisService;
import org.apache.commons.codec.binary.Hex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.devops.common.constants.SCMDevOpsConstants.CACHE_PUB_GROUPS;
import static com.wl4g.devops.common.constants.SCMDevOpsConstants.KEY_PUB_PREFIX;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * SCM configuration source server publisher implements
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月27日
 * @since
 */
public class DefaultRedisConfigSourcePublisher extends AbstractConfigSourcePublisher {

	final private JedisService jedisService;

	public DefaultRedisConfigSourcePublisher(ScmProperties config, JedisService jedisService) {
		super(config);
		this.jedisService = jedisService;
	}

	@Override
	protected Collection<PublishConfigWrapper> pollNextPublishedConfig() {
		List<PublishConfigWrapper> list = new ArrayList<>(4);

		// Extract published config.
		Set<Object> groups = jedisService.getObjectSet(CACHE_PUB_GROUPS);
		if (!isEmpty(groups)) {
			for (Object group : groups) {
				String key = getGroupKey((String) group);
				PublishConfigWrapper wrap = jedisService.getObjectT(key, PublishConfigWrapper.class);
				if (wrap != null) {
					list.add(wrap);
					PublishConfigWrapper wrap2 = jedisService.getObjectT(key, PublishConfigWrapper.class);
					jedisService.del(key);
				}
			}
		}

		log.debug("Extract published config for - ({}), {}", list.size(), list);
		return list;
	}

	@Override
	protected void publishConfig(PublishConfigWrapper wrap) {
		log.debug("Put published config for - {}", wrap);

		// Storage group name
		jedisService.setSetObjectAdd(CACHE_PUB_GROUPS, wrap.getCluster());

		// Storage group published
		jedisService.setObjectT(getGroupKey(wrap.getCluster()), wrap, 0);
	}

	private String getGroupKey(String group) {
		// jedis unsupport '-'
		group = Hex.encodeHexString(group.getBytes(UTF_8));
		return KEY_PUB_PREFIX + group;
	}

}
/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.umc.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wl4g.component.common.serialize.JacksonUtils;
import com.wl4g.component.support.redis.jedis.JedisService;
import com.wl4g.dopaas.common.constant.UmcConstants;
import com.wl4g.dopaas.umc.model.StatusMessage;

/**
 * Dashboard Service
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年6月7日
 * @since
 */
@Service
public class DashboardHandler {
	final protected Logger logger = LoggerFactory.getLogger(getClass());

private @Autowired  JedisService jedisService;

	public StatusMessage findStatusInfo(String msgId) {
		String msg = this.jedisService.get(UmcConstants.INFO_PREFIX + msgId);
		StatusMessage info = JacksonUtils.parseJSON(msg, StatusMessage.class);
		if (info == null) {
			throw new IllegalArgumentException("Getting the `" + msgId + "` corresponding state message is null.");
		}
		return info;
	}

}
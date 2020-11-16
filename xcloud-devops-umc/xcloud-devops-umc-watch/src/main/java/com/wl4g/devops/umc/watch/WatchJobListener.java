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
package com.wl4g.devops.umc.watch;

import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dangdang.ddframe.job.executor.ShardingContexts;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;

/**
 * Indicators watch jobs listener.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月4日
 * @since
 */
public class WatchJobListener implements ElasticJobListener {
	final private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void beforeJobExecuted(ShardingContexts ctxs) {
		if (log.isInfoEnabled()) {
			log.info("Before executed for - {}", toJSONString(ctxs));
		}
	}

	@Override
	public void afterJobExecuted(ShardingContexts ctxs) {
		if (log.isInfoEnabled()) {
			log.info("After executed for - {}", toJSONString(ctxs));
		}
	}

}
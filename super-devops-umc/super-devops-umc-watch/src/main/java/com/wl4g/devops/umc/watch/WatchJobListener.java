package com.wl4g.devops.umc.watch;

import static com.wl4g.devops.common.utils.serialize.JacksonUtils.toJSONString;

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
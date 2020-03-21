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
package com.wl4g.devops.umc.alarm.alerting;

import com.wl4g.devops.common.bean.umc.model.MetricValue;
import com.wl4g.devops.support.concurrent.locks.JedisLockManager;
import com.wl4g.devops.support.redis.JedisService;
import com.wl4g.devops.support.task.GenericTaskRunner;
import com.wl4g.devops.support.task.RunnerProperties;
import com.wl4g.devops.tool.common.log.SmartLogger;
import com.wl4g.devops.umc.alarm.metric.MetricAggregateWrapper;
import com.wl4g.devops.umc.config.AlarmProperties;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static com.wl4g.devops.common.constants.UMCDevOpsConstants.KEY_CACHE_ALARM_METRIC_QUEUE;
import static com.wl4g.devops.tool.common.collection.Collections2.ensureList;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.lang.Math.abs;
import static java.util.Collections.emptyList;

/**
 * Abstract collection metric valve alerter.
 *
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 */
public abstract class AbstractIndicatorsValveAlerter extends GenericTaskRunner<RunnerProperties>
		implements IndicatorsValveAlerter {

	final protected SmartLogger log = getLogger(getClass());

	/** REDIS service */
	final protected JedisService jedisService;

	/**
	 * REDIS lock manager.
	 */
	final protected JedisLockManager lockManager;

	public AbstractIndicatorsValveAlerter(JedisService jedisService, JedisLockManager lockManager, AlarmProperties config) {
		super(config);
		Assert.notNull(jedisService, "JedisService is null, please check config.");
		Assert.notNull(lockManager, "LockManager is null, please check config.");
		this.jedisService = jedisService;
		this.lockManager = lockManager;
	}

	@Override
	public void alarm(MetricAggregateWrapper wrap) {
		// doHandleAlarm not running ???
		getWorker().execute(() -> doHandleAlarm(wrap));
	}

	/**
	 * Do handling alarm.
	 * 
	 * @param agwrap
	 */
	protected abstract void doHandleAlarm(MetricAggregateWrapper agwrap);

	// --- Metric time queue. ---

	/**
	 * Offer metric values in time windows.
	 * 
	 * @param cacheKey
	 *            cacheKey address
	 * @param value
	 *            metric value
	 * @param gatherTime
	 *            gather time-stamp.
	 * @param now
	 *            current date time-stamp.
	 * @param ttl
	 *            time-to-live
	 * @return
	 */
	protected List<MetricValue> offerTimeWindowQueue(String cacheKey, Double value, long gatherTime, long now, long ttl) {
		List<MetricValue> metricVals = emptyList();

		// To solve the concurrency problem of metric window queue in
		// distributed environment.
		Lock lock = lockManager.getLock(getTimeWindowQueueCacheKey(cacheKey));
		try {
			if (lock.tryLock(6L, TimeUnit.SECONDS)) {
				metricVals = ensureList(doPeekMetricValueQueue(cacheKey));

				// Check & clean expired metrics.
				Iterator<MetricValue> it = metricVals.iterator();
				while (it.hasNext()) {
					long gatherTime1 = it.next().getGatherTime();
					// Remove expire data and repeat data
					if (abs(now - gatherTime1) >= ttl || gatherTime1 == gatherTime) {
						it.remove();
					}
				}
				metricVals.add(new MetricValue(gatherTime, value));

				// Offer to queue.
				doOfferMetricValueQueue(cacheKey, ttl, metricVals);
			}
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		} finally {
			lock.unlock();
		}

		return metricVals;
	}

	/**
	 * GET metric values queue by collect address.
	 * 
	 * @param cacheKey
	 * @return
	 */
	protected List<MetricValue> doPeekMetricValueQueue(String cacheKey) {
		String timeWindowKey = getTimeWindowQueueCacheKey(cacheKey);
		return jedisService.getObjectList(timeWindowKey, MetricValue.class);
	}

	/**
	 * Storage metric values to cache.
	 * 
	 * @param cacheKey
	 * @param ttl
	 * @param metricVals
	 */
	protected List<MetricValue> doOfferMetricValueQueue(String cacheKey, long ttl, List<MetricValue> metricVals) {
		String timeWindowKey = getTimeWindowQueueCacheKey(cacheKey);
		jedisService.del(timeWindowKey);
		jedisService.setObjectList(timeWindowKey, metricVals, (int) ttl / 1000);
		return metricVals;
	}

	// --- Cache key. ---

	protected String getTimeWindowQueueCacheKey(String cacheKey) {
		Assert.hasText(cacheKey, "cacheKey must not be empty");
		return KEY_CACHE_ALARM_METRIC_QUEUE + cacheKey;
	}

}
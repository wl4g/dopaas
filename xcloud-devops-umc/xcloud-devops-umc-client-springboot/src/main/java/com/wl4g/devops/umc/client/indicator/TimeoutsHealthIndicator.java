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
package com.wl4g.devops.umc.client.indicator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wl4g.devops.umc.client.metrics.TimerMetricsAdvice.TimerMetricsProperties;
import com.wl4g.devops.umc.client.utils.HealthUtil;

/**
 * Analysis and statistical call time dimension related health messages
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年6月1日
 * @since
 */
public class TimeoutsHealthIndicator extends AbstractHealthIndicator {
	final private static Logger logger = LoggerFactory.getLogger(TimeoutsHealthIndicator.class);
	final private static Comparator<TimesWrapper> comparator = new Comparator<TimesWrapper>() {
		@Override
		public int compare(TimesWrapper o1, TimesWrapper o2) {
			return (int) (o1.getMax() - o2.getMax());
		}
	};
	final private ObjectMapper objectMapper = new ObjectMapper();

	private Map<String, Deque<Long>> records = new ConcurrentHashMap<>();
	private TimerMetricsProperties conf;

	public TimeoutsHealthIndicator(TimerMetricsProperties conf) {
		this.conf = conf;
	}

	@Override
	protected void doHealthCheck(Builder builder) throws Exception {
		try {
			// Get the statistical parameter (min/max/avg/latest) etc.
			TimesWrapper wrap = this.getLargestMessage();
			if (logger.isDebugEnabled()) {
				logger.debug("TimeoutsHealth message={}", objectMapper.writeValueAsString(wrap));
			}
			if (wrap == null) {
				HealthUtil.up(builder, "Healthy");
				return;
			} else if (wrap.getMax() < conf.getTimeoutsThreshold()) {
				HealthUtil.up(builder, "Healthy");
			} else {
				HealthUtil.down(builder, "Method " + wrap.getMetricsName() + " executes " + wrap.getLatest()
						+ "ms with a response exceeding the threshold value of `" + conf.getTimeoutsThreshold() + "`ms.");
				// When the timeout exception is detected, the exception record
				// is cleared.
				this.postPropertiesReset(wrap);
			}
			builder.withDetail("Method", wrap.getMetricsName()).withDetail("Least", wrap.getMin())
					.withDetail("Largest", wrap.getMax()).withDetail("Avg", wrap.getAvg()).withDetail("Latest", wrap.getLatest())
					.withDetail("Samples", wrap.getSamples()).withDetail("Threshold", conf.getTimeoutsThreshold() + "ms");

		} catch (Exception e) {
			builder.down(e);
			logger.error("Analysis timeouts message failed.", e);
		}

	}

	public void addTimes(String metricName, long time) {
		if (logger.isDebugEnabled()) {
			logger.debug("MetricName={}, time={}", metricName, time);
		}
		int latestCount = conf.getSamples();
		Deque<Long> deque = this.records.get(metricName);
		if (deque == null) {
			if (logger.isInfoEnabled()) {
				logger.info("Initial timeoutsHealthIndicator, metricName={}, capacity: {}", metricName, latestCount);
			}
			deque = new ConcurrentLinkedDeque<>();
		}
		// Overflow check.
		if (deque.size() >= (latestCount - 1)) {
			deque.poll(); // Remove first.
		}
		deque.offer(time);
		this.records.put(metricName, deque);
	}

	/**
	 * Gets the most statistics largest time out messages (including average
	 * time, maximum duration, and longest time) of `latestMeasureCount` call
	 * records.
	 * 
	 * @return
	 */
	private TimesWrapper getLargestMessage() {
		List<TimesWrapper> tmp = new ArrayList<>();
		// Calculate the maximum value of each execution record queue.
		for (Entry<String, Deque<Long>> ent : this.records.entrySet()) {
			Deque<Long> deque = ent.getValue();
			if (!deque.isEmpty()) {
				long queueMax = deque.stream().reduce(Long::max).get();
				tmp.add(new TimesWrapper(ent.getKey(), queueMax));
			}
		}
		if (tmp.isEmpty())
			return null;

		// Gets the maximum value in the list of the maximum values in all
		// queues.
		TimesWrapper maxOfAll = Collections.max(tmp, comparator);
		Deque<Long> deque = this.records.get(maxOfAll.getMetricsName());

		maxOfAll.setSamples(deque.size());
		maxOfAll.setMin(deque.stream().reduce(Long::min).get());
		maxOfAll.setLatest(deque.peekLast());
		// Average length of time.
		long totalTime = deque.stream().collect(Collectors.summingLong(Long::longValue));
		long count = deque.stream().count();
		maxOfAll.setAvg(BigDecimal.valueOf(totalTime / count).setScale(2, RoundingMode.HALF_EVEN).longValue());

		// Remove current-largest metrics(timeouts record).
		deque.remove(maxOfAll.getMax());
		// Deque.clear();
		return maxOfAll;
	}

	/**
	 * Post process.
	 * 
	 * @param wrap
	 */
	private void postPropertiesReset(TimesWrapper wrap) {
		Deque<Long> deque = this.records.get(wrap.getMetricsName());
		if (deque != null && !deque.isEmpty()) {
			deque.remove(wrap.getMax());
		}
	}

	/**
	 * Timeout health indicator configuration bootstrap
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2018年6月3日
	 * @since
	 */
	@Configuration
	@ConditionalOnBean(TimerMetricsProperties.class)
	@AutoConfigureBefore({ TimerMetricsProperties.class })
	public static class TimeoutsHealthIndicatorConfiguration {

		@Bean
		public HealthIndicator timeoutsHealthIndicator(HealthAggregator healthAggregator, TimerMetricsProperties conf) {
			if (conf.getSamples() == 0)
				throw new IllegalArgumentException("Latest measure count is 0.");
			if (logger.isInfoEnabled())
				logger.info("Initial timeoutsHealthIndicator. {}", conf);

			TimeoutsHealthIndicator healthIndicator = new TimeoutsHealthIndicator(conf);
			Map<String, Health> healths = new LinkedHashMap<String, Health>();
			healths.put(TimeoutsHealthIndicator.class.getSimpleName(), healthIndicator.health());
			return healthIndicator;
		}

	}

	/**
	 * Timeouts message wrapper.
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2018年6月3日
	 * @since
	 */
	public static class TimesWrapper {
		private String metricsName;
		private long max;
		private long min;
		private long avg;
		private int samples;
		private long latest;

		public TimesWrapper() {
			super();
		}

		public TimesWrapper(int samples, String metricsName, long max, long min, long avg, long latest) {
			super();
			this.samples = samples;
			this.metricsName = metricsName;
			this.max = max;
			this.min = min;
			this.avg = avg;
			this.latest = latest;
		}

		public TimesWrapper(String metricsName, long max) {
			super();
			this.metricsName = metricsName;
			this.max = max;
		}

		public String getMetricsName() {
			return metricsName;
		}

		public void setMetricsName(String metricsName) {
			this.metricsName = metricsName;
		}

		public int getSamples() {
			return samples;
		}

		public void setSamples(int samples) {
			this.samples = samples;
		}

		public long getMax() {
			return max;
		}

		public void setMax(long max) {
			this.max = max;
		}

		public long getMin() {
			return min;
		}

		public void setMin(long min) {
			this.min = min;
		}

		public long getAvg() {
			return avg;
		}

		public void setAvg(long avg) {
			this.avg = avg;
		}

		public long getLatest() {
			return latest;
		}

		public void setLatest(long latest) {
			this.latest = latest;
		}

	}
}
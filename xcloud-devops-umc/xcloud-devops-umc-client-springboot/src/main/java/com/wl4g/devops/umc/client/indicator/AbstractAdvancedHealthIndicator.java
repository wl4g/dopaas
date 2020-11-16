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

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wl4g.devops.umc.client.store.DefaultMemoryEventStore;
import com.wl4g.devops.umc.client.store.EventStore;
import com.wl4g.devops.umc.client.utils.HealthUtil;

/**
 * Custom operation system disk space performance indicator.<br/>
 * Support multi partition monitoring.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年6月1日
 * @since
 */
public abstract class AbstractAdvancedHealthIndicator extends AbstractHealthIndicator
		implements ApplicationRunner, Comparator<Long> {
	final private Logger logger = LoggerFactory.getLogger(getClass());

	final private AtomicBoolean initialized = new AtomicBoolean(false);
	final protected ObjectMapper mapper = new ObjectMapper();
	final protected Map<String, EventStore<Partition>> eventStores = new ConcurrentHashMap<>();

	@Autowired
	private CompositeHealthTaskProcessor processor;
	private AdvancedHealthProperties<? extends Partition> conf;

	public AbstractAdvancedHealthIndicator(AdvancedHealthProperties<? extends Partition> conf) {
		this.conf = conf;
		if (logger.isInfoEnabled()) {
			logger.info("Init health properties: {}", conf);
		}
	}

	@Override
	protected void doHealthCheck(Builder builder) throws Exception {
		int index = 0;
		StringBuffer desc = new StringBuffer();
		for (String name : this.eventStores.keySet()) {
			try {
				EventStore<Partition> store = this.eventStores.get(name);
				Partition latestPart = store.latest();
				if (logger.isDebugEnabled())
					logger.debug("Health performance：{}", mapper.writeValueAsString(latestPart));

				if (latestPart == null) {
					builder.up();
					return;
				}

				// Get partition configuration by current partition.
				Partition confPart = this.conf.getPartitions().get(name);
				if (confPart == null)
					throw new Error("It should not come here. " + name);

				// Check threshold.
				long threshold = confPart.getValue();
				long curVal = latestPart.getValue();
				if (this.compare(curVal, threshold) < 0) { // Trigger event.
					if (desc.length() > 0) {
						desc.append("<br/>");
					}
					desc.append(name).append("：").append(formatValue(curVal)).append(" exceed the threshold：")
							.append(formatValue(confPart.getValue()));
				}
				// Meaningful field name prefix.
				String p = this.compareFieldName();
				builder.withDetail("AcqPosition_" + index, name).withDetail("AcqSamples_" + index, latestPart.getSamples())
						.withDetail("AcqTime_" + index, latestPart.getFormatTimestamp())
						.withDetail(p + "Avg_" + index, formatValue(store.average()))
						.withDetail(p + "Lgt_" + index, formatValue(store.largest().getValue()))
						.withDetail(p + "Let_" + index, formatValue(store.least().getValue()))
						.withDetail(p + "Lat_" + index, formatValue(store.latest().getValue()))
						.withDetail(p + "Threshold_" + index, formatValue(threshold));
				// All the checks are normal.
				if (desc.length() > 0) {
					HealthUtil.down(builder, desc.toString());
					desc.setLength(0); // Reset.
				} else {
					builder.up();
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Acquisition unhealthy description：{}", desc);
				}

			} catch (Exception e) {
				builder.down(e);
				logger.error("Advanced health check failed.", e);
			} finally {
				// Increase by 1
				++index;
			}
		}

	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (this.initialized.compareAndSet(false, true)) {
			this.registerToEventStores();
			this.submit();
		}
	}

	/**
	 * Get the latest monitoring information. (CPU/Memory/Disk etc)
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected abstract Partition latestPerfInfo(String name) throws Exception;

	/**
	 * Collection value formatting
	 * 
	 * @param value
	 * @return
	 */
	protected abstract String formatValue(long value);

	/**
	 * Name of comparison field for display
	 * 
	 * @return
	 */
	protected abstract String compareFieldName();

	/**
	 * Registration of multiple monitoring partitions corresponding to event
	 * memory
	 */
	private void registerToEventStores() {
		this.conf.getPartitions().forEach((name, confPart) -> {
			if (!this.eventStores.containsKey(name)) {
				this.eventStores.put(name, new DefaultMemoryEventStore(confPart.getSamples(), confPart.getRetainTime()));
			}
		});
	}

	/**
	 * Submit task all.
	 */
	private void submit() {
		this.processor.submit(() -> {
			this.conf.getPartitions().forEach((name, confPart) -> {
				try {
					// Get latest performance information.
					Partition part = this.latestPerfInfo(name);
					if (logger.isDebugEnabled()) {
						logger.debug("Performance info: part-name={}, {}", name, mapper.writeValueAsString(part));
					}
					// Save to store queue.
					this.eventStores.get(name).save(part);

				} catch (Exception e) {
					logger.error("Get performance failed.", e);
				}
			});
		});
	}

	/**
	 * Abstract health indicator attribute configuration
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0
	 * @date 2018年6月7日
	 * @since
	 */
	public abstract static class AdvancedHealthProperties<T extends Partition> {

		public abstract Map<String, T> getPartitions();

		public abstract void setPartitions(Map<String, T> partitions);

	}

	/**
	 * Base abstract monitoring partition configuration, (example: multi core
	 * CPU/ monitoring separately)
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0
	 * @date 2018年6月7日
	 * @since
	 */
	public abstract static class Partition implements Comparable<Partition> {
		private int samples = DefaultMemoryEventStore.DEFAULT_CAPACITY;
		private long retainTime = DefaultMemoryEventStore.DEFAULT_RETAIN;
		private long timestamp;

		public abstract long getValue();

		public abstract void setValue(long value);

		public int getSamples() {
			return samples;
		}

		public void setSamples(int samples) {
			this.samples = samples;
		}

		public long getRetainTime() {
			return retainTime;
		}

		public void setRetainTime(long retainTime) {
			this.retainTime = retainTime;
		}

		public void setTimestamp(long timestamp) {
			this.timestamp = timestamp;
		}

		public long getTimestamp() {
			return this.timestamp;
		}

		public String getFormatTimestamp() {
			return new SimpleDateFormat("yy-MM-dd HH:mm:ss").format(this.getTimestamp());
		}

		@Override
		public int compareTo(Partition o) {
			return (int) (this.getValue() - o.getValue());
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = (int) (prime * result + getValue());
			result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Partition other = (Partition) obj;
			if (getValue() != other.getValue())
				return false;
			if (timestamp != other.getValue())
				return false;
			return true;
		}

	}

}
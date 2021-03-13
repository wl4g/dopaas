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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.component.common.io.FileSizeUtils;
import com.wl4g.devops.common.constant.UMCConstants;
import com.wl4g.devops.umc.client.utils.PlatformOSUtil;
import com.wl4g.devops.umc.client.utils.PlatformOSUtil.MemInfo;

/**
 * Custom operation system disk space performance indicator.<br/>
 * Support multi partition monitoring.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年6月1日
 * @since
 */
public class AdvancedMemoryHealthIndicator extends AbstractAdvancedHealthIndicator {
	final private static Logger logger = LoggerFactory.getLogger(AdvancedMemoryHealthIndicator.class);
	final public static String BEAN_NAME = "advancedMemoryHealthIndicator";

	public AdvancedMemoryHealthIndicator(AdvancedHealthProperties<? extends Partition> conf) {
		super(conf);
	}

	@Override
	protected Partition latestPerfInfo(String name) throws Exception {
		// Memory usage info, Unit is all kB
		MemInfo memInfo = PlatformOSUtil.memInfo(); // Unit is KB
		MemoryPartition part = new MemoryPartition();
		part.setMemTotal(memInfo.getMemTotal() * 1024);
		part.setMemFree(memInfo.getMemFree() * 1024);
		part.setBuffers(memInfo.getBuffers() * 1024);
		part.setCached(memInfo.getCached() * 1024);
		part.setValue(memInfo.getMemFree() * 1024); // Used to detect health.
		part.setTimestamp(System.currentTimeMillis());
		return part;
	}

	@Override
	protected String formatValue(long value) {
		return FileSizeUtils.getHumanReadable(value);
	}

	@Override
	protected String compareFieldName() {
		return "freeMem";
	}

	@Override
	public int compare(Long o1, Long o2) {
		return (o1 - o2) > 0 ? 1 : -1;
	}

	/**
	 * Memory health indicator properties.
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2018年6月3日
	 * @since
	 */
	@Configuration
	@ConditionalOnProperty(name = MemoryHealthProperties.CONF_P + ".enable", matchIfMissing = false)
	@ConfigurationProperties(prefix = MemoryHealthProperties.CONF_P)
	public static class MemoryHealthProperties extends AdvancedHealthProperties<MemoryPartition> {
		final public static String CONF_P = UMCConstants.KEY_UMC_METRIC_PREFIX + ".memory";
		private Map<String, MemoryPartition> partitions = new HashMap<>();

		@Override
		public Map<String, MemoryPartition> getPartitions() {
			return this.partitions;
		}

		@Override
		public void setPartitions(Map<String, MemoryPartition> partitions) {
			this.partitions = partitions;
		}

	}

	/**
	 * Memory description
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2018年6月3日
	 * @since
	 */
	public static class MemoryPartition extends Partition {
		/**
		 * Default least threshold memory: 200MB
		 */
		final public static long DEFAULT_MEM_THRESHOLD = 200 * 1024 * 1024L;
		private long freeThresholdBytes = DEFAULT_MEM_THRESHOLD;

		private long memTotal;
		private long memFree;
		private long buffers;
		private long cached;

		public MemoryPartition() {
			super();
		}

		public MemoryPartition(long freeThresholdBytes) {
			super();
			this.freeThresholdBytes = freeThresholdBytes;
		}

		public long getFreeThresholdBytes() {
			return freeThresholdBytes;
		}

		public void setFreeThresholdBytes(long freeThresholdBytes) {
			this.freeThresholdBytes = freeThresholdBytes;
		}

		@Override
		public long getValue() {
			return this.freeThresholdBytes;
		}

		@Override
		public void setValue(long value) {
			this.freeThresholdBytes = value;
		}

		public long getMemTotal() {
			return memTotal;
		}

		public void setMemTotal(long memTotal) {
			this.memTotal = memTotal;
		}

		public long getMemFree() {
			return memFree;
		}

		public void setMemFree(long memFree) {
			this.memFree = memFree;
		}

		public long getBuffers() {
			return buffers;
		}

		public void setBuffers(long buffers) {
			this.buffers = buffers;
		}

		public long getCached() {
			return cached;
		}

		public void setCached(long cached) {
			this.cached = cached;
		}

	}

	/**
	 * Memory health indicator configuration bootstrap
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2018年6月3日
	 * @since
	 */
	@Configuration
	@ConditionalOnBean({ MemoryHealthProperties.class })
	public static class MemoryHealthIndicatorConfiguration {

		@Bean(BEAN_NAME)
		public HealthIndicator memoryHealthIndicator(HealthAggregator healthAggregator, MemoryHealthProperties conf) {
			if (logger.isInfoEnabled())
				logger.info("Initial memoryHealthIndicator. {}", conf);

			AdvancedMemoryHealthIndicator healthIndicator = new AdvancedMemoryHealthIndicator(conf);
			Map<String, Health> healths = new LinkedHashMap<String, Health>();
			healths.put(AdvancedMemoryHealthIndicator.class.getSimpleName(), healthIndicator.health());
			return healthIndicator;
		}

	}

}
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

import java.io.File;
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

/**
 * Custom operation system disk space performance indicator.<br/>
 * Support multi partition monitoring.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年6月1日
 * @since
 */
public class AdvancedDiskSpaceHealthIndicator extends AbstractAdvancedHealthIndicator {
	final private static Logger logger = LoggerFactory.getLogger(AdvancedDiskSpaceHealthIndicator.class);
	final public static String BEAN_NAME = "advancedDiskSpaceHealthIndicator";

	public AdvancedDiskSpaceHealthIndicator(AdvancedHealthProperties<? extends Partition> conf) {
		super(conf);
	}

	@Override
	protected Partition latestPerfInfo(String name) throws Exception {
		// Disk space list info.
		DiskPartition part = new DiskPartition(new File(name).getUsableSpace());
		part.setTimestamp(System.currentTimeMillis());
		return part;
	}

	@Override
	protected String formatValue(long value) {
		return FileSizeUtils.getHumanReadable(value);
	}

	@Override
	protected String compareFieldName() {
		return "FreeSpace";
	}

	@Override
	public int compare(Long o1, Long o2) {
		return (o1 - o2) > 0 ? 1 : -1;
	}

	/**
	 * Disk space health indicator properties.
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2018年6月3日
	 * @since
	 */
	@Configuration
	@ConditionalOnProperty(name = DiskSpaceHealthProperties.CONF_P + ".enable", matchIfMissing = false)
	@ConfigurationProperties(prefix = DiskSpaceHealthProperties.CONF_P)
	public static class DiskSpaceHealthProperties extends AdvancedHealthProperties<DiskPartition> {
		final public static String CONF_P = UMCConstants.KEY_UMC_METRIC_PREFIX + ".disk";
		private Map<String, DiskPartition> partitions = new HashMap<>();

		public Map<String, DiskPartition> getPartitions() {
			return partitions;
		}

		public void setPartitions(Map<String, DiskPartition> partitions) {
			this.partitions = partitions;
		}

	}

	/**
	 * Disk space description
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2018年6月3日
	 * @since
	 */
	public static class DiskPartition extends Partition {
		/**
		 * Default least threshold disk directory space: 5GB
		 */
		final public static long DEFAULT_DS_THRESHOLD = 5 * 1024 * 1024 * 1024L;
		private long freeThresholdBytes = DEFAULT_DS_THRESHOLD;

		public DiskPartition() {
			super();
		}

		public DiskPartition(long freeThresholdBytes) {
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

	}

	/**
	 * Disk space health indicator configuration bootstrap
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2018年6月3日
	 * @since
	 */
	@Configuration
	@ConditionalOnBean({ DiskSpaceHealthProperties.class })
	public static class DiskSpaceHealthIndicatorConfiguration {

		@Bean(BEAN_NAME)
		public HealthIndicator diskSpaceHealthIndicator(HealthAggregator healthAggregator, DiskSpaceHealthProperties conf) {
			if (logger.isInfoEnabled())
				logger.info("Initial diskSpaceHealthIndicator. {}", conf);

			AdvancedDiskSpaceHealthIndicator healthIndicator = new AdvancedDiskSpaceHealthIndicator(conf);
			Map<String, Health> healths = new LinkedHashMap<String, Health>();
			healths.put(AdvancedDiskSpaceHealthIndicator.class.getSimpleName(), healthIndicator.health());
			return healthIndicator;
		}

	}

}
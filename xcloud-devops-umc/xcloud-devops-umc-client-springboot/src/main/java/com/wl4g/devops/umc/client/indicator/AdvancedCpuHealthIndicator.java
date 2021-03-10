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

import com.wl4g.devops.common.constant.UMCConstants;
import com.wl4g.devops.umc.client.utils.PlatformOSUtil;

/**
 * Custom operation system disk space performance indicator.<br/>
 * Support multi partition monitoring.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年6月1日
 * @since
 */
public class AdvancedCpuHealthIndicator extends AbstractAdvancedHealthIndicator {
	final private static Logger logger = LoggerFactory.getLogger(AdvancedCpuHealthIndicator.class);
	final public static String BEAN_NAME = "advancedCoreHealthIndicator";

	public AdvancedCpuHealthIndicator(AdvancedHealthProperties<? extends Partition> conf) {
		super(conf);
	}

	@Override
	protected Partition latestPerfInfo(String name) throws Exception {
		// Core/CPU list info.
		CpuPartition part = new CpuPartition((long) (PlatformOSUtil.cpuUsage() * 100));
		part.setTimestamp(System.currentTimeMillis());
		return part;
	}

	@Override
	protected String formatValue(long value) {
		return value + "%";
	}

	@Override
	protected String compareFieldName() {
		return "Usage";
	}

	@Override
	public int compare(Long o1, Long o2) {
		return (o2 - o1) > 0 ? 1 : -1;
	}

	/**
	 * Core/CPU health indicator properties.
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2018年6月3日
	 * @since
	 */
	@Configuration
	@ConditionalOnProperty(name = CpuHealthProperties.CONF_P + ".enable", matchIfMissing = true)
	@ConfigurationProperties(prefix = CpuHealthProperties.CONF_P)
	public static class CpuHealthProperties extends AdvancedHealthProperties<CpuPartition> {
		final public static String CONF_P = UMCConstants.KEY_UMC_METRIC_PREFIX + ".cpu";
		private Map<String, CpuPartition> partitions = new HashMap<>();

		@Override
		public Map<String, CpuPartition> getPartitions() {
			return this.partitions;
		}

		@Override
		public void setPartitions(Map<String, CpuPartition> partitions) {
			this.partitions = partitions;
		}

	}

	/**
	 * Core/CPU description
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2018年6月3日
	 * @since
	 */
	public static class CpuPartition extends Partition {
		/**
		 * Default threshold core used percent: 80%
		 */
		final public static long DEFAULT_CORE_THRESHOLD = 80L;
		final public static BigDecimal BIGDEC_100 = BigDecimal.valueOf(100);
		private long usedThresholdPercent = DEFAULT_CORE_THRESHOLD;

		public CpuPartition() {
			super();
		}

		public CpuPartition(long usedThresholdPercent) {
			super();
			this.usedThresholdPercent = usedThresholdPercent;
		}

		public long getUsedThresholdPercent() {
			return usedThresholdPercent;
		}

		public void setUsedThresholdPercent(long usedThresholdPercent) {
			this.usedThresholdPercent = usedThresholdPercent;
		}

		@Override
		public long getValue() {
			return this.usedThresholdPercent;
		}

		@Override
		public void setValue(long value) {
			this.usedThresholdPercent = value;
		}

	}

	/**
	 * Core/CPU health indicator configuration bootstrap
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2018年6月3日
	 * @since
	 */
	@Configuration
	@ConditionalOnBean({ CpuHealthProperties.class })
	public static class CpuHealthIndicatorConfiguration {

		@Bean(BEAN_NAME)
		public HealthIndicator coreHealthIndicator(HealthAggregator healthAggregator, CpuHealthProperties conf) {
			if (logger.isInfoEnabled())
				logger.info("Initial CoreHealthIndicator. {}", conf);

			AdvancedCpuHealthIndicator healthIndicator = new AdvancedCpuHealthIndicator(conf);
			Map<String, Health> healths = new LinkedHashMap<String, Health>();
			healths.put(AdvancedCpuHealthIndicator.class.getSimpleName(), healthIndicator.health());
			return healthIndicator;
		}

	}

}
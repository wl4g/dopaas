package com.wl4g.devops.iam.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.common.kit.access.IPAccessControl;
import com.wl4g.devops.common.kit.access.IPAccessControl.IPAccessProperties;

/**
 * IP access configuration processor.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年5月24日
 * @since
 */
@Configuration
public class IPAccessConfiguration {
	final static String IP_ACCESS_PREFIX = "spring.cloud.devops.iam.authc-internal-access";

	@Bean
	public IPAccessControl ipAccessControl(IPAccessProperties properties) {
		return new IPAccessControl(properties);
	}

	@Bean
	@ConfigurationProperties(prefix = IP_ACCESS_PREFIX)
	public IPAccessProperties ipAccessProperties() {
		return new IPAccessProperties();
	}

}
package com.wl4g.devops.umc.config;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.umc.notify.CompositeStatusChangeNotifier;

import de.codecentric.boot.admin.config.NotifierConfiguration.NotifierListenerConfiguration;

/**
 * Automatic configuration of custom message notification. <br/>
 * http://www.gdtarena.com/gdkc/javacxy/13554.html <br/>
 * Reference:
 * de.codecentric.boot.admin.config.NotifierConfiguration.SlackNotifierConfiguration.slackNotifier()
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年5月28日
 * @since
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.boot.admin.notify.composite", name = "enabled")
@AutoConfigureBefore({ NotifierListenerConfiguration.class,
		de.codecentric.boot.admin.config.NotifierConfiguration.CompositeNotifierConfiguration.class })
public class CompositeNotifierConfiguration {

	@Bean
	@ConditionalOnMissingBean
	@ConfigurationProperties("spring.boot.admin.notify.composite")
	public CompositeStatusChangeNotifier compositeStatusChangeNotifier() {
		return new CompositeStatusChangeNotifier();
	}

}

package com.wl4g.devops.scm.client.configure.refresh;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.web.client.RestTemplate;

import com.wl4g.devops.common.bean.scm.model.ReleaseModel;
import com.wl4g.devops.common.bean.scm.model.BaseModel.ReleaseMeta;
import com.wl4g.devops.scm.client.config.InstanceProperties;
import com.wl4g.devops.scm.client.config.RetryProperties;

/**
 * ApplicationContextInitializer instructions see:
 * https://blog.csdn.net/leileibest_437147623/article/details/81074174
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年10月22日
 * @since
 */
public class ScmPropertySourceLocator implements PropertySourceLocator {
	final public static String devOpsPropertySource = "_devOpsPropertySource";
	final private Logger log = LoggerFactory.getLogger(getClass());

	private AbstractBeanRefresher refresher;

	public ScmPropertySourceLocator(String baseUri, RestTemplate restTemplate, InstanceProperties instanceProps,
			ConfigurableEnvironment environment, RetryProperties retryProps) {
		super();
		this.refresher = new ConfigureBeanRefresher(baseUri, restTemplate, retryProps, instanceProps, environment, null, null);
	}

	@Override
	public PropertySource<?> locate(Environment environment) {
		if (log.isInfoEnabled()) {
			log.info("DevOps bootstrap config is enabled for environment {}", environment);
		}

		/*
		 * Define composite property source. {@link
		 * com.wl4g.devops.scm.client.configure.refresh.AbstractBeanRefresher#
		 * addConfigToEnvironment()}
		 */
		CompositePropertySource composite = new CompositePropertySource(devOpsPropertySource); // By-default
		if (environment instanceof ConfigurableEnvironment) {
			try {
				// 1.1 Get remote latest property-sources(version/releaseId is
				// null).
				ReleaseModel config = this.refresher.getRemoteReleaseConfig(new ReleaseMeta());

				// 1.2 Resolves cipher resource.
				this.refresher.resolvesCipherSource(config);

				// 1.3 Add configuration to environment.
				composite = config.convertCompositePropertySource(devOpsPropertySource);

			} catch (Exception e) {
				log.error("DevOps bootstrap config refresh failed. {}", ExceptionUtils.getRootCauseMessage(e));
			}
		}

		// When you refresh the configuration source, you need to clean it up.
		// See:com.wl4g.devops.client.configure.refresh.AbstractBeanRefresher.addConfigToEnvironment()
		return composite;
	}

}

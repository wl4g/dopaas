package com.wl4g.devops.shell.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.Assert;

import com.wl4g.devops.shell.AbstractActuator;
import com.wl4g.devops.shell.config.ShellProperties;
import com.wl4g.devops.shell.registry.ShellBeanRegistry;

/**
 * Abstract shell component processor
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月14日
 * @since
 */
public abstract class AbstractProcessor extends AbstractActuator implements DisposableBean {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Shell properties configuration
	 */
	final protected ShellProperties config;

	public AbstractProcessor(ShellProperties config, ShellBeanRegistry registry) {
		super(registry);
		Assert.notNull(config, "config must not be null");
		this.config = config;
	}

}

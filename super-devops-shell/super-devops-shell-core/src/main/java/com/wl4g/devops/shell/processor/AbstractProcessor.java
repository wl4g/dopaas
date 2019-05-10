/*
 * Copyright 2015 the original author or authors.
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

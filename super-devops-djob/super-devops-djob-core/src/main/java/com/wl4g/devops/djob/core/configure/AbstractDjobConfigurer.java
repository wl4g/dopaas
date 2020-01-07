/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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
package com.wl4g.devops.djob.core.configure;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * AbstractDjobConfigurer
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年12月12日
 * @since
 */
public abstract class AbstractDjobConfigurer implements ApplicationRunner {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	final private AtomicBoolean configurered = new AtomicBoolean(false);

	@Autowired
	protected ApplicationContext actx;

	@Autowired
	protected ConfigurableEnvironment environment;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (configurered.compareAndSet(false, true)) {
			doConfigurer();
		}
	}

	protected abstract void doConfigurer() throws Exception;

	@SuppressWarnings("unchecked")
	protected <T> T registerBean(BeanDefinitionBuilder factory, String beanName) {
		DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) actx.getAutowireCapableBeanFactory();
		defaultListableBeanFactory.registerBeanDefinition(beanName + "SpringJobScheduler", factory.getBeanDefinition());
		return (T) actx.getBean(beanName + "SpringJobScheduler");
	}

}
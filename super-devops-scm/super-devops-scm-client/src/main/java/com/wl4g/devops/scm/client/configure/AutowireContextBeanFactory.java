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
package com.wl4g.devops.scm.client.configure;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonFormat.Value;

/**
 * Spring container context holder bean factory.</br>
 * </br>
 * The attributes of {@link ConfigurationProperties} and {@link Value}
 * annotation are re injected, See:https://m.imooc.com/mip/article/37039<br/>
 * https://blog.csdn.net/qq_28580959/article/details/60129329
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年10月19日
 * @since
 * @see org.springframework.cloud.context.properties.ConfigurationPropertiesRebinder<br/>
 *      org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor<br/>
 *      org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#populateBean
 */
public class AutowireContextBeanFactory implements ApplicationContextAware {

	private ConfigurableApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		Assert.notNull(applicationContext, "object applicationContext is required; it must not be null.");
		this.applicationContext = (ConfigurableApplicationContext) applicationContext;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public AutowireCapableBeanFactory getAutowireCapableBeanFactory() {
		return applicationContext.getAutowireCapableBeanFactory();
	}

	public DefaultListableBeanFactory getBeanFactory() {
		return (DefaultListableBeanFactory) applicationContext.getBeanFactory();
	}

	@SuppressWarnings("unchecked")
	public <T> T getBean(String name) {
		return (T) applicationContext.getBean(name);
	}

	public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {
		return getApplicationContext().getBeansWithAnnotation(RefreshBean.class);
	}

	/**
	 * Register bean.
	 * 
	 * @param beanId
	 *            ID of registered bean
	 * @param className
	 *            Bean's className, three ways of obtaining: <br/>
	 *            1, direct writing, such as: com.mvc.entity.User.<br/>
	 *            2, User.class.getName.<br/>
	 *            3.user.getClass ().GetName ()
	 */
	public void registerBean(String beanId, String className) {
		// Get the BeanDefinitionBuilder
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(className);
		// Get the BeanDefinition
		AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
		// beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
		// Register the bean
		getBeanFactory().registerBeanDefinition(beanId, beanDefinition);
	}

	/**
	 * Destruction instance method does not consider the inheritance and
	 * dependency destruction of bean.
	 * 
	 * @throws ClassNotFoundException
	 * @throws BeansException
	 */
	public void destoryBean(String beanName) {
		AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
		/*
		 * The singleton and prototype modes are saved in the container, whereas
		 * the latter is not saved in the container every time a new object is
		 * created (equivalent to every new object) when a bean Factory. getBean
		 * is called.
		 */
		Object bean = applicationContext.getBean(beanName);
		if (bean != null && beanFactory.isSingleton(beanName)) {
			beanFactory.destroyBean(bean);
		}

		// Remove definition from registry.
		if (getBeanFactory().containsBeanDefinition(beanName)) {
			getBeanFactory().removeBeanDefinition(beanName);
		}

	}

	/**
	 * Re-initialize bean
	 * 
	 * @param existingBean
	 *            object of registered bean
	 * @param beanId
	 *            ID of registered bean
	 * @see https://m.imooc.com/mip/article/37039<br/>
	 *      https://blog.csdn.net/qq_28580959/article/details/60129329
	 */
	public void reinitializationBean(Object existingBean, String beanId) {
		// All destruction methods defined by callback bean.
		getAutowireCapableBeanFactory().destroyBean(existingBean);

		// Re-infuse all @Value annotation attribute values from the current
		// context.
		getAutowireCapableBeanFactory().autowireBean(existingBean);

		// Re-infuse all @ConfigurationProperties annotation attribute values
		// from the current context.
		getAutowireCapableBeanFactory().initializeBean(existingBean, beanId);
	}

}
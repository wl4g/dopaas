package com.wl4g.devops.shell.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

import com.wl4g.devops.shell.annotation.ShellComponent;
import com.wl4g.devops.shell.registry.ShellBeanRegistry;

/**
 * Annotation shell configuration registry
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月14日
 * @since
 */
public class AnnotationBeanRegistry extends ShellBeanRegistry implements BeanPostProcessor {
	private static final long serialVersionUID = 1281712204663635026L;

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		ShellComponent component = AnnotationUtils.findAnnotation(bean.getClass(), ShellComponent.class);
		if (component != null) {
			register(bean);
		}
		return bean;
	}

}

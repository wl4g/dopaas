package com.wl4g.devops.scm.client.configure;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wl4g.devops.common.utils.AopUtils;
import com.wl4g.devops.scm.client.config.SpringContexts;

public class DefaultRefreshBeanRegistry implements RefreshBeanRegistry {
	final private static long serialVersionUID = 2389115852129467732L;

	final private Logger log = LoggerFactory.getLogger(getClass());

	private SpringContexts context;

	public DefaultRefreshBeanRegistry(SpringContexts context) {
		super();
		this.context = context;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// Post registration handle.
		this.postRegistrationHandle();

		// Print refresh scope bean.
		this.context.getBeansWithAnnotation(RefreshBean.class).values().stream().forEach(obj -> {
			log.info("@RefreshScope: {}", AopUtils.getTargetClass(obj).getName());
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getRefreshBean(Class<?> clazz) {
		// Get @RefreshConfig bean.
		Map<String, Object> beanMap = this.context.getBeansWithAnnotation(RefreshBean.class);
		// Save to refresh registry of bean.
		if (beanMap != null) {
			for (Object obj : beanMap.values()) {
				if (obj.getClass() == clazz) {
					return (T) obj;
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getRefreshBean(String beanId) {
		// Get @RefreshConfig bean.
		Map<String, Object> beanMap = this.context.getBeansWithAnnotation(RefreshBean.class);
		// Save to refresh registry of bean.
		if (beanMap != null) {
			for (String beanId0 : beanMap.keySet()) {
				if (beanId0.equals(beanId)) {
					return (T) beanMap.get(beanId);
				}
			}
		}
		return null;
	}

	@Override
	public Map<String, Object> getRefreshBeans() {
		Map<String, Object> beans = this.context.getBeansWithAnnotation(RefreshBean.class);
		return beans == null ? Collections.emptyMap() : beans;
	}

	private void postRegistrationHandle() {

	}

}

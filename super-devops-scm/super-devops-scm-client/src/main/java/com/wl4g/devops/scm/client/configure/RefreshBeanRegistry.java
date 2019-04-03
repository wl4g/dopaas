package com.wl4g.devops.scm.client.configure;

import java.io.Serializable;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

public interface RefreshBeanRegistry extends InitializingBean, Serializable {

	<T> T getRefreshBean(Class<?> clazz);

	<T> T getRefreshBean(String beanId);

	Map<String, Object> getRefreshBeans();

}

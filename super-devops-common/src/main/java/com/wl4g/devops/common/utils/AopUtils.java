/**
 * Copyright (c) 2014 ~ , wangl.sir Individual Inc. All rights reserved. It was made by wangl.sir Auto Build Generated file. Contact us 983708408@qq.com.
 */
/**
 * Copyright (c) 2014 ~ , wangl.sir Individual Inc. All rights reserved. It was made by wangl.sir Auto Build Generated file. Contact us 983708408@qq.com.
 */
package com.wl4g.devops.common.utils;

import org.springframework.aop.framework.Advised;

/**
 * Increasing the support of target objects for obtaining JDK dynamic proxy
 * /CGLIB proxy object agents
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @time 2016年11月9日
 * @since
 */
public abstract class AopUtils extends org.springframework.aop.support.AopUtils {

	/**
	 * Get target object
	 * 
	 * @param candidate
	 *            Proxy object
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getTarget(Object candidate) {
		try {
			if (candidate == null) {
				return null;
			}
			if (isAopProxy(candidate) && (candidate instanceof Advised)) {
				return (T) ((Advised) candidate).getTargetSource().getTarget();
			}
		} catch (Exception e) {
			throw new IllegalStateException("Failed to unwarp proxied object.", e);
		}
		return (T) candidate;
	}

}

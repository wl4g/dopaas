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
package com.wl4g.devops.common.utils;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

/**
 * Increasing the support of target objects for obtaining JDK dynamic proxy
 * /CGLIB proxy object agents
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @time 2016年11月9日
 * @since
 */
public abstract class AopUtils2 extends AopUtils {

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
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
package com.wl4g.devops.iam.client.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.wl4g.devops.iam.client.authc.secondary.SecondaryAuthenticator;

/**
 * Safety reinforcement for inspection of secondary certification.
 * 
 * @author wangl.sir
 * @version v1.0 2019年2月28日
 * @since
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Documented
public @interface SecondaryAuthenticate {

	/**
	 * Function ID, not duplicated
	 * 
	 * @return
	 */
	String funcId();

	/**
	 * Handler class
	 * 
	 * @return
	 */
	Class<? extends SecondaryAuthenticator> handleClass();

}
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
package com.wl4g.devops.components.shell.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link ShellMethod}
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年04月17日
 * @since
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Documented
public @interface ShellMethod {

	/**
	 * Command names definition.</br>
	 * 
	 * e.g:
	 * 
	 * <pre>
	 * $ > mylist
	 * </pre>
	 * 
	 * @return
	 */
	String[] keys();

	/**
	 * Command group name.
	 * 
	 * @return
	 */
	String group();

	/**
	 * Whether to allow command line execution to be interrupted.
	 * 
	 * @return
	 */
	InterruptType interruptible() default InterruptType.NOT_ALLOW;

	/**
	 * Command help description.
	 * 
	 * @return
	 */
	String help();

	/**
	 * {@link InterruptType}
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version 2020年2月4日 v1.0.0
	 * @see
	 */
	public static enum InterruptType {
		ALLOW, NOT_ALLOW
	}

}
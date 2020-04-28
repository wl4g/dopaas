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

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Documented
public @interface ShellOption {

	/**
	 * GNU commands short option prefix.
	 */
	final public static String GNU_CMD_SHORT = "-";

	/**
	 * GNU commands long option prefix.
	 */
	final public static String GNU_CMD_LONG = "--";

	/**
	 * Commands short option.
	 */
	String opt();

	/**
	 * Commands long option.
	 */
	String lopt();

	/**
	 * Default value for shell option argument.
	 */
	String defaultValue() default "";

	/**
	 * Specifying shell options is required.
	 */
	boolean required() default true;

	/**
	 * Specify shell option parameters to help explain.
	 * 
	 * @return
	 */
	String help();

}
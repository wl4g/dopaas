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
package com.wl4g.devops.shell.processor;

import static java.util.Objects.isNull;

/**
 * Shell console utility tools.
 * 
 * @author wangl.sir
 * @version v1.0 2019年5月24日
 * @since
 */
public abstract class ShellHolder {
	/** Shell context cache. */
	final private static ThreadLocal<ShellContext> contextCache = new InheritableThreadLocal<>();

	/**
	 * Bind shell context.
	 * 
	 * @param context
	 * @return
	 */
	static ShellContext bind(ShellContext context) {
		if (context != null) {
			contextCache.set(context);
		}
		return context;
	}

	/**
	 * Got bind shell context.
	 * 
	 * @return
	 */
	public static ShellContext currentShell() {
		return currentShell(false);
	}

	/**
	 * Got bind shell context.
	 * 
	 * @param assertion
	 * @return
	 */
	public static ShellContext currentShell(boolean assertion) {
		ShellContext context = contextCache.get();
		if (assertion && isNull(context)) {
			throw new IllegalStateException("The context object was not retrieved. first use bind()");
		}
		return context;
	}

	/**
	 * Print message to client console
	 * 
	 * @param message
	 */
	public static ShellContext currentPrintf(String message) {
		return currentShell(true).printf(message);
	}

	/**
	 * Manually open data flow message transaction output.
	 */
	public static ShellContext currentOpen() {
		return currentShell(true).open();
	}

	/**
	 * Manually end data flow message transaction output.
	 */
	public static void currentClose() {
		currentShell(true).close();
	}

	/**
	 * Are you currently in an interrupt state? (if the current thread does not
	 * open the shell channel, it will return false, that is, uninterrupted)
	 * 
	 * @return
	 */
	public static boolean isInterruptIfNecessary() {
		ShellContext context = currentShell();
		return context != null ? context.isInterruptIfNecessary() : false;
	}

}
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
package com.wl4g.devops.shell.utils;

import com.wl4g.devops.shell.processor.ShellContext;

/**
 * Shell console utility tools.
 * 
 * @author wangl.sir
 * @version v1.0 2019年5月24日
 * @since
 */
public abstract class ShellContextHolder {

	/** Shell context cache. */
	final private static ThreadLocal<ShellContext> contextCache = new InheritableThreadLocal<>();

	/**
	 * Bind shell context.
	 * 
	 * @param context
	 * @return
	 */
	public static ShellContext bind(ShellContext context) {
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
	public static ShellContext getContext() {
		return getContext(false);
	}

	/**
	 * Got bind shell context.
	 * 
	 * @param assertion
	 * @return
	 */
	public static ShellContext getContext(boolean assertion) {
		ShellContext context = contextCache.get();
		if (assertion && context == null) {
			throw new IllegalStateException("The context object was not retrieved. first use bind()");
		}
		return context;
	}

	/**
	 * Print quietly message to client console
	 * 
	 * @param message
	 */
	public static void printfQuietly(String message) {
		ShellContext context = getContext();
		if (context != null) {
			context.printfQuietly(message);
		}
	}

	/**
	 * Print quietly throwable message to client console
	 * 
	 * @param message
	 */
	public static void printfQuietly(Throwable th) {
		ShellContext context = getContext();
		if (context != null) {
			context.printfQuietly(th);
		}
	}

	/**
	 * Print message to client console
	 * 
	 * @param message
	 */
	public static void printf(String message) {
		ShellContext context = getContext();
		Assert.notNull(context, "The context object was not retrieved. first use bind()");
		context.printf(message);
	}

	/**
	 * Manually open data flow message transaction output.
	 */
	public static void open() {
		getContext(true).open();
	}

	/**
	 * Manually end data flow message transaction output.
	 */
	public static void close() {
		getContext(true).close();
	}

	/**
	 * Are you currently in an interrupt state? (if the current thread does not
	 * open the shell channel, it will return false, that is, uninterrupted)
	 * 
	 * @return
	 */
	public static boolean isInterruptIfNecessary() {
		ShellContext context = getContext();
		return context != null ? context.isInterruptIfNecessary() : false;
	}

	/**
	 * Asserting whether the current shell execution task has been interrupted
	 * is similar to JDK thread. </br>
	 * (only valid for task threads created by the current shell channel, which
	 * is thread-safe)
	 * 
	 * @throws InterruptedException
	 */
	public static void assertInterruptIfNecessary() throws InterruptedException {
		if (isInterruptIfNecessary()) {
			throw new InterruptedException(String.format("Task that interrupted!"));
		}
	}

}

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

import static com.wl4g.devops.shell.bean.LineResultState.RESP_WAIT;
import static org.apache.commons.lang3.exception.ExceptionUtils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shell console utility tools.
 * 
 * @author wangl.sir
 * @version v1.0 2019年5月24日
 * @since
 */
public abstract class ShellConsoleHolder {

	final private static Logger log = LoggerFactory.getLogger(ShellConsoleHolder.class);

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
	 * Output quietly message to client console
	 * 
	 * @param message
	 */
	public static void writeQuietly(String message) {
		ShellContext context = getContext();
		if (context != null && context.getState() == RESP_WAIT) {
			try {
				context.write(message);
			} catch (Exception e) {
				log.warn("Write error => {}", getRootCauseMessage(e));
			}
		}
	}

	/**
	 * Output message to client console
	 * 
	 * @param message
	 */
	public static void write(String message) {
		ShellContext context = getContext();
		if (context == null) {
			throw new IllegalStateException("The context object was not retrieved. first use bind()");
		}
		if (context.getState() != RESP_WAIT) {
			throw new IllegalStateException("The current console channel may be closed!");
		}

		context.write(message);
	}

	/**
	 * Manually open data flow message transaction output.
	 */
	public static void beginQuietly() {
		getContext().begin();
	}

	/**
	 * Manually open data flow message transaction output.
	 */
	public static void begin() {
		getContext(true).begin();
	}

	/**
	 * Manually end data flow message transaction output.
	 */
	public static void endQuietly() {
		getContext().end();
	}

	/**
	 * Manually end data flow message transaction output.
	 */
	public static void end() {
		getContext(true).end();
	}

}

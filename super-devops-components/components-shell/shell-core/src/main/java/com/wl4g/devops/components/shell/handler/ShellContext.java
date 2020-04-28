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
package com.wl4g.devops.components.shell.handler;

import com.wl4g.devops.components.shell.exception.ChannelShellException;
import com.wl4g.devops.components.shell.exception.NoSupportedInterruptShellException;
import com.wl4g.devops.components.shell.registry.ShellAware;

import java.util.Collection;

/**
 * Shell handler context
 *
 * @author wangl.sir
 * @version v1.0 2019年5月24日
 * @since
 */
public interface ShellContext extends ShellAware {
	final public static String DEFAULT_INTERRUPT_LISTENER = "defaultInterruptEventListener";

	/**
	 * Complete processing the current command line channel, effect: the client
	 * will reopen the console prompt.</br>
	 * </br>
	 * <b><font color=red>Note: Don't forget to execute it, or the client
	 * console will pause until it timeout.</font><b>
	 * 
	 * @throws ChannelShellException
	 */
	void completed() throws ChannelShellException;

	/**
	 * Are you currently in an interrupt state? (if the current thread does not
	 * open the shell channel, it will return false, that is, uninterrupted)
	 * 
	 * @return
	 * @throws NoSupportedInterruptShellException
	 */
	boolean isInterrupted() throws NoSupportedInterruptShellException;

	/**
	 * Get unmodifiable event listeners.
	 * 
	 * @return
	 */
	Collection<ShellEventListener> getUnmodifiableEventListeners();

	/**
	 * Add event listener
	 * 
	 * @param name
	 * @param eventListener
	 * @return
	 */
	boolean addEventListener(String name, ShellEventListener eventListener);

	/**
	 * Remove event listener
	 * 
	 * @param name
	 * @return
	 */
	boolean removeEventListener(String name);

}
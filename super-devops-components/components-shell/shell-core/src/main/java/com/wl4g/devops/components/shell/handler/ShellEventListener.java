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

import java.util.EventListener;

import com.wl4g.devops.components.shell.signal.ChannelState;

/**
 * {@link ShellEventListener}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年2月2日 v1.0.0
 * @see
 */
public interface ShellEventListener extends EventListener {

	/**
	 * On accpet command event.
	 * 
	 * @param context
	 * @param command
	 */
	default void onCommand(AbstractShellContext context, String command) {
		// Ignore
	}

	/**
	 * On pre-interrupt event.
	 * 
	 * @param context
	 */
	default void onPreInterrupt(AbstractShellContext context) {
	}

	/**
	 * On interrupt event.
	 * 
	 * @param context
	 * @param confirm
	 */
	default void onInterrupt(AbstractShellContext context, boolean confirm) {
		if (confirm) {
			context.setState(ChannelState.INTERRUPTED);
		}
	}

}
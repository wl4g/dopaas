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
package com.wl4g.devops.components.shell.signal;

/**
 * Line result message state
 * 
 * @author wangl.sir
 * @version v1.0 2019年5月24日
 * @since
 */
public enum ChannelState {

	NEW,

	/**
	 * Currently processing command line task waiting.
	 */
	RUNNING,

	/**
	 * The current processing command line task has been interrupted.
	 */
	INTERRUPTED,

	/**
	 * Command line task currently processed completed.
	 */
	COMPLETED;

}
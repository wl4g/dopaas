/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.paas.scm.client.event;

import java.util.EventListener;

/**
 * {@link ConfigEventListener}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-11
 * @since
 */
public interface ConfigEventListener extends EventListener {

	/**
	 * On release changed event.
	 * 
	 * @param event
	 */
	void onRefresh(RefreshConfigEvent event);

	/**
	 * On Reporting event.
	 * 
	 * @param event
	 */
	default void onReporting(ReportingConfigEvent event) {
		// Ignore
	}

	/**
	 * On report config changed event
	 * 
	 * @param event
	 */
	default void onCheckpoint(CheckpointConfigEvent event) {
		// Ignore
	}

}
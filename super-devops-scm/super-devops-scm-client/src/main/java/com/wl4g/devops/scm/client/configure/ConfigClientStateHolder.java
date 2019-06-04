/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.scm.client.configure;

/**
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年6月2日
 * @since
 */
public final class ConfigClientStateHolder {

	private ConfigClientStateHolder() {
		throw new IllegalStateException("Can't instantiate a utility class");
	}

	private static ThreadLocal<String> state = new ThreadLocal<>();

	public static void resetState() {
		state.remove();
	}

	public static String getState() {
		return state.get();
	}

	public static void setState(String newState) {
		if (newState == null) {
			resetState();
			return;
		}
		state.set(newState);
	}

}
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wl4g.devops.components.shell.registry.TargetMethodWrapper;

/**
 * Meta configuration/commands message
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月4日
 * @since
 */
public class MetaSignal extends Signal {
	private static final long serialVersionUID = -8574315248835509685L;

	/**
	 * Shell component target methods
	 */
	final private Map<String, TargetMethodWrapper> registedMethods = new ConcurrentHashMap<>(16);

	public MetaSignal() {
	}

	public MetaSignal(Map<String, TargetMethodWrapper> wrapper) {
		if (wrapper != null) {
			this.registedMethods.putAll(wrapper);
		}
	}

	public Map<String, TargetMethodWrapper> getRegistedMethods() {
		return registedMethods;
	}

	@Override
	public String toString() {
		return super.toString().concat(registedMethods.values().toString());
	}

}
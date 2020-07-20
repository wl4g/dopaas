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
package com.wl4g.devops.coss.server.handler;

import static com.wl4g.devops.components.tools.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.util.Objects.nonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link DefaultCossActionProvider}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年7月15日
 * @since
 */
public class DefaultCossActionProvider implements CossActionProvider {

	/**
	 * Coss action handler provider regsitry.
	 */
	private final static Map<CossAction, GenericCossChannelHandler> registry = new ConcurrentHashMap<>(16);

	@Override
	public final GenericCossChannelHandler getHandler(CossAction action) {
		notNullOf(action, "cossAction");
		return registry.get(action);
	}

	@Override
	public final void register(CossAction action, GenericCossChannelHandler handler) {
		notNullOf(action, "action");
		notNullOf(handler, "actionHandler");
		if (nonNull(registry.putIfAbsent(action, handler))) {
			throw new IllegalStateException(format("Already register action: %s handler: %s", action, handler));
		}
	}

}
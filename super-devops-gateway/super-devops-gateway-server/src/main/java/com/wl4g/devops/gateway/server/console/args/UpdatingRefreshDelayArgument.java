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
package com.wl4g.devops.gateway.server.console.args;

import com.wl4g.devops.components.shell.annotation.ShellOption;

import java.io.Serializable;

import static com.wl4g.devops.components.tools.common.lang.Assert2.notNullOf;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

/**
 * {@link UpdatingRefreshDelayArgument}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-07-23
 * @since
 */
public class UpdatingRefreshDelayArgument implements Serializable {
	private static final long serialVersionUID = -90377698662015272L;

	@ShellOption(opt = "t", lopt = "refreshDelayMs", help = "Gateway configuration refresh delay ms", required = true)
	private Long refreshDelayMs;

	public Long getRefreshDelayMs() {
		notNull(refreshDelayMs, "Update refresh time must not be null.");
		isTrue(refreshDelayMs > 0, "Update refresh time must greater than 0.");
		return refreshDelayMs;
	}

	public void setRefreshDelayMs(Long refreshTimeMs) {
		notNullOf(refreshTimeMs, "refreshTimeMs");
		isTrue(refreshTimeMs > 0, "Updating refreshDelayMs > 0");
		this.refreshDelayMs = refreshTimeMs;
	}

}
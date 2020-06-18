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
package com.wl4g.devops.support.cli.destroy;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

import java.io.Serializable;

import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.devops.support.cli.GenericProcessManager.*;

/**
 * Command-line process signal model.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年10月21日
 * @since
 */
public class DestroySignal implements Serializable {
	private static final long serialVersionUID = -7048011146751774527L;

	/** Destroy processId */
	private String processId = EMPTY;

	/** Destroy timeoutMs */
	private Long timeoutMs;

	public DestroySignal() {
		super();
	}

	public DestroySignal(String processId, Long timeoutMs) {

		this.processId = processId;
		this.timeoutMs = timeoutMs;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		hasText(processId, "Destroy processId must not be null.");
		this.processId = processId;
	}

	public Long getTimeoutMs() {
		return timeoutMs;
	}

	public void setTimeoutMs(Long timeoutMs) {
		notNull(timeoutMs, "Destroy timeoutMs must not be null.");
		isTrue(timeoutMs >= DEFAULT_DESTROY_INTERVALMS,
				String.format("Destroy timeoutMs must be less than or equal to %s", DEFAULT_DESTROY_INTERVALMS));
		this.timeoutMs = timeoutMs;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [" + toJSONString(this) + "]";
	}

}
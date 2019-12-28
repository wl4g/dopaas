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
package com.wl4g.devops.shell.bean;

import static com.wl4g.devops.shell.bean.RunState.*;
import static com.wl4g.devops.tool.common.lang.Assert2.*;

/**
 * Result transform message
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月4日
 * @since
 */
public class ResultMessage extends Message {
	private static final long serialVersionUID = -8574311246731909685L;

	final private RunState state;

	final private String content;

	public ResultMessage(String content) {
		this(NONCE, content);
	}

	public ResultMessage(RunState state, String content) {
		notNull(state, "State must not be empty");
		this.state = state;
		this.content = content;
	}

	public RunState getState() {
		return state;
	}

	public String getContent() {
		return content;
	}

	@Override
	public String toString() {
		return "ResultMessage [state=" + state + ", content=" + content + "]";
	}

}
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

import static com.wl4g.devops.components.tools.common.lang.Assert2.*;
import static org.apache.commons.lang3.exception.ExceptionUtils.*;

/**
 * Stderr exception message.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月4日
 * @since
 */
public class StderrSignal extends Signal {
	private static final long serialVersionUID = -8574315277731909685L;

	final private Throwable throwable;

	public StderrSignal(Throwable throwable) {
		notNull(throwable, "throwable must not be null");
		this.throwable = throwable;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	@Override
	public String toString() {
		return "stderr [" + getRootCauseMessage(throwable) + "]";
	}

}
/*
 * Copyright 2015 the original author or authors.
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

import static org.apache.commons.lang3.exception.ExceptionUtils.*;
import com.wl4g.devops.shell.utils.Assert;

/**
 * Exception result message
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月4日
 * @since
 */
public class ExceptionMessage extends Message {
	private static final long serialVersionUID = -8574315277731909685L;

	final private Throwable throwable;

	public ExceptionMessage(Throwable throwable) {
		Assert.notNull(throwable, "throwable must not be null");
		this.throwable = throwable;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	@Override
	public String toString() {
		return "ExceptionMessage [throwable=" + getRootCauseMessage(throwable) + ", toString()=" + super.toString() + "]";
	}

}

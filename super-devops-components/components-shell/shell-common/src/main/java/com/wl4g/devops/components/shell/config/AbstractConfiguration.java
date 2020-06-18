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
package com.wl4g.devops.components.shell.config;

import static com.wl4g.devops.components.tools.common.lang.Assert2.*;
import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.isNumeric;

import java.io.Serializable;

/**
 * Abstract shell configuration
 * 
 * @author wangl.sir
 * @version v1.0 2019年5月8日
 * @since
 */
public abstract class AbstractConfiguration implements Serializable {

	final private static long serialVersionUID = -5897277204687388946L;

	/**
	 * Default shell console listen port range begin.
	 */
	final public static int DEFAULT_PORT_BEGIN = 60100;

	/**
	 * Default shell console listen port range end.
	 */
	final public static int DEFAULT_PORT_END = 60200;

	/**
	 * Listening serve socket port range.
	 */
	private String portRange;

	//
	// Temporary
	//

	/**
	 * Begin listen socket port
	 */
	private transient int beginPort;

	/**
	 * End listen socket port
	 */
	private transient int endPort;

	public AbstractConfiguration() {
		setPortRange(DEFAULT_PORT_BEGIN + ":" + DEFAULT_PORT_END);
	}

	public String getPortRange() {
		return portRange;
	}

	public void setPortRange(String portRange) {
		hasText(portRange, "Listen port range must not be empty");
		String[] part = portRange.split(":");
		isTrue(part.length == 2 && isNumeric(part[0]) && isNumeric(part[1]), "Invalid listen port range. (e.g. 66100:66200)");
		int begin = parseInt(part[0]);
		int end = parseInt(part[1]);
		isTrue(begin > 1024 && begin < 65535 && end > 1024 && end < 65535 && end > begin,
				String.format(
						"Both start and end ports must be between 1024 and 65535, And the end port must be greater than the begin port, actual is %s",
						portRange));
		this.portRange = portRange;
		this.beginPort = begin;
		this.endPort = end;
	}

	public int getBeginPort() {
		return beginPort;
	}

	public int getEndPort() {
		return endPort;
	}

}
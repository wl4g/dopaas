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

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.util.Assert;

import com.wl4g.devops.components.shell.config.AbstractConfiguration;

/**
 * Shell properties configuration
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月1日
 * @since
 */
public class ShellProperties extends AbstractConfiguration {

	private static final long serialVersionUID = -24798955162679115L;

	/**
	 * listening TCP backlog
	 */
	private int backlog = 16;

	/**
	 * Listening server socket bind address
	 */
	private String bindAddr = "127.0.0.1";

	/**
	 * Maximum number of concurrent client connections.
	 */
	private int maxClients = 2;

	public int getBacklog() {
		return backlog;
	}

	public void setBacklog(int backlog) {
		Assert.isTrue(backlog > 0, String.format("backlog must greater than 0, actual is %s", backlog));
		this.backlog = backlog;
	}

	public String getBindAddr() {
		return bindAddr;
	}

	public InetAddress getInetBindAddr() {
		try {
			return InetAddress.getByName(getBindAddr());
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	public void setBindAddr(String bindAddr) {
		Assert.hasText(bindAddr, "binAddr is emtpy, please check configure");
		this.bindAddr = bindAddr;
	}

	public int getMaxClients() {
		return maxClients;
	}

	public void setMaxClients(int maxClients) {
		Assert.isTrue(maxClients > 0, String.format("maxClients must greater than 0, actual is %s", backlog));
		this.maxClients = maxClients;
	}

}
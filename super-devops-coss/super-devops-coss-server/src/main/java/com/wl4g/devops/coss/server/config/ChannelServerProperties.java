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
package com.wl4g.devops.coss.server.config;

import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.toJSONString;

import java.io.Serializable;

import io.netty.handler.logging.LogLevel;

/**
 * {@link ChannelServerProperties}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年6月17日
 * @since
 */
public class ChannelServerProperties implements Serializable {
	private static final long serialVersionUID = -1824139235237935352L;

	/** Server bind inet host. */
	private String inetHost = "127.0.0.1";

	/** Server bind inet port. */
	private int inetPort = 10030;

	/** Server channel TCP backlog. */
	private int backlog = 4096;

	/** Server channel read timeout seconds. */
	private int readTimeoutSec = 30 * 60 * 5;

	/** Server channel write timeout seconds. */
	private int writeTimeoutSec = 30 * 60 * 5;

	/** Server channel read/write timeout seconds. */
	private int allTimeoutSec = 30 * 60 * 5;

	/** Server channel read/write timeout seconds. */
	private int maxContentLength = 1024 * 1024;

	/** Enable ssl secure channel. */
	private boolean enableSslSecure = false;

	/** Enable channel transport trace. */
	private boolean enableChannelLog = false;

	/** Enable channel transport trace. */
	private String channelLogLevel = LogLevel.DEBUG.name();

	public String getInetHost() {
		return inetHost;
	}

	public void setInetHost(String inetHost) {
		this.inetHost = inetHost;
	}

	public int getInetPort() {
		return inetPort;
	}

	public void setInetPort(int inetPort) {
		this.inetPort = inetPort;
	}

	public int getBacklog() {
		return backlog;
	}

	public void setBacklog(int backlog) {
		this.backlog = backlog;
	}

	public int getReadTimeoutSec() {
		return readTimeoutSec;
	}

	public void setReadTimeoutSec(int readIdleSeconds) {
		this.readTimeoutSec = readIdleSeconds;
	}

	public int getWriteTimeoutSec() {
		return writeTimeoutSec;
	}

	public void setWriteTimeoutSec(int writeIdleSeconds) {
		this.writeTimeoutSec = writeIdleSeconds;
	}

	public int getAllTimeoutSec() {
		return allTimeoutSec;
	}

	public void setAllTimeoutSec(int allIdleSeconds) {
		this.allTimeoutSec = allIdleSeconds;
	}

	public int getMaxContentLength() {
		return maxContentLength;
	}

	public void setMaxContentLength(int maxContentLength) {
		this.maxContentLength = maxContentLength;
	}

	public boolean isEnableSslSecure() {
		return enableSslSecure;
	}

	public void setEnableSslSecure(boolean enableSslSecure) {
		this.enableSslSecure = enableSslSecure;
	}

	public boolean isEnableChannelLog() {
		return enableChannelLog;
	}

	public void setEnableChannelLog(boolean enableChannelLog) {
		this.enableChannelLog = enableChannelLog;
	}

	public String getChannelLogLevel() {
		return channelLogLevel;
	}

	public void setChannelLogLevel(String channelLogLevel) {
		this.channelLogLevel = channelLogLevel;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName().concat(" - ").concat(toJSONString(this));
	}

}
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
package com.wl4g.devops.common.bean.erm;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wl4g.devops.common.bean.erm.log.Beat;
import com.wl4g.devops.common.bean.erm.log.LogHost;
import com.wl4g.devops.common.bean.erm.log.Input;
import com.wl4g.devops.common.bean.erm.log.Prospector;

public class Log {

	@JsonProperty(value = "@timestamp")
	private String timestamp;

	private int offset;

	private String message;

	private String source;

	private Beat beat;

	private LogHost host;

	private Prospector prospector;

	private Input input;

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Beat getBeat() {
		return beat;
	}

	public void setBeat(Beat beat) {
		this.beat = beat;
	}

	public LogHost getHost() {
		return host;
	}

	public void setHost(LogHost host) {
		this.host = host;
	}

	public Prospector getProspector() {
		return prospector;
	}

	public void setProspector(Prospector prospector) {
		this.prospector = prospector;
	}

	public Input getInput() {
		return input;
	}

	public void setInput(Input input) {
		this.input = input;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "Log{" + "timestamp=" + timestamp + ", offset=" + offset + ", message='" + message + '\'' + ", source='" + source
				+ '\'' + ", beat=" + beat.toString() + ", host=" + host + ", prospector=" + prospector + ", input=" + input + '}';
	}
}
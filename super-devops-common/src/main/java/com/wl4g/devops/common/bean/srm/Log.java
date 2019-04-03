package com.wl4g.devops.common.bean.srm;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wl4g.devops.common.bean.srm.log.Beat;
import com.wl4g.devops.common.bean.srm.log.Host;
import com.wl4g.devops.common.bean.srm.log.Input;
import com.wl4g.devops.common.bean.srm.log.Prospector;

public class Log {

	@JsonProperty(value = "@timestamp")
	private String timestamp;

	private int offset;

	private String message;

	private String source;

	private Beat beat;

	private Host host;

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

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
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

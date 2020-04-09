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
package com.wl4g.devops.iam.common.web.model;

import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wl4g.devops.iam.common.session.IamSession;

/**
 * {@link IamSession} to information model.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年4月9日
 * @since
 */
public class SessionInfo implements Serializable {

	private static final long serialVersionUID = -4118097602451640788L;

	/**
	 * Session keyname.
	 */
	@NotBlank
	@JsonProperty("sk")
	private String sessionKey;

	/**
	 * Session value.
	 */
	@NotBlank
	@JsonProperty("sv")
	private Serializable sessionValue;

	public SessionInfo() {
		super();
	}

	public SessionInfo(@NotBlank String sessionKey, @NotBlank Serializable sessionValue) {
		setSessionKey(sessionKey);
		setSessionValue(sessionValue);
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public SessionInfo setSessionKey(String sessionKey) {
		notNullOf(sessionKey, "sessionKey");
		this.sessionKey = sessionKey;
		return this;
	}

	public Serializable getSessionValue() {
		return sessionValue;
	}

	public SessionInfo setSessionValue(Serializable sessionValue) {
		notNullOf(sessionValue, "sessionValue");
		this.sessionValue = sessionValue;
		return this;
	}

}

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
package com.wl4g.devops.iam.web.model;

import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wl4g.devops.iam.common.web.model.SessionInfo;

/**
 * Pre-processing handshake result model. {@link HandshakeResult}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年3月10日 v1.0.0
 * @see
 */
public class HandshakeResult implements Serializable {
	private static final long serialVersionUID = 2636163327046053795L;

	/**
	 * Iam server version.
	 */
	@NotBlank
	@JsonProperty("v")
	private String version;

	/**
	 * Applied secretKey algorithms.
	 */
	@NotEmpty
	@JsonProperty("algs")
	private List<String> algorithms = new ArrayList<>();

	/**
	 * Session info.
	 */
	@NotNull
	@JsonProperty(KEY_SESSIONINFO_NAME)
	private SessionInfo session = new SessionInfo();

	public HandshakeResult() {
		super();
	}

	public HandshakeResult(String version) {
		// hasTextOf(version, "version");
		setVersion(version);
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<String> getAlgorithms() {
		return algorithms;
	}

	public void setAlgorithms(List<String> algorithms) {
		if (!CollectionUtils.isEmpty(algorithms)) {
			for (String alg : algorithms) {
				if (!this.algorithms.contains(alg)) {
					this.algorithms.add(alg);
				}
			}
		}
	}

	public SessionInfo getSession() {
		return session;
	}

	public HandshakeResult setSessionInfo(SessionInfo session) {
		notNullOf(session, "session");
		this.session = session;
		return this;
	}

}
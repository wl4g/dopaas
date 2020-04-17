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
package com.wl4g.devops.vcs.operator.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;

/**
 * Composite VCS basic project model.</br>
 * Project information stored by corresponding VCs server (simple core
 * information)
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @version v1.0.0 2019-11-14
 * @since
 */
public class CompositeBasicVcsProjectModel implements Serializable {
	private static final long serialVersionUID = 3384209918335868080L;

	/** VCS remote project ID. */
	@NotNull
	private Integer id;

	/** VCS remote project name. */
	@NotBlank
	private String name;

	/** VCS remote project clone URL(HTTP). */
	@NotBlank
	private String httpUrl;

	/** VCS remote project clone URL(SSH). */
	@NotBlank
	private String sshUrl;

	public CompositeBasicVcsProjectModel() {
		super();
	}

	public CompositeBasicVcsProjectModel(Integer id, String name, String httpUrl, String sshUrl) {
		super();
		setId(id);
		setName(name);
		setHttpUrl(httpUrl);
		setSshUrl(sshUrl);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		notNull(id, "projectId can't is null.");
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		hasText(name, "name can't is empty.");
		this.name = name;
	}

	public String getHttpUrl() {
		return httpUrl;
	}

	public void setHttpUrl(String httpUrl) {
		hasText(httpUrl, "httpUrl can't is empty.");
		this.httpUrl = httpUrl;
	}

	public String getSshUrl() {
		return sshUrl;
	}

	public void setSshUrl(String sshUrl) {
		hasText(sshUrl, "sshUrl can't is empty.");
		this.sshUrl = sshUrl;
	}

	@Override
	public String toString() {
		return "CompositeSimpleVcsProjectModel [id=" + id + ", name=" + name + ", httpUrl=" + httpUrl + ", sshUrl=" + sshUrl
				+ "]";
	}

}
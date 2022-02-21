/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.urm.operator.github;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wl4g.dopaas.common.bean.urm.model.CompositeBasicVcsProjectModel;
import com.wl4g.dopaas.urm.operator.model.VcsProjectModel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Gitlab API-v4 for projects simple model.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月13日
 * @since
 */
@Getter
@Setter
@ToString
public class GithubV3SimpleProjectModel implements VcsProjectModel, Serializable {
	private static final long serialVersionUID = 3384209918335868080L;

	private Long id;
	private String name;
	@JsonProperty("full_name")
	private String fullName;
	@JsonProperty("html_url")
	private String htmlUrl;
	private String description;
	private String url;
	@JsonProperty("ssh_url")
	private String sshUrl;
	@JsonProperty("clone_url")
	private String cloneUrl;

	@Override
	public CompositeBasicVcsProjectModel toCompositeVcsProject() {
		return new CompositeBasicVcsProjectModel(getId(), getName(), getCloneUrl(), getSshUrl(), getFullName());
	}

}
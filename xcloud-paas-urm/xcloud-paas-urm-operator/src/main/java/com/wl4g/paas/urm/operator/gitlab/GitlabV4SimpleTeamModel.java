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
package com.wl4g.paas.urm.operator.gitlab;

import com.wl4g.paas.urm.operator.model.VcsGroupModel;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Gitlab API-v4 for projects simple model.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月13日
 * @since
 */
@Getter
@Setter
public class GitlabV4SimpleTeamModel implements VcsGroupModel, Serializable {
	private static final long serialVersionUID = 3384209918335868080L;

	private Integer id;
	private String web_url;
	private String name;
	private String path;
	private String description;
	private String visibility;
	private Boolean lfs_enabled;
	private String avatar_url;
	private Boolean request_access_enabled;
	private String full_name;
	private String full_path;
	private Integer parent_id;

	// Other
	List<GitlabV4SimpleTeamModel> children;

	public GitlabV4SimpleTeamModel() {
	}

}
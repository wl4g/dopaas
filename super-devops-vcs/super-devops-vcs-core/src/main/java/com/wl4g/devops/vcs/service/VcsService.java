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
package com.wl4g.devops.vcs.service;

import com.wl4g.devops.common.bean.ci.Vcs;
import com.wl4g.devops.page.PageModel;
import com.wl4g.devops.vcs.operator.model.CompositeBasicVcsProjectModel;
import com.wl4g.devops.vcs.operator.model.VcsGroupModel;
import com.wl4g.devops.vcs.operator.model.VcsProjectModel;

import java.util.List;

/**
 * @author vjay
 * @date 2019-11-12 11:05:00
 */
public interface VcsService {

	PageModel list(PageModel pm, String name, String provider, Integer authType);

	void save(Vcs vcs);

	void del(Integer id);

	Vcs detail(Integer id);

	List<Vcs> all();

	List<CompositeBasicVcsProjectModel> getProjectsToCompositeBasic(Integer vcsId, String projectName);

	List<VcsGroupModel> getGroups(Integer id, String groupName);

	List<VcsProjectModel> getProjects(PageModel pm, Integer vcsId, Integer groupId, String projectName);

}
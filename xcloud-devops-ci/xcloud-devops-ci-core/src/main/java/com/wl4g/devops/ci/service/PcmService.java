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
package com.wl4g.devops.ci.service;

import com.wl4g.components.core.bean.ci.Pcm;
import com.wl4g.components.core.bean.ci.PipeHistoryPcm;
import com.wl4g.components.core.web.model.SelectionModel;
import com.wl4g.devops.page.PageModel;

import java.util.List;

/**
 * @author vjay
 * @date 2019-11-12 11:05:00
 */
public interface PcmService {

	PageModel list(PageModel pm, String name, String providerKind, Integer authType);

	void save(Pcm pcm);

	void del(Integer id);

	Pcm detail(Integer id);

	List<Pcm> all();

	List<SelectionModel> getUsers(Integer pcmId);

	List<SelectionModel> getProjects(Integer pcmId);

	List<SelectionModel> getIssues(Integer pcmId, String userId, String projectId, String search);

	List<SelectionModel> getProjectsByPcmId(Integer pcmId);

	List<SelectionModel> getTrackers(Integer pcmId);

	List<SelectionModel> getStatuses(Integer pcmId);

	List<SelectionModel> getPriorities(Integer pcmId);

	void createIssues(Integer pcmId, PipeHistoryPcm pipeHistoryPcm);

}
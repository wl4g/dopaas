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
package com.wl4g.devops.ci.service;

import com.wl4g.components.core.bean.model.PageModel;
import com.wl4g.components.core.bean.model.SelectionModel;
import com.wl4g.devops.common.bean.ci.Pcm;
import com.wl4g.devops.common.bean.ci.PipeHistoryPcm;

import java.util.List;

/**
 * @author vjay
 * @date 2019-11-12 11:05:00
 */
public interface PcmService {

	PageModel<Pcm> list(PageModel<Pcm> pm, String name, String providerKind, Integer authType);

	void save(Pcm pcm);

	void del(Long id);

	Pcm detail(Long id);

	List<Pcm> all();

	List<SelectionModel> getUsers(Long pcmId);

	List<SelectionModel> getProjects(Long pcmId);

	List<SelectionModel> getIssues(Long pcmId, String userId, String projectId, String search);

	List<SelectionModel> getProjectsByPcmId(Long pcmId);

	List<SelectionModel> getTrackers(Long pcmId);

	List<SelectionModel> getStatuses(Long pcmId);

	List<SelectionModel> getPriorities(Long pcmId);

	void createIssues(Long pcmId, PipeHistoryPcm pipeHistoryPcm);

}
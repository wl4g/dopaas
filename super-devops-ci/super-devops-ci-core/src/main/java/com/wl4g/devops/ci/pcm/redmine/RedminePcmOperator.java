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
package com.wl4g.devops.ci.pcm.redmine;

import com.wl4g.devops.ci.pcm.AbstractPcmOperator;
import com.wl4g.devops.ci.pcm.redmine.model.RedmineIssues;
import com.wl4g.devops.ci.pcm.redmine.model.RedmineIssues.RedmineIssue;
import com.wl4g.devops.ci.pcm.redmine.model.RedmineProjects;
import com.wl4g.devops.ci.pcm.redmine.model.RedmineUsers;
import com.wl4g.devops.common.bean.ci.Pcm;
import com.wl4g.devops.common.web.model.SelectionModel;
import com.wl4g.devops.dao.ci.PcmDao;
import com.wl4g.devops.dao.ci.TaskDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * PCM API operator of redmine.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @version 2020年1月7日 v1.0.0
 * @see
 */
public class RedminePcmOperator extends AbstractPcmOperator {

	@Autowired
	private TaskDao taskDao;

	private PcmDao pcmDao;

	@Override
	public PcmKind kind() {
		return PcmKind.Redmine;
	}

	@Override
	public List<SelectionModel> getProjects(Pcm pcm) {
		check(pcm);
		String url = pcm.getBaseUrl() + "/projects.json?key=" + pcm.getAccessToken();
		RedmineProjects projects = restTemplate.getForObject(url, RedmineProjects.class);
		List<SelectionModel> result = new ArrayList<>();
		for (RedmineProjects.Project project : projects.getProjects()) {
			SelectionModel selectInfo = new SelectionModel();
			selectInfo.setValue(String.valueOf(project.getId()));
			selectInfo.setLabel(project.getName());
			result.add(selectInfo);
		}
		return result;
	}

	@Override
	public List<SelectionModel> getUsers(Pcm pcm) {
		check(pcm);
		String url = pcm.getBaseUrl() + "/users.json?key=" + pcm.getAccessToken();
		RedmineUsers users = restTemplate.getForObject(url, RedmineUsers.class);
		List<SelectionModel> result = new ArrayList<>();
		for (RedmineUsers.User user : users.getUsers()) {
			SelectionModel selectInfo = new SelectionModel();
			selectInfo.setValue(String.valueOf(user.getId()));
			selectInfo.setLabel(user.getLastname() + user.getFirstname() + " / " + user.getLogin());
			result.add(selectInfo);
		}
		return result;
	}

	@Override
	public List<SelectionModel> getIssues(Pcm pcm, String userId, String projectId, String searchSubject) {
		check(pcm);
		String url = pcm.getBaseUrl() + "/issues.json?limit=100&key=" + pcm.getAccessToken();
		if (StringUtils.isNotBlank(userId)) {
			url += "&assigned_to_id=" + userId;
		}
		if (StringUtils.isNotBlank(projectId)) {
			url += "&project_id=" + projectId;
		}
		if (StringUtils.isNotBlank(searchSubject)) {
			url += "&subject=" + searchSubject;
		}
		RedmineIssues issues = restTemplate.getForObject(url, RedmineIssues.class);
		List<SelectionModel> result = new ArrayList<>();
		for (RedmineIssue issue : issues.getIssues()) {
			SelectionModel selectInfo = new SelectionModel();
			selectInfo.setValue(String.valueOf(issue.getId()));
			selectInfo.setLabel("#" + issue.getId() + ": " + issue.getSubject());
			result.add(selectInfo);
		}
		return result;
	}


	private void check(Pcm pcm){
		Assert.notNull(pcm,"pcm is null");
		Assert.hasText(pcm.getAccessToken(),"access token is null");
		Assert.hasText(pcm.getBaseUrl(),"base url is null");

	}


}
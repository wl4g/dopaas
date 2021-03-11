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
package com.wl4g.devops.ci.pcm.redmine;

import com.wl4g.component.common.serialize.JacksonUtils;
import com.wl4g.component.core.bean.model.SelectionModel;
import com.wl4g.devops.ci.pcm.AbstractPcmOperator;
import com.wl4g.devops.ci.pcm.redmine.model.*;
import com.wl4g.devops.ci.pcm.redmine.model.RedmineIssues.RedmineIssue;
import com.wl4g.devops.common.bean.ci.Pcm;
import com.wl4g.devops.common.bean.ci.PipeHistoryPcm;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.Objects.nonNull;

/**
 * PCM API operator of redmine.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @version 2020年1月7日 v1.0.0
 * @see
 */
public class RedminePcmOperator extends AbstractPcmOperator {

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
	public List<SelectionModel> getTracker(Pcm pcm) {
		check(pcm);
		String url = pcm.getBaseUrl() + "/trackers.json";
		RedmineTrackers trackers = restTemplate.getForObject(url, RedmineTrackers.class);
		List<SelectionModel> result = new ArrayList<>();
		for (RedmineTrackers.RedmineTracker tracker : trackers.getTrackers()) {
			SelectionModel selectInfo = new SelectionModel();
			selectInfo.setValue(String.valueOf(tracker.getId()));
			selectInfo.setLabel(tracker.getName());
			result.add(selectInfo);
		}
		return result;
	}

	@Override
	public List<SelectionModel> getStatuses(Pcm pcm) {
		check(pcm);
		String url = pcm.getBaseUrl() + "/issue_statuses.json";
		RedmineIssueStatuses trackers = restTemplate.getForObject(url, RedmineIssueStatuses.class);
		List<SelectionModel> result = new ArrayList<>();
		for (RedmineIssueStatuses.IssueStatus tracker : trackers.getIssueStatuses()) {
			SelectionModel selectInfo = new SelectionModel();
			selectInfo.setValue(String.valueOf(tracker.getId()));
			selectInfo.setLabel(tracker.getName());
			result.add(selectInfo);
		}
		return result;
	}

	@Override
	public List<SelectionModel> getPriorities(Pcm pcm) {
		check(pcm);
		String url = pcm.getBaseUrl() + "/enumerations/issue_priorities.json";
		RedmineIssuePriorities issuePriorities = restTemplate.getForObject(url, RedmineIssuePriorities.class);
		List<SelectionModel> result = new ArrayList<>();
		for (RedmineIssuePriorities.IssuesPriorities issuesPriority : issuePriorities.getIssuePriorities()) {
			SelectionModel selectInfo = new SelectionModel();
			selectInfo.setValue(String.valueOf(issuesPriority.getId()));
			selectInfo.setLabel(issuesPriority.getName());
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

	@Override
	public void createIssues(Pcm pcm, PipeHistoryPcm pipeHistoryPcm) {
		check(pcm);
		String url = pcm.getBaseUrl() + "/issues.json?key=" + pcm.getAccessToken();

		HttpHeaders httpHeaders = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json;charset=UTF-8");
		httpHeaders.setContentType(type);
		// MultiValueMap<String, Object> map=new LinkedMultiValueMap<>();
		HashMap<String, Object> map = new HashMap<>();

		// Necessary parameters
		map.put("project_id", pipeHistoryPcm.getXProjectId());
		map.put("subject", pipeHistoryPcm.getXSubject());
		map.put("assigned_to_id", pipeHistoryPcm.getXAssignTo());

		// Not Necessary parameters
		if (nonNull(pipeHistoryPcm.getXTracker())) {
			map.put("tracker_id", pipeHistoryPcm.getXTracker());
		}
		if (nonNull(pipeHistoryPcm.getXStatus())) {
			map.put("status_id", pipeHistoryPcm.getXStatus());
		}
		if (nonNull(pipeHistoryPcm.getXPriority())) {
			map.put("priority_id", pipeHistoryPcm.getXPriority());
		}
		if (nonNull(pipeHistoryPcm.getXDescription())) {
			map.put("description", pipeHistoryPcm.getXDescription());
		}
		if (nonNull(pipeHistoryPcm.getXStartDate())) {
			map.put("start_date", pipeHistoryPcm.getXStartDate());
		}
		if (nonNull(pipeHistoryPcm.getXExpectedTime())) {
			map.put("estimated_hours", pipeHistoryPcm.getXExpectedTime());
		}

		HashMap<String, Object> map2 = new HashMap<>();
		map2.put("issue", map);

		HttpEntity<String> objectHttpEntity = new HttpEntity<>(JacksonUtils.toJSONString(map2), httpHeaders);

		ResponseEntity<String> responseResultResponseEntity = restTemplate.postForEntity(url, objectHttpEntity, String.class);
		String result = responseResultResponseEntity.getBody();
		log.info(result);
	}

	private void check(Pcm pcm) {
		Assert.notNull(pcm, "pcm is null");
		Assert.hasText(pcm.getAccessToken(), "access token is null");
		Assert.hasText(pcm.getBaseUrl(), "base url is null");

	}

}
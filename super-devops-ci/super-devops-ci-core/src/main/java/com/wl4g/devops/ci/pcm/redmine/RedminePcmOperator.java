package com.wl4g.devops.ci.pcm.redmine;

import com.wl4g.devops.ci.pcm.AbstractPcmOperator;
import com.wl4g.devops.ci.pcm.redmine.model.RedmineIssues;
import com.wl4g.devops.ci.pcm.redmine.model.RedmineIssues.RedmineIssue;
import com.wl4g.devops.ci.pcm.redmine.model.RedmineProjects;
import com.wl4g.devops.ci.pcm.redmine.model.RedmineUsers;
import com.wl4g.devops.common.web.model.SelectionModel;

import org.apache.commons.lang3.StringUtils;

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

	// TODO config in file
	final private static String baseUrl = "http://redmine.anjiancloud.repo/redmine";
	final private static String key = "049c9528c9a11903e6b7dbdc046bc1ccac82d1aa";

	@Override
	public PcmKind kind() {
		return PcmKind.Redmine;
	}

	@Override
	public List<SelectionModel> getProjects(Integer trackId) {
		String url = baseUrl + "/projects.json?key=" + key;
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
	public List<SelectionModel> getUsers(Integer trackId) {
		String url = baseUrl + "/users.json?key=" + key;
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
	public List<SelectionModel> getIssues(Integer trackId, String userId, String projectId, String searchSubject) {
		String url = baseUrl + "/issues.json?limit=100&key=" + key;
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

}

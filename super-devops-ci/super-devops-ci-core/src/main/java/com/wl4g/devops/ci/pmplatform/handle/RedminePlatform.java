package com.wl4g.devops.ci.pmplatform.handle;

import com.wl4g.devops.ci.pmplatform.anno.PmPlatform;
import com.wl4g.devops.ci.pmplatform.constant.PlatformEnum;
import com.wl4g.devops.ci.pmplatform.model.dto.SelectInfo;
import com.wl4g.devops.ci.pmplatform.model.redmine.Issues;
import com.wl4g.devops.ci.pmplatform.model.redmine.Projects;
import com.wl4g.devops.ci.pmplatform.model.redmine.Users;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vjay
 * @date 2020-01-03 15:03:00
 */
@PmPlatform(PlatformEnum.Redmine)
@Component
public class RedminePlatform extends BasePlatform implements PmPlatformInterface {

	// TODO config in file
	private String baseUrl = "http://redmine.anjiancloud.repo/redmine";
	private String key = "049c9528c9a11903e6b7dbdc046bc1ccac82d1aa";

	@Override
	public List<SelectInfo> getProjects() {
		RestTemplate restTemplate = new RestTemplate(factory);
		String url = baseUrl + "/projects.json?key=" + key;
		Projects projects = restTemplate.getForObject(url, Projects.class);
		List<SelectInfo> result = new ArrayList<>();
		for (Projects.Project project : projects.getProjects()) {
			SelectInfo selectInfo = new SelectInfo();
			selectInfo.setValue(String.valueOf(project.getId()));
			selectInfo.setLabel(project.getName());
			result.add(selectInfo);
		}
		return result;
	}

	@Override
	public List<SelectInfo> getUsers() {
		RestTemplate restTemplate = new RestTemplate(factory);
		String url = baseUrl + "/users.json?key=" + key;
		Users users = restTemplate.getForObject(url, Users.class);
		List<SelectInfo> result = new ArrayList<>();
		for (Users.User user : users.getUsers()) {
			SelectInfo selectInfo = new SelectInfo();
			selectInfo.setValue(String.valueOf(user.getId()));
			selectInfo.setLabel(user.getLastname() + user.getFirstname() + " / " + user.getLogin());
			result.add(selectInfo);
		}
		return result;
	}

	@Override
	public List<SelectInfo> getIssues(String userId, String projectId, String search) {
		RestTemplate restTemplate = new RestTemplate(factory);
		String url = baseUrl + "/issues.json?limit=100&key=" + key;
		if (StringUtils.isNotBlank(userId)) {
			url += "&assigned_to_id=" + userId;
		}
		if (StringUtils.isNotBlank(projectId)) {
			url += "&project_id=" + projectId;
		}
		if (StringUtils.isNotBlank(search)) {
			url += "&subject=" + search;
		}
		Issues issues = restTemplate.getForObject(url, Issues.class);
		List<SelectInfo> result = new ArrayList<>();
		for (Issues.Issue issue : issues.getIssues()) {
			SelectInfo selectInfo = new SelectInfo();
			selectInfo.setValue(String.valueOf(issue.getId()));
			selectInfo.setLabel("#" + issue.getId() + ": " + issue.getSubject());
			result.add(selectInfo);
		}
		return result;
	}

}

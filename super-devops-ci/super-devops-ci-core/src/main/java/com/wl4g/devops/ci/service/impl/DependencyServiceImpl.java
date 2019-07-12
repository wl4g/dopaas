/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.ci.service.impl;

import com.wl4g.devops.ci.config.DeployProperties;
import com.wl4g.devops.ci.service.DependencyService;
import com.wl4g.devops.ci.utils.GitUtils;
import com.wl4g.devops.ci.utils.SSHTool;
import com.wl4g.devops.common.bean.ci.Dependency;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.dao.ci.DependencyDao;
import com.wl4g.devops.dao.ci.ProjectDao;
import com.wl4g.devops.shell.utils.ShellContextHolder;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * Dependency service implements
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-22 11:39:00
 */
@Service
public class DependencyServiceImpl implements DependencyService {

	@Autowired
	private DeployProperties config;

	@Autowired
	private DependencyDao dependencyDao;

	@Autowired
	private ProjectDao projectDao;

	@Override
	public void build(Dependency dependency, String branch) throws Exception {
		Integer projectId = dependency.getProjectId();

		List<Dependency> dependencies = dependencyDao.getParentsByProjectId(projectId);
		if (dependencies != null && dependencies.size() > 0) {
			for (Dependency dep : dependencies) {
				String br = dep.getParentBranch();
				build(new Dependency(dep.getParentId()), StringUtils.isBlank(br) ? branch : br);
			}
		}

		// build
		Project project = projectDao.selectByPrimaryKey(projectId);
		String path = config.getGitBasePath() + "/" + project.getProjectName();
		if (checkGitPahtExist(path)) {
			GitUtils.checkout(config.getCredentials(), path, branch);
		} else {
			GitUtils.clone(config.getCredentials(), project.getGitUrl(), path, branch);
		}

		// Install
		mvnInstall(path);
	}

	private boolean checkGitPahtExist(String path) throws Exception {
		File file = new File(path + "/.git");
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Building (maven)
	 */
	private String mvnInstall(String path) throws Exception {
		// Execution mvn
		String command = "mvn -f " + path + "/pom.xml clean install -Dmaven.test.skip=true";
		return SSHTool.exec(command, inlog -> !ShellContextHolder.isInterruptIfNecessary());
	}

}
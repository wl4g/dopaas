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
package com.wl4g.devops.ci.service.impl;

import com.github.pagehelper.PageHelper;
import com.wl4g.components.common.lang.Assert2;
import com.wl4g.components.core.bean.BaseBean;
import com.wl4g.components.core.bean.ci.*;
import com.wl4g.devops.ci.service.DependencyService;
import com.wl4g.devops.ci.service.PipelineService;
import com.wl4g.devops.ci.service.ProjectService;
import com.wl4g.devops.dao.ci.*;
import com.wl4g.devops.page.PageModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

import static com.wl4g.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCode;
import static com.wl4g.iam.common.utils.IamOrganizationHolder.getRequestOrganizationCodes;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author vjay
 * @date 2020-04-27 15:08:00
 */
@Service
public class PipelineServiceImpl implements PipelineService {

	@Autowired
	private PipelineDao pipelineDao;

	@Autowired
	private PipelineInstanceDao pipelineInstanceDao;

	@Autowired
	private PipeStepBuildingDao pipeStepBuildingDao;

	@Autowired
	private PipeStepBuildingProjectDao pipeStepBuildingProjectDao;

	@Autowired
	private PipeStepPcmDao pipeStepPcmDao;

	@Autowired
	private PipeStepNotificationDao pipeStepNotificationDao;

	@Autowired
	private ProjectDao projectDao;

	@Autowired
	private DependencyService dependencyService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private PipeStepInstanceCommandDao pipeStepInstanceCommandDao;

	@Autowired
	private PipeStepDeployDao pipeStepDeployDao;

	@Override
	public PageModel list(PageModel pm, String pipeName, String providerKind, String environment) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(pipelineDao.list(getRequestOrganizationCodes(), null, pipeName, providerKind, environment,null));
		return pm;
	}

	@Override
	public void save(Pipeline pipeline) {
		if (nonNull(pipeline.getId())) {
			update(pipeline);
		} else {
			insert(pipeline);
		}
	}

	@Override
	public Pipeline detail(Integer id) {
		Assert2.notNullOf(id, "id");
		// Pipeline
		Pipeline pipeline = pipelineDao.selectByPrimaryKey(id);
		// Pipeline Deploy
		PipeStepDeploy pipeStepDeploy = pipeStepDeployDao.selectByPipeId(id);
		pipeline.setPipeStepDeploy(pipeStepDeploy);
		// Pipeline Instance
		List<PipelineInstance> pipelineInstances = pipelineInstanceDao.selectByPipeId(id);
		Integer[] instanceIds = new Integer[pipelineInstances.size()];
		for (int i = 0; i < pipelineInstances.size(); i++) {
			instanceIds[i] = pipelineInstances.get(i).getInstanceId();
		}
		pipeline.setInstanceIds(instanceIds);

		// Pipeline Building
		PipeStepBuilding pipeStepBuilding = pipeStepBuildingDao.selectByPipeId(id);
		pipeline.setPipeStepBuilding(pipeStepBuilding);

		// Pipeline Pcm
		PipeStepPcm pipeStepPcm = pipeStepPcmDao.selectByPipeId(id);
		pipeline.setPipeStepPcm(pipeStepPcm);

		// Pipeline Notification
		PipeStepNotification pipeStepNotification = pipeStepNotificationDao.selectByPipeId(id);
		if (Objects.nonNull(pipeStepNotification)) {
			pipeStepNotification.setContactGroupId2(pipeStepNotification.getContactGroupIds().split(","));
			pipeline.setPipeStepNotification(pipeStepNotification);
		}

		// Pipeline Instance Command
		PipeStepInstanceCommand pipeStepInstanceCommand = pipeStepInstanceCommandDao.selectByPipeId(id);
		pipeline.setPipeStepInstanceCommand(pipeStepInstanceCommand);

		// TODO ...... testing,analysis,docker,k8s

		return pipeline;
	}

	@Override
	public void del(Integer id) {
		Pipeline pipeline = new Pipeline();
		pipeline.setId(id);
		pipeline.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		pipelineDao.updateByPrimaryKeySelective(pipeline);
	}

	@Override
	public List<Pipeline> getByClusterId(Integer clusterId) {
		return pipelineDao.selectByClusterId(clusterId);
	}

	public void insert(Pipeline pipeline) {
		Assert2.notNullOf(pipeline, "pipeline");
		// Insert Pipeline
		pipeline.preInsert(getRequestOrganizationCode());
		pipelineDao.insertSelective(pipeline);
		// Insert PipeInstance
		Integer[] instanceIds = pipeline.getInstanceIds();

		PipeStepDeploy pipeStepDeploy = pipeline.getPipeStepDeploy();
		if (isNull(pipeStepDeploy)) {
			pipeStepDeploy = new PipeStepDeploy();
		}
		pipeStepDeploy.preInsert();
		pipeStepDeploy.setPipeId(pipeline.getId());
		pipeStepDeployDao.insertSelective(pipeStepDeploy);

		if (nonNull(instanceIds) && instanceIds.length > 0) {
			List<PipelineInstance> pipelineInstances = new ArrayList<>();
			for (Integer i : instanceIds) {
				PipelineInstance pipelineInstance = new PipelineInstance();
				pipelineInstance.preInsert();
				pipelineInstance.setDeployId(pipeStepDeploy.getId());
				pipelineInstance.setInstanceId(i);
				pipelineInstances.add(pipelineInstance);
			}
			if (!CollectionUtils.isEmpty(pipelineInstances)) {
				pipelineInstanceDao.insertBatch(pipelineInstances);
			}

		}
		// Insert PipeStepBuilding
		PipeStepBuilding pipeStepBuilding = pipeline.getPipeStepBuilding();
		if (nonNull(pipeStepBuilding)) {
			pipeStepBuilding.preInsert();
			pipeStepBuilding.setPipeId(pipeline.getId());
			pipeStepBuildingDao.insertSelective(pipeStepBuilding);
			// Insert PipeStepBuildingProject
			List<PipeStepBuildingProject> pipeStepBuildingProjects = pipeline.getPipeStepBuilding().getPipeStepBuildingProjects();
			if (!CollectionUtils.isEmpty(pipeStepBuildingProjects)) {
				for (int i = 0; i < pipeStepBuildingProjects.size(); i++) {
					pipeStepBuildingProjects.get(i).preInsert();
					pipeStepBuildingProjects.get(i).setSort(i + 1);
					pipeStepBuildingProjects.get(i).setBuildingId(pipeStepBuilding.getId());
				}
				pipeStepBuildingProjectDao.insertBatch(pipeStepBuildingProjects);
			}
		}

		// Insert Pipeline Instance Command
		PipeStepInstanceCommand pipeStepInstanceCommand = pipeline.getPipeStepInstanceCommand();
		if (nonNull(pipeStepInstanceCommand)) {
			pipeStepInstanceCommand.preInsert();
			pipeStepInstanceCommand.setPipeId(pipeline.getId());
			pipeStepInstanceCommandDao.insertSelective(pipeStepInstanceCommand);
		}

		// TODO ...... testing,analysis,docker,k8s

		// Insert Pcm
		PipeStepPcm pipeStepPcm = pipeline.getPipeStepPcm();
		if (nonNull(pipeStepPcm)) {
			pipeStepPcm.preInsert();
			pipeStepPcm.setPipeId(pipeline.getId());
			pipeStepPcmDao.insertSelective(pipeStepPcm);
		}
		// Insert Notification
		PipeStepNotification pipeStepNotification = pipeline.getPipeStepNotification();
		if (nonNull(pipeStepNotification)) {
			pipeStepNotification.preInsert();
			pipeStepNotification.setPipeId(pipeline.getId());
			pipeStepNotification.setContactGroupIds(StringUtils.join(pipeStepNotification.getContactGroupId(), ","));
			pipeStepNotificationDao.insertSelective(pipeStepNotification);
		}

	}

	public void update(Pipeline pipeline) {
		pipeline.preUpdate();
		Assert2.notNullOf(pipeline, "pipeline");
		// Update Pipeline
		pipeline.preUpdate();
		pipelineDao.updateByPrimaryKeySelective(pipeline);

		PipeStepDeploy pipeStepDeploy = pipeline.getPipeStepDeploy();
		if (nonNull(pipeStepDeploy)) {
			pipeStepDeployDao.updateByPrimaryKeySelective(pipeStepDeploy);
		} else {
			pipeStepDeploy = new PipeStepDeploy();
			pipeStepDeploy.preInsert();
			pipeStepDeploy.setPipeId(pipeline.getId());
			pipeStepDeployDao.insertSelective(pipeStepDeploy);
		}

		// Update PipeInstance
		Integer[] instanceIds = pipeline.getInstanceIds();
		pipelineInstanceDao.deleteByPipeId(pipeline.getId());
		if (nonNull(instanceIds)) {
			List<PipelineInstance> pipelineInstances = new ArrayList<>();
			for (Integer i : instanceIds) {
				PipelineInstance pipelineInstance = new PipelineInstance();
				pipelineInstance.preInsert();
				pipelineInstance.setDeployId(pipeStepDeploy.getId());
				pipelineInstance.setInstanceId(i);
				pipelineInstances.add(pipelineInstance);
			}
			if (!CollectionUtils.isEmpty(pipelineInstances)) {
				pipelineInstanceDao.insertBatch(pipelineInstances);
			}
		}
		// Update PipeStepBuilding
		pipeStepBuildingProjectDao.deleteByPipeId(pipeline.getId());
		pipeStepBuildingDao.deleteByPipeId(pipeline.getId());
		PipeStepBuilding pipeStepBuilding = pipeline.getPipeStepBuilding();
		if (nonNull(pipeStepBuilding)) {
			pipeStepBuilding.preInsert();
			pipeStepBuilding.setPipeId(pipeline.getId());
			pipeStepBuildingDao.insertSelective(pipeStepBuilding);
			// Update PipeStepBuildingProject
			List<PipeStepBuildingProject> pipeStepBuildingProjects = pipeline.getPipeStepBuilding().getPipeStepBuildingProjects();
			if (!CollectionUtils.isEmpty(pipeStepBuildingProjects)) {
				for (int i = 0; i < pipeStepBuildingProjects.size(); i++) {
					pipeStepBuildingProjects.get(i).preInsert();
					pipeStepBuildingProjects.get(i).setSort(i + 1);
					pipeStepBuildingProjects.get(i).setBuildingId(pipeStepBuilding.getId());
				}
				pipeStepBuildingProjectDao.insertBatch(pipeStepBuildingProjects);
			}
		}

		// Update Pipeline Instance Command
		pipeStepInstanceCommandDao.deleteByPipeId(pipeline.getId());
		PipeStepInstanceCommand pipeStepInstanceCommand = pipeline.getPipeStepInstanceCommand();
		if (nonNull(pipeStepInstanceCommand)) {
			pipeStepInstanceCommand.preInsert();
			pipeStepInstanceCommand.setPipeId(pipeline.getId());
			pipeStepInstanceCommandDao.insertSelective(pipeStepInstanceCommand);
		}

		// TODO ...... testing,analysis,docker,k8s

		// Update Pcm
		pipeStepPcmDao.deleteByPipeId(pipeline.getId());
		PipeStepPcm pipeStepPcm = pipeline.getPipeStepPcm();
		if (nonNull(pipeStepPcm)) {
			pipeStepPcm.preInsert();
			pipeStepPcm.setPipeId(pipeline.getId());
			pipeStepPcmDao.insertSelective(pipeStepPcm);
		}
		// Update Notification
		pipeStepNotificationDao.deleteByPipeId(pipeline.getId());
		PipeStepNotification pipeStepNotification = pipeline.getPipeStepNotification();
		if (nonNull(pipeStepNotification)) {
			pipeStepNotification.preInsert();
			pipeStepNotification.setPipeId(pipeline.getId());
			pipeStepNotification.setContactGroupIds(StringUtils.join(pipeStepNotification.getContactGroupId(), ","));
			pipeStepNotificationDao.insertSelective(pipeStepNotification);
		}
	}

	@Override
	public PipeStepBuilding getPipeStepBuilding(Integer clusterId, Integer pipeId, Integer refType) {
		Project project = projectDao.getByAppClusterId(clusterId);
		Assert2.notNullOf(project, "project");
		PipeStepBuilding pipeStepBuilding = pipeStepBuildingDao.selectByPipeId(pipeId);
		if (Objects.isNull(pipeStepBuilding)) {
			pipeStepBuilding = new PipeStepBuilding();
			pipeStepBuilding.setPipeId(pipeId);
		}
		if (nonNull(refType)) {
			pipeStepBuilding.setRefType(refType);
		}
		List<PipeStepBuildingProject> pipeStepBuildingProjectsFromdb = pipeStepBuildingProjectDao.selectByPipeId(pipeId);
		LinkedHashSet<Dependency> dependencys = dependencyService.getHierarchyDependencys(project.getId(), null);
		List<PipeStepBuildingProject> pipeStepBuildingProjects = new ArrayList<>();
		for (Dependency dependency : dependencys) {
			PipeStepBuildingProject pipeStepBuildingProject = getPipeStepBuildingProject(pipeStepBuildingProjectsFromdb,
					dependency.getDependentId());
			if (isNull(pipeStepBuildingProject)) {
				pipeStepBuildingProject = new PipeStepBuildingProject();
			}
			Project project1 = projectDao.selectByPrimaryKey(dependency.getDependentId());
			if (project1 == null) {
				continue;
			}
			pipeStepBuildingProject.setProjectId(dependency.getDependentId());
			pipeStepBuildingProject.setProjectName(project1.getProjectName());
			List<String> branchs = projectService.getBranchsByProjectId(pipeStepBuildingProject.getProjectId(), refType);
			pipeStepBuildingProject.setBranchs(branchs);
			pipeStepBuildingProjects.add(pipeStepBuildingProject);
		}

		// self
		PipeStepBuildingProject pipeStepBuildingProject = getPipeStepBuildingProject(pipeStepBuildingProjectsFromdb,
				project.getId());
		if (isNull(pipeStepBuildingProject)) {
			pipeStepBuildingProject = new PipeStepBuildingProject();
		}
		pipeStepBuildingProject.setProjectId(project.getId());
		pipeStepBuildingProject.setProjectName(project.getProjectName());
		List<String> branchs = projectService.getBranchsByProjectId(pipeStepBuildingProject.getProjectId(), refType);
		pipeStepBuildingProject.setBranchs(branchs);
		pipeStepBuildingProjects.add(pipeStepBuildingProject);

		pipeStepBuilding.setPipeStepBuildingProjects(pipeStepBuildingProjects);
		return pipeStepBuilding;
	}

	@Override
	public List<Pipeline> getForSelect(String environment) {
		return pipelineDao.list(getRequestOrganizationCodes(), null, null, null, environment,null);
	}

	private PipeStepBuildingProject getPipeStepBuildingProject(List<PipeStepBuildingProject> pipeStepBuildingProjects,
			Integer projectId) {
		if (isEmpty(pipeStepBuildingProjects) || isNull(projectId)) {
			return null;
		}
		for (PipeStepBuildingProject pipeStepBuildingProject : pipeStepBuildingProjects) {
			if (pipeStepBuildingProject.getProjectId().equals(projectId)) {
				return pipeStepBuildingProject;
			}
		}
		return null;
	}
}
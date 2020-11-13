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
import com.wl4g.components.data.page.PageModel;
import com.wl4g.devops.ci.dao.ClusterExtensionDao;
import com.wl4g.devops.ci.dao.PipeStageBuildingDao;
import com.wl4g.devops.ci.dao.PipeStageBuildingProjectDao;
import com.wl4g.devops.ci.dao.PipeStageDeployDao;
import com.wl4g.devops.ci.dao.PipeStageInstanceCommandDao;
import com.wl4g.devops.ci.dao.PipeStageNotificationDao;
import com.wl4g.devops.ci.dao.PipeStagePcmDao;
import com.wl4g.devops.ci.dao.PipelineDao;
import com.wl4g.devops.ci.dao.PipelineInstanceDao;
import com.wl4g.devops.ci.dao.ProjectDao;
import com.wl4g.devops.ci.service.DependencyService;
import com.wl4g.devops.ci.service.PipelineService;
import com.wl4g.devops.ci.service.ProjectService;
import com.wl4g.devops.common.bean.ci.ClusterExtension;
import com.wl4g.devops.common.bean.ci.Dependency;
import com.wl4g.devops.common.bean.ci.PipeStageBuilding;
import com.wl4g.devops.common.bean.ci.PipeStageBuildingProject;
import com.wl4g.devops.common.bean.ci.PipeStageDeploy;
import com.wl4g.devops.common.bean.ci.PipeStageInstanceCommand;
import com.wl4g.devops.common.bean.ci.PipeStageNotification;
import com.wl4g.devops.common.bean.ci.PipeStagePcm;
import com.wl4g.devops.common.bean.ci.Pipeline;
import com.wl4g.devops.common.bean.ci.PipelineInstance;
import com.wl4g.devops.common.bean.ci.Project;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

import static com.wl4g.iam.core.utils.IamOrganizationHolder.getRequestOrganizationCode;
import static com.wl4g.iam.core.utils.IamOrganizationHolder.getRequestOrganizationCodes;
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
	private PipeStageBuildingDao pipeStepBuildingDao;

	@Autowired
	private PipeStageBuildingProjectDao pipeStepBuildingProjectDao;

	@Autowired
	private PipeStagePcmDao pipeStepPcmDao;

	@Autowired
	private PipeStageNotificationDao pipeStepNotificationDao;

	@Autowired
	private ProjectDao projectDao;

	@Autowired
	private DependencyService dependencyService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private PipeStageInstanceCommandDao pipeStepInstanceCommandDao;

	@Autowired
	private PipeStageDeployDao pipeStepDeployDao;

	@Autowired
	private ClusterExtensionDao clusterExtensionDao;

	@Override
	public PageModel<Pipeline> list(PageModel<Pipeline> pm, String pipeName, String providerKind, String environment) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		List<Pipeline> list = pipelineDao.list(getRequestOrganizationCodes(), null, pipeName, providerKind, environment, null);
		for (Pipeline pipeline : list) {
			List<PipeStageBuildingProject> pipeStepBuildingProjects = pipeStepBuildingProjectDao.selectByPipeId(pipeline.getId());
			pipeline.setPipeStepBuildingProjects(pipeStepBuildingProjects);
		}
		pm.setRecords(list);
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
	public Pipeline detail(Long id) {
		Assert2.notNullOf(id, "id");
		// Pipeline
		Pipeline pipeline = pipelineDao.selectByPrimaryKey(id);
		// Pipeline Deploy
		PipeStageDeploy pipeStepDeploy = pipeStepDeployDao.selectByPipeId(id);
		pipeline.setPipeStepDeploy(pipeStepDeploy);
		// Pipeline Instance
		List<PipelineInstance> pipelineInstances = pipelineInstanceDao.selectByPipeId(id);
		Long[] instanceIds = new Long[pipelineInstances.size()];
		for (int i = 0; i < pipelineInstances.size(); i++) {
			instanceIds[i] = pipelineInstances.get(i).getInstanceId();
		}
		pipeline.setInstanceIds(instanceIds);

		// Pipeline Building
		PipeStageBuilding pipeStepBuilding = pipeStepBuildingDao.selectByPipeId(id);
		pipeline.setPipeStepBuilding(pipeStepBuilding);

		// Pipeline Pcm
		PipeStagePcm pipeStepPcm = pipeStepPcmDao.selectByPipeId(id);
		pipeline.setPipeStepPcm(pipeStepPcm);

		// Pipeline Notification
		PipeStageNotification pipeStepNotification = pipeStepNotificationDao.selectByPipeId(id);
		if (Objects.nonNull(pipeStepNotification)) {
			pipeStepNotification.setContactGroupId2(pipeStepNotification.getContactGroupIds().split(","));
			pipeline.setPipeStepNotification(pipeStepNotification);
		}

		// Pipeline Instance Command
		PipeStageInstanceCommand pipeStepInstanceCommand = pipeStepInstanceCommandDao.selectByPipeId(id);
		pipeline.setPipeStepInstanceCommand(pipeStepInstanceCommand);

		// TODO ...... testing,analysis,docker,k8s

		return pipeline;
	}

	@Override
	public void del(Long id) {
		Pipeline pipeline = new Pipeline();
		pipeline.setId(id);
		pipeline.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		pipelineDao.updateByPrimaryKeySelective(pipeline);
	}

	@Override
	public List<Pipeline> getByClusterId(Long clusterId) {
		return pipelineDao.selectByClusterId(clusterId);
	}

	public void insert(Pipeline pipeline) {
		Assert2.notNullOf(pipeline, "pipeline");
		// Insert Pipeline
		pipeline.preInsert(getRequestOrganizationCode());
		pipelineDao.insertSelective(pipeline);
		// Insert PipeInstance
		Long[] instanceIds = pipeline.getInstanceIds();

		PipeStageDeploy pipeStepDeploy = pipeline.getPipeStepDeploy();
		if (isNull(pipeStepDeploy)) {
			pipeStepDeploy = new PipeStageDeploy();
		}
		pipeStepDeploy.preInsert();
		pipeStepDeploy.setPipeId(pipeline.getId());
		pipeStepDeployDao.insertSelective(pipeStepDeploy);

		if (nonNull(instanceIds) && instanceIds.length > 0) {
			List<PipelineInstance> pipelineInstances = new ArrayList<>();
			for (Long i : instanceIds) {
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
		PipeStageBuilding pipeStepBuilding = pipeline.getPipeStepBuilding();
		if (nonNull(pipeStepBuilding)) {
			pipeStepBuilding.preInsert();
			pipeStepBuilding.setPipeId(pipeline.getId());
			pipeStepBuildingDao.insertSelective(pipeStepBuilding);
			// Insert PipeStepBuildingProject
			List<PipeStageBuildingProject> pipeStepBuildingProjects = pipeline.getPipeStepBuilding()
					.getPipeStepBuildingProjects();
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
		PipeStageInstanceCommand pipeStepInstanceCommand = pipeline.getPipeStepInstanceCommand();
		if (nonNull(pipeStepInstanceCommand)) {
			pipeStepInstanceCommand.preInsert();
			pipeStepInstanceCommand.setPipeId(pipeline.getId());
			pipeStepInstanceCommandDao.insertSelective(pipeStepInstanceCommand);
		}

		// TODO ...... testing,analysis,docker,k8s

		// Insert Pcm
		PipeStagePcm pipeStepPcm = pipeline.getPipeStepPcm();
		if (nonNull(pipeStepPcm)) {
			pipeStepPcm.preInsert();
			pipeStepPcm.setPipeId(pipeline.getId());
			pipeStepPcmDao.insertSelective(pipeStepPcm);
		}
		// Insert Notification
		PipeStageNotification pipeStepNotification = pipeline.getPipeStepNotification();
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

		PipeStageDeploy pipeStepDeploy = pipeline.getPipeStepDeploy();
		if (nonNull(pipeStepDeploy)) {
			pipeStepDeployDao.updateByPrimaryKeySelective(pipeStepDeploy);
		} else {
			pipeStepDeploy = new PipeStageDeploy();
			pipeStepDeploy.preInsert();
			pipeStepDeploy.setPipeId(pipeline.getId());
			pipeStepDeployDao.insertSelective(pipeStepDeploy);
		}

		// Update PipeInstance
		Long[] instanceIds = pipeline.getInstanceIds();
		pipelineInstanceDao.deleteByPipeId(pipeline.getId());
		if (nonNull(instanceIds)) {
			List<PipelineInstance> pipelineInstances = new ArrayList<>();
			for (Long i : instanceIds) {
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
		PipeStageBuilding pipeStepBuilding = pipeline.getPipeStepBuilding();
		if (nonNull(pipeStepBuilding)) {
			pipeStepBuilding.preInsert();
			pipeStepBuilding.setPipeId(pipeline.getId());
			pipeStepBuildingDao.insertSelective(pipeStepBuilding);
			// Update PipeStepBuildingProject
			List<PipeStageBuildingProject> pipeStepBuildingProjects = pipeline.getPipeStepBuilding()
					.getPipeStepBuildingProjects();
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
		PipeStageInstanceCommand pipeStepInstanceCommand = pipeline.getPipeStepInstanceCommand();
		if (nonNull(pipeStepInstanceCommand)) {
			pipeStepInstanceCommand.preInsert();
			pipeStepInstanceCommand.setPipeId(pipeline.getId());
			pipeStepInstanceCommandDao.insertSelective(pipeStepInstanceCommand);
		}

		// TODO ...... testing,analysis,docker,k8s

		// Update Pcm
		pipeStepPcmDao.deleteByPipeId(pipeline.getId());
		PipeStagePcm pipeStepPcm = pipeline.getPipeStepPcm();
		if (nonNull(pipeStepPcm)) {
			pipeStepPcm.preInsert();
			pipeStepPcm.setPipeId(pipeline.getId());
			pipeStepPcmDao.insertSelective(pipeStepPcm);
		}
		// Update Notification
		pipeStepNotificationDao.deleteByPipeId(pipeline.getId());
		PipeStageNotification pipeStepNotification = pipeline.getPipeStepNotification();
		if (nonNull(pipeStepNotification)) {
			pipeStepNotification.preInsert();
			pipeStepNotification.setPipeId(pipeline.getId());
			pipeStepNotification.setContactGroupIds(StringUtils.join(pipeStepNotification.getContactGroupId(), ","));
			pipeStepNotificationDao.insertSelective(pipeStepNotification);
		}
	}

	@Override
	public PipeStageBuilding getPipeStepBuilding(Long clusterId, Long pipeId, Integer refType) throws Exception {
		Project project = projectDao.getByAppClusterId(clusterId);
		Assert2.notNullOf(project, "project");
		PipeStageBuilding pipeStepBuilding = pipeStepBuildingDao.selectByPipeId(pipeId);
		if (Objects.isNull(pipeStepBuilding)) {
			pipeStepBuilding = new PipeStageBuilding();
			pipeStepBuilding.setPipeId(pipeId);
		}
		if (nonNull(refType)) {
			pipeStepBuilding.setRefType(refType);
		}
		List<PipeStageBuildingProject> pipeStepBuildingProjectsFromdb = pipeStepBuildingProjectDao.selectByPipeId(pipeId);
		LinkedHashSet<Dependency> dependencys = dependencyService.getHierarchyDependencys(project.getId(), null);
		List<PipeStageBuildingProject> pipeStepBuildingProjects = new ArrayList<>();
		for (Dependency dependency : dependencys) {
			PipeStageBuildingProject pipeStepBuildingProject = getPipeStepBuildingProject(pipeStepBuildingProjectsFromdb,
					dependency.getDependentId());
			if (isNull(pipeStepBuildingProject)) {
				pipeStepBuildingProject = new PipeStageBuildingProject();
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
		PipeStageBuildingProject pipeStepBuildingProject = getPipeStepBuildingProject(pipeStepBuildingProjectsFromdb,
				project.getId());
		if (isNull(pipeStepBuildingProject)) {
			pipeStepBuildingProject = new PipeStageBuildingProject();
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
		return pipelineDao.list(getRequestOrganizationCodes(), null, null, null, environment, null);
	}

	@Override
	public PageModel<ClusterExtension> clusterExtensionList(PageModel<ClusterExtension> pm, String clusterName) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(clusterExtensionDao.list(clusterName));
		return pm;
	}

	@Override
	public void saveClusterExtension(ClusterExtension clusterExtension) {
		Assert2.notNull(clusterExtension, "clusterExtension");
		ClusterExtension clusterExtensionDb = clusterExtensionDao.selectByClusterId(clusterExtension.getClusterId());
		if (Objects.nonNull(clusterExtensionDb)) {// update
			clusterExtensionDb.preUpdate();
			clusterExtensionDao.updateByPrimaryKeySelective(clusterExtension);
		} else {// insert
			clusterExtension.preInsert();
			clusterExtensionDao.insertSelective(clusterExtension);
		}
	}

	private PipeStageBuildingProject getPipeStepBuildingProject(List<PipeStageBuildingProject> pipeStepBuildingProjects,
			Long projectId) {
		if (isEmpty(pipeStepBuildingProjects) || isNull(projectId)) {
			return null;
		}
		for (PipeStageBuildingProject pipeStepBuildingProject : pipeStepBuildingProjects) {
			if (pipeStepBuildingProject.getProjectId().equals(projectId)) {
				return pipeStepBuildingProject;
			}
		}
		return null;
	}
}
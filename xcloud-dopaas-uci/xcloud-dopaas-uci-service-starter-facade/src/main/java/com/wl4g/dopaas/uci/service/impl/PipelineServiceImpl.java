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
package com.wl4g.dopaas.uci.service.impl;

import com.wl4g.component.common.id.SnowflakeIdGenerator;
import com.wl4g.component.common.lang.Assert2;
import com.wl4g.component.core.bean.BaseBean;
import com.wl4g.component.core.page.PageHolder;
import com.wl4g.dopaas.uci.data.ClusterExtensionDao;
import com.wl4g.dopaas.uci.data.PipeStageBuildingDao;
import com.wl4g.dopaas.uci.data.PipeStageBuildingProjectDao;
import com.wl4g.dopaas.uci.data.PipeStageDeployDao;
import com.wl4g.dopaas.uci.data.PipeStageInstanceCommandDao;
import com.wl4g.dopaas.uci.data.PipeStageNotificationDao;
import com.wl4g.dopaas.uci.data.PipeStagePcmDao;
import com.wl4g.dopaas.uci.data.PipelineDao;
import com.wl4g.dopaas.uci.data.PipelineInstanceDao;
import com.wl4g.dopaas.uci.data.ProjectDao;
import com.wl4g.dopaas.common.bean.uci.*;
import com.wl4g.dopaas.uci.data.*;
import com.wl4g.dopaas.uci.service.DependencyService;
import com.wl4g.dopaas.uci.service.PipelineService;
import com.wl4g.dopaas.uci.service.ProjectService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

import static com.wl4g.component.common.collection.CollectionUtils2.safeList;
import static com.wl4g.component.common.lang.Assert2.notNullOf;
import static com.wl4g.iam.common.utils.IamOrganizationUtils.getRequestOrganizationCode;
import static com.wl4g.iam.common.utils.IamOrganizationUtils.getRequestOrganizationCodes;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * {@link PipelineServiceImpl}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-04-27
 * @sine v1.0
 * @see
 */
@Service
public class PipelineServiceImpl implements PipelineService {

	@Autowired
	private PipelineDao pipelineDao;

	@Autowired
	private PipelineInstanceDao pipelineInstanceDao;

	@Autowired
	private PipeStageBuildingDao pipeStageBuildingDao;

	@Autowired
	private PipeStageBuildingProjectDao pipeStepBuildingProjectDao;

	@Autowired
	private PipeStagePcmDao pipeStepPcmDao;

	@Autowired
	private PipeStepApiDao pipeStepApiDao;

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
	public PageHolder<Pipeline> list(PageHolder<Pipeline> pm, String pipeName, String providerKind, String environment) {
		pm.useCount().bindPage();
		List<Pipeline> pipes = pipelineDao.list(getRequestOrganizationCodes(), null, pipeName, providerKind, environment, null);
		for (Pipeline p : safeList(pipes)) {
			p.setPipeStepBuildingProjects(pipeStepBuildingProjectDao.selectByPipeId(p.getId()));
		}
		pm.setRecords(pipes);
		return pm;
	}

	@Override
	public List<Pipeline> findList(List<String> organizationCodes, Long id, String pipeName, String providerKind,
			String environment, String clusterName) {
		return pipelineDao.list(getRequestOrganizationCodes(), null, pipeName, providerKind, environment, null);
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
		PipeStageBuilding pipeStepBuilding = pipeStageBuildingDao.selectByPipeId(id);
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

		// Pipeline Api
		PipeStepApi pipeStepApi = pipeStepApiDao.selectByPipeId(id);
		pipeline.setPipeStepApi(pipeStepApi);

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
				pipelineInstance.setId(SnowflakeIdGenerator.getDefault().nextId());
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
			pipeStageBuildingDao.insertSelective(pipeStepBuilding);
			// Insert PipeStepBuildingProject
			List<PipeStageBuildingProject> pipeStepBuildingProjects = pipeline.getPipeStepBuilding()
					.getPipeStepBuildingProjects();
			if (!CollectionUtils.isEmpty(pipeStepBuildingProjects)) {
				for (int i = 0; i < pipeStepBuildingProjects.size(); i++) {
					pipeStepBuildingProjects.get(i).preInsert();
					pipeStepBuildingProjects.get(i).setId(SnowflakeIdGenerator.getDefault().nextId());
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

		//Insert Api Config
		PipeStepApi pipeStepApi = pipeline.getPipeStepApi();
		if (nonNull(pipeStepApi)) {
			pipeStepApi.preInsert();
			pipeStepApi.setPipeId(pipeline.getId());
			pipeStepApiDao.insertSelective(pipeStepApi);
		}


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
				pipelineInstance.setId(SnowflakeIdGenerator.getDefault().nextId());
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
		pipeStageBuildingDao.deleteByPipeId(pipeline.getId());
		PipeStageBuilding pipeStepBuilding = pipeline.getPipeStepBuilding();
		if (nonNull(pipeStepBuilding)) {
			pipeStepBuilding.preInsert();
			pipeStepBuilding.setPipeId(pipeline.getId());
			pipeStageBuildingDao.insertSelective(pipeStepBuilding);
			// Update PipeStepBuildingProject
			List<PipeStageBuildingProject> pipeStepBuildingProjects = pipeline.getPipeStepBuilding()
					.getPipeStepBuildingProjects();
			if (!CollectionUtils.isEmpty(pipeStepBuildingProjects)) {
				for (int i = 0; i < pipeStepBuildingProjects.size(); i++) {
					pipeStepBuildingProjects.get(i).preInsert();
					pipeStepBuildingProjects.get(i).setId(SnowflakeIdGenerator.getDefault().nextId());
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

		PipeStepApi pipeStepApi = pipeline.getPipeStepApi();
		if (nonNull(pipeStepApi)) {
			if(nonNull(pipeStepApi.getId())){
				pipeStepApi.preUpdate();
				pipeStepApi.setPipeId(pipeline.getId());
				pipeStepApiDao.updateByPrimaryKeySelective(pipeStepApi);
			}else{
				pipeStepApi.preInsert();
				pipeStepApi.setPipeId(pipeline.getId());
				pipeStepApiDao.insertSelective(pipeStepApi);
			}
		}

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
	public PipeStageBuilding getSimplePipeStageBuilding(Long pipeId) {
		return pipeStageBuildingDao.selectByPipeId(pipeId);
	}

	@Override
	public PipeStageBuilding getPipeStageBuilding(Long clusterId, Long pipeId, Integer refType) throws Exception {
		Project project = notNullOf(projectDao.getByAppClusterId(clusterId), "project");

		PipeStageBuilding pipeStepBuilding = pipeStageBuildingDao.selectByPipeId(pipeId);
		if (Objects.isNull(pipeStepBuilding)) {
			pipeStepBuilding = new PipeStageBuilding();
			pipeStepBuilding.setPipeId(pipeId);
		}
		if (nonNull(refType)) {
			pipeStepBuilding.setRefType(refType);
		}

		List<PipeStageBuildingProject> pipeStageBuildingProjects1 = pipeStepBuildingProjectDao.selectByPipeId(pipeId);
		LinkedHashSet<Dependency> dependencys = dependencyService.getHierarchyDependencys(project.getId(), null);

		List<PipeStageBuildingProject> pipeStageBuildingProjects2 = new ArrayList<>();
		for (Dependency dependency : dependencys) {
			PipeStageBuildingProject pipeStepBuildingProject = getPipeStepBuildingProject(pipeStageBuildingProjects1,
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
			pipeStageBuildingProjects2.add(pipeStepBuildingProject);
		}

		// self
		PipeStageBuildingProject pipeStageBuildingProject = getPipeStepBuildingProject(pipeStageBuildingProjects1,
				project.getId());
		if (isNull(pipeStageBuildingProject)) {
			pipeStageBuildingProject = new PipeStageBuildingProject();
		}
		pipeStageBuildingProject.setProjectId(project.getId());
		pipeStageBuildingProject.setProjectName(project.getProjectName());
		List<String> branchs = projectService.getBranchsByProjectId(pipeStageBuildingProject.getProjectId(), refType);
		pipeStageBuildingProject.setBranchs(branchs);
		pipeStageBuildingProjects2.add(pipeStageBuildingProject);

		pipeStepBuilding.setPipeStepBuildingProjects(pipeStageBuildingProjects2);
		return pipeStepBuilding;
	}

	@Override
	public List<Pipeline> getForSelect(String environment) {
		return pipelineDao.list(getRequestOrganizationCodes(), null, null, null, environment, null);
	}

	@Override
	public PageHolder<ClusterExtension> clusterExtensionList(PageHolder<ClusterExtension> pm, String clusterName) {
		pm.useCount().bindPage();
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

	@Override
	public ClusterExtension getClusterExtensionByName(String clusterName) {
		return clusterExtensionDao.selectByClusterName(clusterName);
	}

	@Override
	public PipeStageInstanceCommand getPipeInstanceById(Long pipeId) {
		return pipeStepInstanceCommandDao.selectByPipeId(pipeId);
	}

}
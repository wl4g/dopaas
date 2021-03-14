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
package com.wl4g.dopaas.common.bean.uci;

import com.wl4g.component.core.bean.BaseBean;
import com.wl4g.dopaas.common.bean.cmdb.AppInstance;

import java.util.List;

public class Pipeline extends BaseBean {
	private static final long serialVersionUID = 6815608076300843748L;

	private String pipeName;

	private Long clusterId;

	private String providerKind;

	private String environment;

	private String parentAppHome;

	private String assetsDir;

	private String clusterName;

	// ==================Expand==================

	private List<AppInstance> instances;

	private Long[] instanceIds;

	private PipeStageBuilding pipeStepBuilding;

	private PipeStagePcm pipeStepPcm;

	private PipeStageNotification pipeStepNotification;

	private PipeStageInstanceCommand pipeStepInstanceCommand;

	private PipeStageDeploy pipeStepDeploy;

	private Integer pcmEnable;

	private Long pcmId;

	private Long deployId;

	private List<PipeStageBuildingProject> pipeStepBuildingProjects;

	public String getPipeName() {
		return pipeName;
	}

	public void setPipeName(String pipeName) {
		this.pipeName = pipeName == null ? null : pipeName.trim();
	}

	public Long getClusterId() {
		return clusterId;
	}

	public void setClusterId(Long clusterId) {
		this.clusterId = clusterId;
	}

	public String getProviderKind() {
		return providerKind;
	}

	public void setProviderKind(String providerKind) {
		this.providerKind = providerKind == null ? null : providerKind.trim();
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment == null ? null : environment.trim();
	}

	public String getParentAppHome() {
		return parentAppHome;
	}

	public void setParentAppHome(String parentAppHome) {
		this.parentAppHome = parentAppHome == null ? null : parentAppHome.trim();
	}

	public String getAssetsDir() {
		return assetsDir;
	}

	public void setAssetsDir(String assetsDir) {
		this.assetsDir = assetsDir == null ? null : assetsDir.trim();
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public List<AppInstance> getInstances() {
		return instances;
	}

	public void setInstances(List<AppInstance> instances) {
		this.instances = instances;
	}

	public Long[] getInstanceIds() {
		return instanceIds;
	}

	public void setInstanceIds(Long[] instanceIds) {
		this.instanceIds = instanceIds;
	}

	public PipeStageBuilding getPipeStepBuilding() {
		return pipeStepBuilding;
	}

	public void setPipeStepBuilding(PipeStageBuilding pipeStepBuilding) {
		this.pipeStepBuilding = pipeStepBuilding;
	}

	public PipeStagePcm getPipeStepPcm() {
		return pipeStepPcm;
	}

	public void setPipeStepPcm(PipeStagePcm pipeStepPcm) {
		this.pipeStepPcm = pipeStepPcm;
	}

	public PipeStageNotification getPipeStepNotification() {
		return pipeStepNotification;
	}

	public void setPipeStepNotification(PipeStageNotification pipeStepNotification) {
		this.pipeStepNotification = pipeStepNotification;
	}

	public PipeStageInstanceCommand getPipeStepInstanceCommand() {
		return pipeStepInstanceCommand;
	}

	public void setPipeStepInstanceCommand(PipeStageInstanceCommand pipeStepInstanceCommand) {
		this.pipeStepInstanceCommand = pipeStepInstanceCommand;
	}

	public Integer getPcmEnable() {
		return pcmEnable;
	}

	public void setPcmEnable(Integer pcmEnable) {
		this.pcmEnable = pcmEnable;
	}

	public Long getPcmId() {
		return pcmId;
	}

	public void setPcmId(Long pcmId) {
		this.pcmId = pcmId;
	}

	public PipeStageDeploy getPipeStepDeploy() {
		return pipeStepDeploy;
	}

	public void setPipeStepDeploy(PipeStageDeploy pipeStepDeploy) {
		this.pipeStepDeploy = pipeStepDeploy;
	}

	public Long getDeployId() {
		return deployId;
	}

	public void setDeployId(Long deployId) {
		this.deployId = deployId;
	}

	public List<PipeStageBuildingProject> getPipeStepBuildingProjects() {
		return pipeStepBuildingProjects;
	}

	public void setPipeStepBuildingProjects(List<PipeStageBuildingProject> pipeStepBuildingProjects) {
		this.pipeStepBuildingProjects = pipeStepBuildingProjects;
	}
}
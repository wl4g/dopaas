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
package com.wl4g.devops.common.bean.ci;

import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.erm.AppInstance;

import java.util.List;

public class Pipeline extends BaseBean {
	private static final long serialVersionUID = 6815608076300843748L;

	private String pipeName;

	private Integer clusterId;

	private String providerKind;

	private String environment;

	private String parentAppHome;

	private String assetsDir;

	private String clusterName;

	// ==================Expand==================

	private List<AppInstance> instances;

	private Integer[] instanceIds;

	private PipeStepBuilding pipeStepBuilding;

	private PipeStepPcm pipeStepPcm;

	private PipeStepNotification pipeStepNotification;

	private PipeStepInstanceCommand pipeStepInstanceCommand;

	private PipeStepDeploy pipeStepDeploy;

	private Integer pcmEnable;

	private Integer pcmId;

	private Integer deployId;

	public String getPipeName() {
		return pipeName;
	}

	public void setPipeName(String pipeName) {
		this.pipeName = pipeName == null ? null : pipeName.trim();
	}

	public Integer getClusterId() {
		return clusterId;
	}

	public void setClusterId(Integer clusterId) {
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

	public Integer[] getInstanceIds() {
		return instanceIds;
	}

	public void setInstanceIds(Integer[] instanceIds) {
		this.instanceIds = instanceIds;
	}

	public PipeStepBuilding getPipeStepBuilding() {
		return pipeStepBuilding;
	}

	public void setPipeStepBuilding(PipeStepBuilding pipeStepBuilding) {
		this.pipeStepBuilding = pipeStepBuilding;
	}

	public PipeStepPcm getPipeStepPcm() {
		return pipeStepPcm;
	}

	public void setPipeStepPcm(PipeStepPcm pipeStepPcm) {
		this.pipeStepPcm = pipeStepPcm;
	}

	public PipeStepNotification getPipeStepNotification() {
		return pipeStepNotification;
	}

	public void setPipeStepNotification(PipeStepNotification pipeStepNotification) {
		this.pipeStepNotification = pipeStepNotification;
	}

	public PipeStepInstanceCommand getPipeStepInstanceCommand() {
		return pipeStepInstanceCommand;
	}

	public void setPipeStepInstanceCommand(PipeStepInstanceCommand pipeStepInstanceCommand) {
		this.pipeStepInstanceCommand = pipeStepInstanceCommand;
	}

	public Integer getPcmEnable() {
		return pcmEnable;
	}

	public void setPcmEnable(Integer pcmEnable) {
		this.pcmEnable = pcmEnable;
	}

	public Integer getPcmId() {
		return pcmId;
	}

	public void setPcmId(Integer pcmId) {
		this.pcmId = pcmId;
	}

	public PipeStepDeploy getPipeStepDeploy() {
		return pipeStepDeploy;
	}

	public void setPipeStepDeploy(PipeStepDeploy pipeStepDeploy) {
		this.pipeStepDeploy = pipeStepDeploy;
	}

	public Integer getDeployId() {
		return deployId;
	}

	public void setDeployId(Integer deployId) {
		this.deployId = deployId;
	}
}
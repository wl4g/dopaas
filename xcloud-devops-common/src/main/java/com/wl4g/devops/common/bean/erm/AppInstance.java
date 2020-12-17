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
package com.wl4g.devops.common.bean.erm;

import java.util.List;

import com.wl4g.component.core.bean.BaseBean;

/**
 * 对应：app_instance表
 * 
 * @author sut
 * @date 2018年9月25日
 */
public class AppInstance extends BaseBean {
	private static final long serialVersionUID = 4324569366421220002L;

	private String name;

	private Long clusterId;

	private Long versionId;

	private Long hostId;

	private Long k8sId;

	private Long dockerId;

	private String cossRefBucket;

	private String envType;

	private Integer enable;

	// private String endpoint;

	private String remark;

	// private Ssh ssh;

	//
	// --- Temporary fields. ---
	//

	private String hostname;

	private String clusterName;

	private String endpoint;

	private Integer deployType;

	private DockerCluster dockerCluster;

	private List<Long> hosts;

	public Long getClusterId() {
		return clusterId;
	}

	public void setClusterId(Long clusterId) {
		this.clusterId = clusterId;
	}

	public Long getVersionId() {
		return versionId;
	}

	public void setVersionId(Long versionId) {
		this.versionId = versionId;
	}

	public Long getHostId() {
		return hostId;
	}

	public void setHostId(Long hostId) {
		this.hostId = hostId;
	}

	public String getEnvType() {
		return envType;
	}

	public void setEnvType(String envType) {
		this.envType = envType;
	}

	@Override
	public Integer getEnable() {
		return enable;
	}

	@Override
	public void setEnable(Integer enable) {
		this.enable = enable;
	}

	@Override
	public String getRemark() {
		return remark;
	}

	@Override
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getK8sId() {
		return k8sId;
	}

	public void setK8sId(Long k8sId) {
		this.k8sId = k8sId;
	}

	public Long getDockerId() {
		return dockerId;
	}

	public void setDockerId(Long dockerId) {
		this.dockerId = dockerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getCossRefBucket() {
		return cossRefBucket;
	}

	public void setCossRefBucket(String cossRefBucket) {
		this.cossRefBucket = cossRefBucket;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public Integer getDeployType() {
		return deployType;
	}

	public void setDeployType(Integer deployType) {
		this.deployType = deployType;
	}

	public DockerCluster getDockerCluster() {
		return dockerCluster;
	}

	public void setDockerCluster(DockerCluster dockerCluster) {
		this.dockerCluster = dockerCluster;
	}

	public List<Long> getHosts() {
		return hosts;
	}

	public void setHosts(List<Long> hosts) {
		this.hosts = hosts;
	}

	@Override
	public String toString() {
		return "AppInstance{" + "name='" + name + '\'' + ", clusterId=" + clusterId + ", versionId=" + versionId + ", hostId="
				+ hostId + ", k8sId=" + k8sId + ", dockerId=" + dockerId + ", envType='" + envType + '\'' + ", enable=" + enable
				+ ", remark='" + remark + '\'' + '}';
	}
}
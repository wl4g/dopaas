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
package com.wl4g.devops.ci.service;

import com.wl4g.components.core.web.model.PageModel;
import com.wl4g.devops.common.bean.ci.ClusterExtension;
import com.wl4g.devops.common.bean.ci.PipeStageBuilding;
import com.wl4g.devops.common.bean.ci.PipeStageInstanceCommand;
import com.wl4g.devops.common.bean.ci.Pipeline;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * {@link PipelineService}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @version v1.0 2020-04-27
 * @sine v1.0
 * @see
 */
public interface PipelineService {

	PageModel<Pipeline> list(PageModel<Pipeline> pm, String pipeName, String providerKind, String environment);

	List<Pipeline> findList(List<String> organizationCodes, Long id, String pipeName, @Param("providerKind") String providerKind,
			String environment, String clusterName);

	void save(Pipeline pipeline);

	Pipeline detail(Long id);

	void del(Long id);

	List<Pipeline> getByClusterId(Long clusterId);

	PipeStageBuilding getSimplePipeStageBuilding(Long pipeId);

	PipeStageBuilding getPipeStageBuilding(Long clusterId, Long pipeId, Integer refType) throws Exception;

	List<Pipeline> getForSelect(String environment);

	PageModel<ClusterExtension> clusterExtensionList(PageModel<ClusterExtension> pm, String clusterName);

	void saveClusterExtension(ClusterExtension clusterExtension);

	ClusterExtension getClusterExtensionByName(String clusterName);

	PipeStageInstanceCommand getPipeInstanceById(Long pipeId);

}
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

import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.component.rpc.springboot.feign.annotation.SpringBootFeignClient;
import com.wl4g.devops.common.bean.ci.ClusterExtension;
import com.wl4g.devops.common.bean.ci.PipeStageBuilding;
import com.wl4g.devops.common.bean.ci.PipeStageInstanceCommand;
import com.wl4g.devops.common.bean.ci.Pipeline;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * {@link PipelineService}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @version v1.0 2020-04-27
 * @sine v1.0
 * @see
 */
@SpringBootFeignClient(name = "${provider.serviceId:pipeline-service}")
@RequestMapping("/pipeline")
public interface PipelineService {

	@RequestMapping(value = "/list", method = POST)
	PageHolder<Pipeline> list(@RequestBody PageHolder<Pipeline> pm,
							  @RequestParam(name="pipeName",required=false) String pipeName,
							  @RequestParam(name="providerKind",required=false) String providerKind,
							  @RequestParam(name="environment",required=false) String environment);

	@RequestMapping(value = "/findList", method = POST)
	List<Pipeline> findList(@RequestBody List<String> organizationCodes,
							@RequestParam(name="id",required=false) Long id,
							@RequestParam(name="pipeName",required=false) String pipeName,
							@RequestParam(name="providerKind",required=false) String providerKind,
							@RequestParam(name="environment",required=false) String environment,
							@RequestParam(name="clusterName",required=false) String clusterName);

	@RequestMapping(value = "/save", method = POST)
	void save(@RequestBody Pipeline pipeline);

	@RequestMapping(value = "/detail", method = POST)
	Pipeline detail(@RequestParam(name="id",required=false) Long id);

	@RequestMapping(value = "/del", method = POST)
	void del(@RequestParam(name="id",required=false) Long id);

	@RequestMapping(value = "/getByClusterId", method = POST)
	List<Pipeline> getByClusterId(@RequestParam(name="clusterId",required=false) Long clusterId);

	@RequestMapping(value = "/getSimplePipeStageBuilding", method = POST)
	PipeStageBuilding getSimplePipeStageBuilding(@RequestParam(name="pipeId",required=false) Long pipeId);

	@RequestMapping(value = "/getPipeStageBuilding", method = POST)
	PipeStageBuilding getPipeStageBuilding(@RequestParam(name="clusterId",required=false) Long clusterId,
										   @RequestParam(name="pipeId",required=false) Long pipeId,
										   @RequestParam(name="refType",required=false) Integer refType) throws Exception;

	@RequestMapping(value = "/getForSelect", method = POST)
	List<Pipeline> getForSelect(@RequestParam(name="environment",required=false) String environment);

	@RequestMapping(value = "/clusterExtensionList", method = POST)
	PageHolder<ClusterExtension> clusterExtensionList(@RequestBody PageHolder<ClusterExtension> pm,
													  @RequestParam(name="clusterName",required=false) String clusterName);

	@RequestMapping(value = "/saveClusterExtension", method = POST)
	void saveClusterExtension(@RequestBody ClusterExtension clusterExtension);

	@RequestMapping(value = "/getClusterExtensionByName", method = POST)
	ClusterExtension getClusterExtensionByName(@RequestParam(name="clusterName",required=false) String clusterName);

	@RequestMapping(value = "/getPipeInstanceById", method = POST)
	PipeStageInstanceCommand getPipeInstanceById(@RequestParam(name="pipeId",required=false) Long pipeId);

}
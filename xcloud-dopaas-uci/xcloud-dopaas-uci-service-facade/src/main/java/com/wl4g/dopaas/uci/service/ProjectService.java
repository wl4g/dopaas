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
package com.wl4g.dopaas.uci.service;

import com.wl4g.component.core.page.PageHolder;
import com.wl4g.component.integration.feign.core.annotation.FeignConsumer;
import com.wl4g.dopaas.common.bean.uci.Project;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author vjay
 * @date 2019-05-17 10:23:00
 */
@FeignConsumer(name = "${provider.serviceId.uci-facade:uci-facade}")
@RequestMapping("/project-service")
public interface ProjectService {

	@RequestMapping(value = "/save", method = POST)
	void save(@RequestBody Project project);

	@RequestMapping(value = "/deleteById", method = POST)
	int deleteById(@RequestParam(name = "id", required = false) Long id);

	@RequestMapping(value = "/removeById", method = POST)
	int removeById(@RequestParam(name = "id", required = false) Long id);

	@RequestMapping(value = "/list", method = POST)
	PageHolder<Project> list(@RequestBody PageHolder<Project> pm,
			@RequestParam(name = "groupName", required = false) String groupName,
			@RequestParam(name = "projectName", required = false) String projectName);

	@RequestMapping(value = "/getBySelect", method = POST)
	List<Project> getBySelect(@RequestParam(name = "isBoot", required = false) Integer isBoot);

	@RequestMapping(value = "/selectByPrimaryKey", method = POST)
	Project selectByPrimaryKey(@RequestParam(name = "id", required = false) Long id);

	@RequestMapping(value = "/getProjectById", method = POST)
	Project getProjectById(@RequestParam(name = "id", required = false) Long id);

	@RequestMapping(value = "/getByAppClusterId", method = POST)
	Project getByAppClusterId(@RequestParam(name = "appClusteId", required = false) Long appClusteId);

	@RequestMapping(value = "/updateLockStatus", method = POST)
	int updateLockStatus(@RequestParam(name = "id", required = false) Long id,
			@RequestParam(name = "lockStatus", required = false) Integer lockStatus);

	@RequestMapping(value = "/getBranchs", method = POST)
	List<String> getBranchs(@RequestParam(name = "appClusterId", required = false) Long appClusterId,
			@RequestParam(name = "tagOrBranch", required = false) Integer tagOrBranch) throws Exception;

	@RequestMapping(value = "/getBranchsByProjectId", method = POST)
	List<String> getBranchsByProjectId(@RequestParam(name = "projectId", required = false) Long projectId,
			@RequestParam(name = "tagOrBranch", required = false) Integer tagOrBranch) throws Exception;

}
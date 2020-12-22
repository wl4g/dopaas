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
package com.wl4g.devops.ci.web;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.component.core.bean.model.SelectionModel;
import com.wl4g.devops.ci.service.PcmService;
import com.wl4g.devops.common.bean.ci.Pcm;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.apache.shiro.authz.annotation.Logical.AND;

/**
 * Porject manager
 *
 * @author vjay
 * @date 2020-01-03 16:28:00
 */
@RestController
@RequestMapping("/pcm")
public class PcmController {

	@Autowired
	private PcmService pcmService;

	//
	// PCM provider operation.
	//

	@RequestMapping(value = "/getUsers")
	public RespBase<?> getUsers(Long pcmId) {
		RespBase<Object> resp = RespBase.create();
		List<SelectionModel> users = pcmService.getUsers(pcmId);
		resp.setData(users);
		return resp;
	}

	@RequestMapping(value = "/getProjects")
	public RespBase<?> getProjects(Long pcmId) {
		RespBase<Object> resp = RespBase.create();
		List<SelectionModel> selectInfos = pcmService.getProjects(pcmId);
		resp.setData(selectInfos);
		return resp;
	}

	@RequestMapping(value = "/getProjectsByPcmId")
	public RespBase<?> getProjectsByPcmId(Long pcmId) {
		RespBase<Object> resp = RespBase.create();
		List<SelectionModel> selectInfos = pcmService.getProjectsByPcmId(pcmId);
		resp.setData(selectInfos);
		return resp;
	}

	@RequestMapping(value = "/getIssues")
	public RespBase<?> getIssues(Long pcmId, String userId, String projectId, String search) {
		RespBase<Object> resp = RespBase.create();
		List<SelectionModel> selectInfos = pcmService.getIssues(pcmId, userId, projectId, search);
		resp.setData(selectInfos);
		return resp;
	}

	@RequestMapping(value = "/getStatuses")
	public RespBase<?> getStatuses(Long pcmId) {
		RespBase<Object> resp = RespBase.create();
		List<SelectionModel> selectInfos = pcmService.getStatuses(pcmId);
		resp.setData(selectInfos);
		return resp;
	}

	@RequestMapping(value = "/getTrackers")
	public RespBase<?> getTrackers(Long pcmId) {
		RespBase<Object> resp = RespBase.create();
		List<SelectionModel> selectInfos = pcmService.getTrackers(pcmId);
		resp.setData(selectInfos);
		return resp;
	}

	@RequestMapping(value = "/getPriorities")
	public RespBase<?> getPriorities(Long pcmId) {
		RespBase<Object> resp = RespBase.create();
		List<SelectionModel> selectInfos = pcmService.getPriorities(pcmId);
		resp.setData(selectInfos);
		return resp;
	}

	//
	// PCM information.
	//

	@RequestMapping("/list")
	@RequiresPermissions(value = { "ci", "ci:pcm" }, logical = AND)
	public RespBase<?> list(PageHolder<Pcm> pm, String name, String providerKind, Integer authType) {
		RespBase<Object> resp = RespBase.create();
		resp.setData(pcmService.list(pm, name, providerKind, authType));
		return resp;
	}

	@RequestMapping("/save")
	@RequiresPermissions(value = { "ci", "ci:pcm" }, logical = AND)
	public RespBase<?> save(Pcm pcm) {
		RespBase<Object> resp = RespBase.create();
		pcmService.save(pcm);
		return resp;
	}

	@RequestMapping("/del")
	@RequiresPermissions(value = { "ci", "ci:pcm" }, logical = AND)
	public RespBase<?> del(Long id) {
		RespBase<Object> resp = RespBase.create();
		pcmService.del(id);
		return resp;
	}

	@RequestMapping("/detail")
	@RequiresPermissions(value = { "ci", "ci:pcm" }, logical = AND)
	public RespBase<?> detail(Long id) {
		RespBase<Object> resp = RespBase.create();
		Pcm pcm = pcmService.detail(id);
		resp.setData(pcm);
		return resp;
	}

	@RequestMapping("/all")
	public RespBase<?> all() {
		RespBase<Object> resp = RespBase.create();
		resp.setData(pcmService.all());
		return resp;
	}

}
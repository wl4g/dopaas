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

import com.wl4g.component.core.bean.model.PageHolder;
import com.wl4g.component.core.bean.model.SelectionModel;
import com.wl4g.component.rpc.feign.core.annotation.FeignConsumer;
import com.wl4g.dopaas.common.bean.uci.Pcm;
import com.wl4g.dopaas.common.bean.uci.PipeHistoryPcm;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author vjay
 * @date 2019-11-12 11:05:00
 */
@FeignConsumer(name = "${provider.serviceId.uci-facade:uci-facade}")
@RequestMapping("/pcm-service")
public interface PcmService {

	@RequestMapping(value = "/list", method = POST)
	PageHolder<Pcm> list(@RequestBody PageHolder<Pcm> pm, @RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "providerKind", required = false) String providerKind,
			@RequestParam(name = "authType", required = false) Integer authType);

	@RequestMapping(value = "/save", method = POST)
	void save(@RequestBody Pcm pcm);

	@RequestMapping(value = "/del", method = POST)
	void del(@RequestParam(name = "id", required = false) Long id);

	@RequestMapping(value = "/detail", method = POST)
	Pcm detail(@RequestParam(name = "id", required = false) Long id);

	@RequestMapping(value = "/all", method = POST)
	List<Pcm> all();

	@RequestMapping(value = "/getUsers", method = POST)
	List<SelectionModel> getUsers(@RequestParam(name = "pcmId", required = false) Long pcmId);

	@RequestMapping(value = "/getProjects", method = POST)
	List<SelectionModel> getProjects(@RequestParam(name = "pcmId", required = false) Long pcmId);

	@RequestMapping(value = "/getIssues", method = POST)
	List<SelectionModel> getIssues(@RequestParam(name = "pcmId", required = false) Long pcmId,
			@RequestParam(name = "userId", required = false) String userId,
			@RequestParam(name = "projectId", required = false) String projectId,
			@RequestParam(name = "search", required = false) String search);

	@RequestMapping(value = "/getProjectsByPcmId", method = POST)
	List<SelectionModel> getProjectsByPcmId(@RequestParam(name = "pcmId", required = false) Long pcmId);

	@RequestMapping(value = "/getTrackers", method = POST)
	List<SelectionModel> getTrackers(@RequestParam(name = "pcmId", required = false) Long pcmId);

	@RequestMapping(value = "/getStatuses", method = POST)
	List<SelectionModel> getStatuses(@RequestParam(name = "pcmId", required = false) Long pcmId);

	@RequestMapping(value = "/getPriorities", method = POST)
	List<SelectionModel> getPriorities(@RequestParam(name = "pcmId", required = false) Long pcmId);

	@RequestMapping(value = "/createIssues", method = POST)
	void createIssues(@RequestParam(name = "pcmId") Long pcmId, @RequestBody PipeHistoryPcm pipeHistoryPcm);

}
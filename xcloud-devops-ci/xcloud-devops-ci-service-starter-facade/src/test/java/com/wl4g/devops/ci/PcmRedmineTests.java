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
package com.wl4g.devops.ci;

import com.wl4g.CiFacade;
import com.wl4g.component.common.lang.Assert2;
import com.wl4g.component.core.framework.operator.GenericOperatorAdapter;
import com.wl4g.component.core.bean.model.SelectionModel;
import com.wl4g.devops.ci.data.PcmDao;
import com.wl4g.devops.ci.pcm.PcmOperator;
import com.wl4g.devops.ci.service.PcmService;
import com.wl4g.devops.common.bean.ci.Pcm;
import com.wl4g.devops.common.bean.ci.PipeHistoryPcm;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author vjay
 * @date 2020-04-09 11:36:00
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CiFacade.class)
@FixMethodOrder(MethodSorters.JVM)
public class PcmRedmineTests {

	private static final Long pcmId = 2L;

	@Autowired
	protected PcmDao pcmDao;

	@Autowired
	protected PcmService pcmService;

	@Autowired
	private GenericOperatorAdapter<PcmOperator.PcmKind, PcmOperator> pcmOperator;

	@Test
	public void getProjects() {
		Pcm pcm = getPcmKind(pcmId);
		List<SelectionModel> projects = pcmOperator.forOperator(pcm.getProviderKind()).getProjects(pcm);
		for (SelectionModel selectionModel : projects) {
			System.out.println(selectionModel.getLabel() + " ---" + selectionModel.getValue());
		}
	}

	@Test
	public void getUsers() {
		Pcm pcm = getPcmKind(pcmId);
		List<SelectionModel> users = pcmOperator.forOperator(pcm.getProviderKind()).getUsers(pcm);
		for (SelectionModel selectionModel : users) {
			System.out.println(selectionModel.getLabel() + " ---" + selectionModel.getValue());
		}
	}

	@Test
	public void getTrackers() {
		Pcm pcm = getPcmKind(pcmId);
		List<SelectionModel> trackers = pcmOperator.forOperator(pcm.getProviderKind()).getTracker(pcm);
		for (SelectionModel selectionModel : trackers) {
			System.out.println(selectionModel.getLabel() + " ---" + selectionModel.getValue());
		}
	}

	@Test
	public void getPriorities() {
		Pcm pcm = getPcmKind(pcmId);
		List<SelectionModel> priorities = pcmOperator.forOperator(pcm.getProviderKind()).getPriorities(pcm);
		for (SelectionModel selectionModel : priorities) {
			System.out.println(selectionModel.getLabel() + " ---" + selectionModel.getValue());
		}
	}

	@Test
	public void createIssues() {
		Pcm pcm = getPcmKind(pcmId);

		PipeHistoryPcm pipeHistoryPcm = new PipeHistoryPcm();
		pipeHistoryPcm.setXProjectId(8L);
		pipeHistoryPcm.setXSubject("test add issues");
		pipeHistoryPcm.setXAssignTo("27");

		pcmOperator.forOperator(pcm.getProviderKind()).createIssues(pcm, pipeHistoryPcm);

	}

	private Pcm getPcmKind(Long PcmId) {
		Pcm pcm = pcmDao.selectByPrimaryKey(PcmId);
		Assert2.notNullOf(pcm, "pcm");
		Assert.hasText(pcm.getProviderKind(), "provide kind is null");
		return pcm;
	}

}
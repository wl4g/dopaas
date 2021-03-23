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
package com.wl4g.dopaas.uci;

import com.wl4g.UciFacade;
import com.wl4g.dopaas.uci.service.OrchestrationManagerAdapter;
import com.wl4g.dopaas.uci.service.PipelineManagerAdapter;
import com.wl4g.dopaas.common.bean.uci.model.PipelineModel;
import com.wl4g.dopaas.common.bean.uci.param.RunParameter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * {@link PipelineManagerTests}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @author vjay
 * @version v1.0 2019-09-29
 * @sine v1.0
 * @see
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UciFacade.class)
public class PipelineManagerTests {

private @Autowired  PipelineManagerAdapter pipelineManager;

private @Autowired  OrchestrationManagerAdapter flowManagerService;

	@Test
	public void createTask() throws Exception {
		Long taskId = 152L;
		PipelineModel pipeModel = flowManagerService.buildPipeline(taskId);
		pipelineManager.runPipeline(new RunParameter(taskId, null, null, null, null, pipeModel));
	}

}
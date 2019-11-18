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
package com.wl4g.devops.ci.core;

import com.wl4g.devops.CiServer;
import com.wl4g.devops.ci.core.PipelineManager;
import com.wl4g.devops.ci.core.command.NewCommand;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author vjay
 * @date 2019-09-29 10:51:00
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CiServer.class)
public class PipelineManagerTests {

	@Autowired
	private PipelineManager pipeManager;

	@Test
	public void createTask() {
		pipeManager.newPipeline(new NewCommand(152, null, null, null));
	}

}
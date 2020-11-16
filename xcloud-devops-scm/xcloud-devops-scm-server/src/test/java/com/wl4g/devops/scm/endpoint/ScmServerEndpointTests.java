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
package com.wl4g.devops.scm.endpoint;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.wl4g.ScmServer;
import com.wl4g.components.common.lang.ThreadUtils2;
import com.wl4g.devops.scm.common.model.ReleaseConfigInfo;
import com.wl4g.devops.scm.common.model.AbstractConfigInfo.ConfigNode;
import com.wl4g.devops.scm.handler.CentralConfigServerHandler;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ScmServer.class)
@FixMethodOrder(MethodSorters.JVM)
public class ScmServerEndpointTests {

	/**
	 * Scm config handler.
	 */
	@Autowired
	protected CentralConfigServerHandler handler;

	/**
	 * Simulation config release. </br>
	 * The full demo needs to start SCM example.
	 * 
	 * @return
	 */
	@Test
	public void manualReleaseTests() {
		ReleaseConfigInfo result = new ReleaseConfigInfo();
		result.setCluster("scm-example");
		result.getMeta().setReleaseId("1");
		result.getMeta().setVersion("1.0.1");
		result.getNodes().add(new ConfigNode("localhost", "8848"));
		handler.release(result);

		// For simulation continuous running of SCM server
		ThreadUtils2.sleep(Integer.MAX_VALUE);
	}

}
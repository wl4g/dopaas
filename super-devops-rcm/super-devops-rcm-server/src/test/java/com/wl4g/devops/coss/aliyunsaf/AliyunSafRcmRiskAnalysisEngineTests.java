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
package com.wl4g.devops.coss.aliyunsaf;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.wl4g.devops.RcmServer;
import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.rcm.RcmProvider;
import com.wl4g.devops.rcm.RiskAnalysisEngine;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RcmServer.class, properties = {})
public class AliyunSafRcmRiskAnalysisEngineTests {

	@Autowired
	private GenericOperatorAdapter<RcmProvider, RiskAnalysisEngine> endpointAdapter;

	@Test
	public void aliyunSafEngineTest1() {
		System.out.println("Starting...");
		RiskAnalysisEngine engine = endpointAdapter.forOperator(RcmProvider.AliyunSafEngine);
		System.out.println(engine);
		// TODO
		System.out.println("End.");
	}

}

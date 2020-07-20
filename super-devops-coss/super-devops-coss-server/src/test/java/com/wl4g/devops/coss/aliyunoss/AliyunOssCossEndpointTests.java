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
package com.wl4g.devops.coss.aliyunoss;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.wl4g.devops.CossServer;
import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.coss.ServerCossEndpoint;
import com.wl4g.devops.coss.common.CossEndpoint;
import com.wl4g.devops.coss.common.CossEndpoint.CossProvider;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CossServer.class, properties = {})
@FixMethodOrder(MethodSorters.JVM)
public class AliyunOssCossEndpointTests {

	@Autowired
	private GenericOperatorAdapter<CossProvider, ServerCossEndpoint<?>> endpointAdapter;

	@Test
	public void ossEndpointTest1() {
		System.out.println("Starting...");
		CossEndpoint endpoint = endpointAdapter.forOperator(CossProvider.AliyunOss);
		System.out.println(endpoint.getBucketAcl("sm-clound"));
		System.out.println("End.");
	}

}
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
package com.wl4g.devops.iam.session;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.CACHE_SESSION;

import java.io.IOException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.base.Charsets;
import com.wl4g.devops.IamServer;
import com.wl4g.devops.common.utils.serialize.ProtostuffUtils;
import com.wl4g.devops.iam.common.session.IamSession;
import com.wl4g.devops.support.redis.ScanCursor;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.ScanParams;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = IamServer.class)
@FixMethodOrder(MethodSorters.JVM)
public class ScanCursorTests {

	@Autowired
	private JedisCluster cluster;

	@Test
	public void test1() {
		byte[] data = cluster.get("iam_session_1c315080e64b4731b011a14551a54c92".getBytes(Charsets.UTF_8));
		System.out.println("IamSession: " + ProtostuffUtils.deserialize(data, IamSession.class));
	}

	@Test
	public void test2() throws IOException {
		byte[] match = ("iam_" + CACHE_SESSION + "*").getBytes(Charsets.UTF_8);
		ScanParams params = new ScanParams().count(200).match(match);

		ScanCursor<IamSession> sc = new ScanCursor<IamSession>(cluster, null, params) {
		}.open();
		System.out.println("ScanResult: " + sc);
		while (sc.hasNext()) {
			System.out.println("IamSession: " + sc.next());
		}

	}

}
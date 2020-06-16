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
package com.wl4g.devops.dguid;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.wl4g.devops.dguid.baidu.impl.DefaultUidGenerator;
import com.wl4g.devops.dguid.snowflake.SnowflakeIdWorker;
import com.wl4g.devops.dguid.strategy.SnowflakeStrategy;
import com.wl4g.devops.dguid.worker.SimpleWorkerIdAssigner;

public class ParseUidTest {

	public static void main(String[] args) {
		testBuildRule();
		try {
			// testZkNode();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void testParseUid() {
		System.out.println(new SnowflakeIdWorker(10, 12).nextId());
		System.out.println(new SnowflakeIdWorker(10, 12).parseUID("91543914239533056"));
		System.out.println(new SnowflakeIdWorker(10, 12).parseUID(91543914239533056L));

		// 百度
		DefaultUidGenerator uidGenerator = new DefaultUidGenerator();
		uidGenerator.setWorkerIdAssigner(new SimpleWorkerIdAssigner());
		uidGenerator.setSeqBits(13);
		uidGenerator.setTimeBits(29);
		uidGenerator.setWorkerBits(21);
		uidGenerator.setEpochStr("2017-12-25");
		try {
			uidGenerator.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(uidGenerator.getUID());
		System.out.println(uidGenerator.parseUID(374964732433530880L));
	}

	public static void testBuildRule() {
		Long did = SnowflakeStrategy.getMachineNum(31L);
		System.out.println(did);
		System.out.println(SnowflakeStrategy.getProcessNum(did, 31L));
		System.out.println(getMachineNum(31L));
	}

	private static long getMachineNum(long maxWorkerId) {
		StringBuilder sb = new StringBuilder();
		Enumeration<NetworkInterface> e = null;
		try {
			e = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		while (e.hasMoreElements()) {
			NetworkInterface ni = e.nextElement();
			sb.append(ni.toString());
		}
		return (sb.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
	}

}
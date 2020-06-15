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
package com.wl4g.devops.dguid.baidu;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:uid/zk-uid-baidu.xml" })
public class UidTest {
	@Autowired
	private UidGenerator uidOne;

	@Autowired
	private UidGenerator uidTwo;

	@Test
	public void test() {
		// Generate UID
		long uid = uidOne.getUID();
		System.out.println("one:" + uid);
		System.out.println("one:" + uidOne.getUID());
		// Parse UID into [Timestamp, WorkerId, Sequence]
		// {"UID":"180363646902239241","parsed":{ "timestamp":"2017-01-19
		// 12:15:46", "workerId":"4", "sequence":"9" }}
		System.out.println("one:" + uidOne.parseUID(uid));

		Runnable threadOne = new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 10; i++) {
					System.out.println("one:" + uidOne.getUID());
				}
			}
		};

		Runnable threadTwo = new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < 10; i++) {
					System.out.println("two:" + uidTwo.getUID());
				}
			}
		};
		threadOne.run();
		threadTwo.run();

		uid = uidTwo.getUID();
		System.out.println("two:" + uid);
		System.out.println("two:" + uidTwo.getUID());
		// Parse UID into [Timestamp, WorkerId, Sequence]
		// {"UID":"180363646902239241","parsed":{ "timestamp":"2017-01-19
		// 12:15:46", "workerId":"4", "sequence":"9" }}
		System.out.println("two:" + uidTwo.parseUID(uid));
	}
}
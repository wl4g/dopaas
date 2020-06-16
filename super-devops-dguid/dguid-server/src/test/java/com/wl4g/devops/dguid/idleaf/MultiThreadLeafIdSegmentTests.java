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
package com.wl4g.devops.dguid.idleaf;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.wl4g.devops.dguid.leaf.DefaultLeafIdSegmentHandler;

/**
 * Segment多线程并发测试
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年6月15日
 * @since
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/test/resources/idleaf/app-leaf.xml" })
public class MultiThreadLeafIdSegmentTests extends Thread {

	@Autowired
	private DefaultLeafIdSegmentHandler segmentHandler;

	@Test
	public void multiThreadLeafIdSegmentGenerateTest1() {
		// 开13，23，43个线程进行测试,step设置为10
		ExecutorService executor = Executors.newFixedThreadPool(13);
		executor.submit(new LeafIdTestTask());

	}

	class LeafIdTestTask extends Thread {
		@Override
		public void run() {
			System.out.println(getName() + " - generateId: " + segmentHandler.getId());
		}
	}

}
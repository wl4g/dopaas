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
package com.wl4g.devops.components.tools.common.id;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.wl4g.devops.components.tools.common.id.SnowflakeIdGenerator;

/**
 * {@link SnowflakeIdGeneratorTests}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年6月10日
 * @since
 */
public class SnowflakeIdGeneratorTests {

	public static void main(String[] args) {
		simpleGenerateTest1();
		concurrentGenerateCostTest2();
	}

	public static void simpleGenerateTest1() {
		final SnowflakeIdGenerator idGen = SnowflakeIdGenerator.getDefault();
		for (int i = 0; i < 100; i++) {
			System.out.println(idGen.nextId());
		}
	}

	public static void concurrentGenerateCostTest2() {
		final SnowflakeIdGenerator idGen = SnowflakeIdGenerator.getDefault();
		long avg = 0, runCount = 10, idGenCount = 100_0000;
		for (long count = 0; count < runCount; count++) {
			List<Callable<Long>> taskParts = new ArrayList<>();
			for (long i = 0; i < idGenCount; i++) {
				taskParts.add(new Callable<Long>() {
					@Override
					public Long call() throws Exception {
						return idGen.nextId();
					}
				});
			}
			ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			try {
				long s = System.currentTimeMillis();
				executor.invokeAll(taskParts, 10_000, TimeUnit.SECONDS);
				long s_avg = System.currentTimeMillis() - s;
				avg += s_avg;
				System.out.println("第" + count + "次完成所需时间: " + s_avg / 1.0e3 + "秒");
				executor.shutdown();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("总共运行" + runCount + "次，每次生成" + idGenCount + "个ID，平均完成时间需要: " + avg / 10 / 1.0e3 + "秒");
	}

}
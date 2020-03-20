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
package com.wl4g.devops.tool.common.task;

import static java.lang.System.currentTimeMillis;

import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SafeEnhancedScheduledTaskExecutorTests {

	public static void main(String[] args) throws Exception {
		scheduleQueueRejectedTest1();
		// scheduleWithFixedErrorInterruptedTest2();
		// scheduleWithRandomErrorInterruptedTest3();
	}

	public static void scheduleQueueRejectedTest1() throws Exception {
		// Create ScheduledTaskExecutor.
		SafeEnhancedScheduledTaskExecutor executor = createSafeEnhancedScheduledExecutor(2);

		for (int i = 0; i < 20; i++) {
			final String idStr = "testjob-" + i;
			executor.submit(new Runnable() {
				private String id = idStr;

				@Override
				public void run() {
					try {
						System.out.println("Starting... testjob-" + id);
						Thread.sleep(3000L);
						System.out.println("Completed. testjob-" + id);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}

		// executor.shutdown();
	}

	public static void scheduleWithFixedErrorInterruptedTest2() throws Exception {
		// Create ScheduledTaskExecutor.
		SafeEnhancedScheduledTaskExecutor executor = createSafeEnhancedScheduledExecutor(2);

		// Task1(Error):
		executor.scheduleAtFixedRate(() -> {
			System.out.println(currentTimeMillis() + " - Error of task1..." + executor);
			throw new RuntimeException(currentTimeMillis() + " - Error of task1...");
		}, 1, 2, TimeUnit.SECONDS);

		// Task2(Normal):
		executor.scheduleAtFixedRate(() -> {
			System.out.println(currentTimeMillis() + " - Normal of task2..." + executor);
		}, 1, 2, TimeUnit.SECONDS);

		// Task3(Normal):
		executor.scheduleAtFixedRate(() -> {
			System.out.println(currentTimeMillis() + " - Normal of task3..." + executor);
		}, 1, 2, TimeUnit.SECONDS);

		// executor.shutdown();
	}

	public static void scheduleWithRandomErrorInterruptedTest3() throws Exception {
		// Create ScheduledTaskExecutor.
		SafeEnhancedScheduledTaskExecutor executor = createSafeEnhancedScheduledExecutor(2);

		// Task1(Error):
		executor.scheduleAtRandomRate(() -> {
			System.out.println(currentTimeMillis() + " - Error of task1..." + executor);
			throw new RuntimeException(currentTimeMillis() + " - Error of task1...");
		}, 1, 1, 2, TimeUnit.SECONDS);

		// Task2(Normal):
		executor.scheduleAtRandomRate(() -> {
			System.out.println(currentTimeMillis() + " - Normal of task2..." + executor);
		}, 1, 1, 6, TimeUnit.SECONDS);

		// Task3(Normal):
		executor.scheduleAtRandomRate(() -> {
			System.out.println(currentTimeMillis() + " - Normal of task3..." + executor);
		}, 1, 1, 6, TimeUnit.SECONDS);

	}

	private static SafeEnhancedScheduledTaskExecutor createSafeEnhancedScheduledExecutor(int concurrencyPoolSize)
			throws Exception {
		return new SafeEnhancedScheduledTaskExecutor(concurrencyPoolSize, 0L, Executors.defaultThreadFactory(), 2,
				new RejectedExecutionHandler() {
					@Override
					public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
						System.err.println("ERROR ==>> " + r);
					}
				});
	}

}

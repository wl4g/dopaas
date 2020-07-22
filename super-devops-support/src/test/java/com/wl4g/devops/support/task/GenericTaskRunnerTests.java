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
package com.wl4g.devops.support.task;

import static java.lang.System.currentTimeMillis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.wl4g.devops.components.tools.common.task.RunnerProperties;
import com.wl4g.devops.support.task.ApplicationTaskRunner;

/**
 * {@link ApplicationTaskRunner} tests.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年6月2日
 * @since
 */
public class GenericTaskRunnerTests {

	public static void main(String[] args) throws Exception {
		// submitForCompleteTest1();
		// scheduleQueueRejectedTest2();
		scheduleWithFixedErrorInterruptedTest3();
		// scheduleWithRandomErrorInterruptedTest4();
	}

	@SuppressWarnings({ "rawtypes" })
	public static void submitForCompleteTest1() throws Exception {
		// Add testing jobs.
		List<Runnable> jobs = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			final String idStr = "testjob-" + i;
			jobs.add(new Runnable() {
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

		// Create runner.
		ApplicationTaskRunner runner = createGenericTaskRunner(2);
		System.out.println(runner);

		// Submit jobs & listen job timeout.
		runner.getWorker().submitForComplete(jobs, (ex, completed, uncompleted) -> {
			ex.printStackTrace();
			System.out.println(String.format("Completed: %s, uncompleted sets: %s", completed, uncompleted));
		}, 4 * 1000l); // > 3*3000

		// runner.close();
	}

	@SuppressWarnings({ "rawtypes" })
	public static void scheduleQueueRejectedTest2() throws Exception {
		// Create runner.
		ApplicationTaskRunner runner = createGenericTaskRunner(2);

		for (int i = 0; i < 100; i++) {
			final String idStr = "testjob-" + i;
			runner.getWorker().submit(new Runnable() {
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

		// runner.close();
	}

	@SuppressWarnings({ "rawtypes" })
	public static void scheduleWithFixedErrorInterruptedTest3() throws Exception {
		// Create runner.
		ApplicationTaskRunner runner = createGenericTaskRunner(2);

		// Task1(Error):
		runner.getWorker().scheduleAtFixedRate(() -> {
			System.out.println(currentTimeMillis() + " - Error of task1..." + runner.getWorker());
			throw new RuntimeException(currentTimeMillis() + " - Error of task1...");
		}, 1, 2, TimeUnit.SECONDS);

		// Task2(Normal):
		runner.getWorker().scheduleAtFixedRate(() -> {
			System.out.println(currentTimeMillis() + " - Normal of task2..." + runner.getWorker());
		}, 1, 2, TimeUnit.SECONDS);

		// Task3(Normal):
		runner.getWorker().scheduleAtFixedRate(() -> {
			System.out.println(currentTimeMillis() + " - Normal of task3..." + runner.getWorker());
		}, 1, 2, TimeUnit.SECONDS);

		// runner.close();
	}

	@SuppressWarnings({ "rawtypes" })
	public static void scheduleWithRandomErrorInterruptedTest4() throws Exception {
		// Create runner.
		ApplicationTaskRunner runner = createGenericTaskRunner(2);

		// Task1(Error):
		runner.getWorker().scheduleAtRandomRate(() -> {
			System.out.println(currentTimeMillis() + " - Error of task1..." + runner.getWorker());
			throw new RuntimeException(currentTimeMillis() + " - Error of task1...");
		}, 1, 1, 2, TimeUnit.SECONDS);

		// Task2(Normal):
		runner.getWorker().scheduleAtRandomRate(() -> {
			System.out.println(currentTimeMillis() + " - Normal of task2..." + runner.getWorker());
		}, 1, 1, 6, TimeUnit.SECONDS);

		// Task3(Normal):
		runner.getWorker().scheduleAtRandomRate(() -> {
			System.out.println(currentTimeMillis() + " - Normal of task3..." + runner.getWorker());
		}, 1, 1, 6, TimeUnit.SECONDS);

		// runner.close();
	}

	private static ApplicationTaskRunner<RunnerProperties> createGenericTaskRunner(int concurrencyPoolSize) throws Exception {
		ApplicationTaskRunner<RunnerProperties> runner = new ApplicationTaskRunner<RunnerProperties>(
				new RunnerProperties(false, concurrencyPoolSize)) {
		};
		runner.run(null);
		return runner;
	}

}
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

import com.wl4g.devops.support.task.GenericTaskRunner;
import com.wl4g.devops.support.task.RunnerProperties;

/**
 * {@link GenericTaskRunner} tests.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年6月2日
 * @since
 */
public class GenericTaskRunnerTests {

	public static void main(String[] args) throws Exception {
		// submitForCompleteTest1();
		// scheduleQueueRejectedTest2();
		// scheduleWithFixedTest3();
		scheduleWithRandomTest4();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		GenericTaskRunner runner = new GenericTaskRunner<RunnerProperties>(new RunnerProperties(false, 2)) {
		};
		runner.run(null);
		System.out.println(runner);

		// Submit jobs & listen job timeout.
		runner.submitForComplete(jobs, (ex, completed, uncompleted) -> {
			ex.printStackTrace();
			System.out.println(String.format("Completed: %s, uncompleted sets: %s", completed, uncompleted));
		}, 4 * 1000l); // > 3*3000

		// runner.close();
	}

	@SuppressWarnings({ "rawtypes", "resource" })
	public static void scheduleQueueRejectedTest2() throws Exception {
		// Create runner.
		GenericTaskRunner runner = new GenericTaskRunner<RunnerProperties>(new RunnerProperties(false, 2)) {
		};
		runner.run(null);

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

	@SuppressWarnings({ "rawtypes", "resource" })
	public static void scheduleWithFixedTest3() throws Exception {
		// Create runner.
		GenericTaskRunner runner = new GenericTaskRunner<RunnerProperties>(new RunnerProperties(false, 2)) {
		};
		runner.run(null);

		runner.getWorker().scheduleAtFixedRate(() -> {
			System.out.println(currentTimeMillis() + " - Fixed time task...");
		}, 500, 1000, TimeUnit.MILLISECONDS);

		// runner.close();
	}

	@SuppressWarnings({ "rawtypes", "resource" })
	public static void scheduleWithRandomTest4() throws Exception {
		// Create runner.
		GenericTaskRunner runner = new GenericTaskRunner<RunnerProperties>(new RunnerProperties(false, 2)) {
		};
		runner.run(null);

		runner.scheduleAtRandomRate(() -> {
			System.out.println(currentTimeMillis() + " - Random time task...");
		}, 500, 1, 6, TimeUnit.SECONDS);

		// runner.close();
	}

}
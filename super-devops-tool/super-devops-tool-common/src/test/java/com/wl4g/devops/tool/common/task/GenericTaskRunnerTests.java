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

import java.util.ArrayList;
import java.util.List;

import com.wl4g.devops.tool.common.task.GenericTaskRunner;
import com.wl4g.devops.tool.common.task.NamedIdJob;
import com.wl4g.devops.tool.common.task.RunnerProperties;

/**
 * {@link GenericTaskRunner} tests.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年6月2日
 * @since
 */
public class GenericTaskRunnerTests {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws Exception {
		// Add testing jobs.
		List<NamedIdJob> jobs = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			jobs.add(new NamedIdJob("testjob-" + i) {
				@Override
				public void run() {
					try {
						System.out.println("Starting... testjob-" + getId());
						Thread.sleep(3000L);
						System.out.println("Completed. testjob-" + getId());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}

		// Create runner.
		GenericTaskRunner runner = new GenericTaskRunner<RunnerProperties>(new RunnerProperties(false, 2)) {
		};
		runner.initRunner();
		System.out.println(runner);

		// Submit jobs & listen job timeout.
		runner.submitForComplete(jobs, (ex, completed, uncompleted) -> {
			ex.printStackTrace();
			System.out.println(String.format("Completed: %s, uncompleted sets: %s", completed, uncompleted));
		}, 4 * 1000l); // > 3*3000

	}

}
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
package com.wl4g.devops.shell.console;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.wl4g.devops.shell.annotation.ShellComponent;
import com.wl4g.devops.shell.annotation.ShellMethod;
import com.wl4g.devops.shell.annotation.ShellOption;
import com.wl4g.devops.shell.console.args.MixedArgument;
import com.wl4g.devops.shell.console.args.SumArgument;
import com.wl4g.devops.shell.handler.ShellContext;
import com.wl4g.devops.shell.service.ExampleService;

/**
 * Example console.</br>
 * Note: for the shell program to take effect, you must inject {@link Component}
 * or {@link Service} or {@link Bean} into the spring
 * {@link ApplicationContext}.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-04-21
 * @since
 */
@Component
@ShellComponent
public class ExampleConsole {

	final public static String GROUP_NAME = "Example commands";

	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private ExampleService exampleService;

	/**
	 * For example: $> sum -a 1 -b 123
	 */
	@ShellMethod(keys = "sum", group = GROUP_NAME, help = "Shell method tests that do not output results")
	public void sum(SumArgument arg) {
		exampleService.add(arg);
	}

	/**
	 * For example: $> sum2 -a 1 -b 123
	 */
	@ShellMethod(keys = "sum2", group = GROUP_NAME, help = "Test of shell method that can output results")
	public void sum2(ShellContext context, @ShellOption(opt = "a", lopt = "add1", help = "Add number") int a,
			@ShellOption(opt = "b", lopt = "add2", help = "Added number", defaultValue = "1") int b) {
		context.printf(exampleService.add(new SumArgument(a, b)).toString()).completed();
	}

	/**
	 * For example: $> set -l 1,2 -s x3,x4
	 */
	@ShellMethod(keys = "set", group = GROUP_NAME, help = "Complex parameter injection testing")
	public void set(ShellContext context,
			@ShellOption(opt = "s", lopt = "set", help = "Set<String> type argument field") Set<String> set1,
			@ShellOption(opt = "l", lopt = "list", help = "List<Integer> type argument field") List<Integer> list) {
		context.printf("Direct mixed set parameter injection results: set=" + set1 + ", list=" + list).completed();
	}

	/**
	 * For example: $> mixed -l x1,x2 -m a1=b1,a2=b2 -p aa1=bb1,aa2=bb2 -s x3,x4
	 * -e false -E true
	 */
	@ShellMethod(keys = "mixed", group = GROUP_NAME, help = "Mixed parameter injection testing")
	public void mixed(ShellContext context, MixedArgument arg) {
		context.printf("Bean field mixed set parameter injection test results: " + arg.toString()).completed();
	}

	/**
	 * For example: $> log -n 20
	 */
	@ShellMethod(keys = "log", group = GROUP_NAME, help = "This is a shell method for printing logs asynchronously.(Not support interrupt)")
	public void log(
			@ShellOption(opt = "n", lopt = "num", required = false, defaultValue = "5", help = "Number of printed messages") int num,
			ShellContext context) {

		Executors.newSingleThreadExecutor().execute(() -> {
			try {
				context.printf("Log print...");

				for (int i = 1; i <= num; i++) {
					String message = "This is the " + i + "th message!";
					System.out.println(message);

					// Print stream message
					context.printf(message);

					try {
						Thread.sleep(200L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				context.printf("Print successfully completed!");

			} finally {
				// After execution, the client console will automatically
				// refresh the prompt.
				context.completed();
			}
		});
	}

	/**
	 * For example: $> log2 -n 20
	 */
	@ShellMethod(keys = "log2", group = GROUP_NAME, help = "This is a shell method for printing logs asynchronously.(Support interrupt)")
	public void log2(
			@ShellOption(opt = "n", lopt = "num", required = false, defaultValue = "5", help = "Number of printed messages") int num,
			@ShellOption(opt = "s", lopt = "sleep", required = false, defaultValue = "100", help = "Print message delay(ms)") long sleep,
			ShellContext context) {

		Executors.newSingleThreadExecutor().execute(() -> {
			try {
				context.printf("Log2 print...");

				for (int i = 1; !context.isInterrupted() && i <= num; i++) {
					String message = "This is the " + i + "th message!";
					System.out.println(message);

					// Print message to client.
					// ShellHolder.currentPrintf(message);
					context.printf(message, num, i);

					try {
						Thread.sleep(sleep);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				context.printf("Log2 print finished!");

			} finally {
				// After execution, the client console will automatically
				// refresh the prompt.
				context.completed();
			}
		});
	}

	/**
	 * For example: $> log3 -n 20
	 */
	@ShellMethod(keys = "log3", group = GROUP_NAME, help = "This is a simple shell method for printing logs synchronously.(Support interrupt)")
	public void log3(
			@ShellOption(opt = "n", lopt = "num", required = false, defaultValue = "5", help = "Number of printed messages") int num,
			@ShellOption(opt = "s", lopt = "sleep", required = false, defaultValue = "100", help = "Print message delay(ms)") long sleep,
			ShellContext context) {

		context.printf("Log3 print...");

		for (int i = 1; !context.isInterrupted() && i <= num; i++) {
			String message = "This is the " + i + "th message!";
			System.out.println(message);

			// Print message to client.
			// ShellHolder.currentPrintf(message);
			context.printf(message, num, i);

			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		context.printf("Log3 print finished!").completed(); // MARK1
	}

}
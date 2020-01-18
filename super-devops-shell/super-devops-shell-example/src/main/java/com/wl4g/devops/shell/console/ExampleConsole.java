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
import com.wl4g.devops.shell.console.args.SumResult;
import com.wl4g.devops.shell.processor.ShellHolder;
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
	@ShellMethod(keys = "sum", group = GROUP_NAME, help = "This is an example method of summation. The parameter list is not the base type.")
	public SumResult sum(SumArgument arg) {
		return exampleService.add(arg);
	}

	/**
	 * For example: $> sum2 -a 1 -b 123
	 */
	@ShellMethod(keys = "sum2", group = GROUP_NAME, help = "This is an example method of summation. The parameter list is the basic type.")
	public SumResult sum2(@ShellOption(opt = "a", lopt = "add1", help = "Add number") int a,
			@ShellOption(opt = "b", lopt = "add2", help = "Added number", defaultValue = "1") int b) {
		return exampleService.add(new SumArgument(a, b));
	}

	/**
	 * For example: $> set -l 1,2 -s x3,x4
	 */
	@ShellMethod(keys = "set", group = GROUP_NAME, help = "Direct set parameter injection testing")
	public String set(@ShellOption(opt = "s", lopt = "set", help = "Set<String> type argument field") Set<String> set1,
			@ShellOption(opt = "l", lopt = "list", help = "List<Integer> type argument field") List<Integer> list) {
		return "Direct mixed set parameter injection results: set=" + set1 + ", list=" + list;
	}

	/**
	 * For example: $> mixed -l x1,x2 -m a1=b1,a2=b2 -p aa1=bb1,aa2=bb2 -s x3,x4
	 * -e false -E true
	 */
	@ShellMethod(keys = "mixed", group = GROUP_NAME, help = "Mixed set type parameter injection testing")
	public String mixed(MixedArgument arg) {
		return "Bean field mixed set parameter injection test results: " + arg.toString();
	}

	/**
	 * For example: $> log -n 100
	 */
	@ShellMethod(keys = "log", group = GROUP_NAME, help = "This is a shell command that print logs in real-time.(Interruption is not supported)")
	public String log(
			@ShellOption(opt = "n", lopt = "num", required = false, defaultValue = "5", help = "Physical number of printed items") int num) {

		// Open the flow message output, and the client will always be
		// blocked waiting until ShellConsoles.currentClose() is called.
		ShellHolder.currentOpen();

		// Used to simulate an asynchronous task, constantly outputting logs
		new Thread(() -> {
			try {
				for (int i = 1; i <= num; i++) {
					String message = "This is the " + i + "th message!";
					System.out.println(message);

					// Print stream message
					ShellHolder.currentPrintf(message);
				}
				ShellHolder.currentPrintf("Print successfully completed!");

			} finally {
				// Must end, and must be after ShellConsoles.currentOpen()
				ShellHolder.currentClose();
			}
		}).start();

		return "Logs printer starting-up ...";
	}

	/**
	 * For example: $> log2 -n 100
	 */
	@ShellMethod(keys = "logs2", group = GROUP_NAME, help = "This is a shell command that print logs in real-time.(Interruption is supported)")
	public String log2(
			@ShellOption(opt = "n", lopt = "num", required = false, defaultValue = "5", help = "Physical number of printed items") int num,
			@ShellOption(opt = "s", lopt = "sleep", required = false, defaultValue = "100", help = "Print sleep interval(ms)") long sleep) {

		// Open the flow message output, and the client will always be
		// blocked waiting until ShellConsoles.currentClose() is called.
		ShellHolder.currentOpen();

		new Thread(() -> {
			try {
				for (int i = 1; !ShellHolder.currentInterrupted() && i <= num; i++) {
					String message = "This is the " + i + "th message!";
					System.out.println(message);

					// Print message to client.
					// ShellHolder.currentPrintf(message);
					ShellHolder.currentPrintf(message, 100, i);

					try {
						Thread.sleep(sleep);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				ShellHolder.currentPrintf("Print finished!");

			} finally {
				// Must end, and must be after ShellConsoles.currentOpen()
				ShellHolder.currentClose();
			}
		}).start();

		return "Logs printer starting-up ...";
	}

}
/*
 * Copyright 2015 the original author or authors.
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
package com.wl4g.devops.shell.exporter;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.devops.shell.annotation.ShellComponent;
import com.wl4g.devops.shell.annotation.ShellMethod;
import com.wl4g.devops.shell.annotation.ShellOption;
import com.wl4g.devops.shell.bean.ChaosTypeArgument;
import com.wl4g.devops.shell.bean.SumArgument;
import com.wl4g.devops.shell.bean.SumResult;
import com.wl4g.devops.shell.service.ExampleService;

@ShellComponent
public class ExampleExporter {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private ExampleService exampleService;

	@ShellMethod(keys = {
			"sumTest" }, group = "Example command", help = "This is an example method of summation. The parameter list is not the base type.")
	public SumResult sumTest(SumArgument arg) {
		return exampleService.add(arg);
	}

	@ShellMethod(keys = {
			"sumTest2" }, group = "Example command", help = "This is an example method of summation. The parameter list is the basic type.")
	public SumResult sum2Test(@ShellOption(opt = "a", lopt = "add1", help = "Add number") int a,
			@ShellOption(opt = "b", lopt = "add2", help = "Added number (default: 1)", defaultValue = "1") int b) {
		return exampleService.add(new SumArgument(a, b));
	}

	/**
	 * $> setTest -l 1,2 -s x3,x4
	 */
	@ShellMethod(keys = { "setTest" }, group = "Example command", help = "Direct set parameter injection testing")
	public String setTest(@ShellOption(opt = "s", lopt = "set", help = "Set<String>类型参数字段") Set<String> set1,
			@ShellOption(opt = "l", lopt = "list", help = "List<Integer>类型参数字段") List<Integer> list) {
		return "Direct mixed set parameter injection results: set=" + set1 + ", list=" + list;
	}

	/**
	 * $> mixedTest -l x1,x2 -m a1=b1,a2=b2 -p aa1=bb1,aa2=bb2 -s x3,x4
	 */
	@ShellMethod(keys = { "mixedTest" }, group = "Example command", help = "Mixed set type parameter injection testing")
	public String mixedTest(ChaosTypeArgument arg) {
		return "Bean field mixed set parameter injection test results: " + arg.toString();
	}

}

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.devops.shell.annotation.ShellComponent;
import com.wl4g.devops.shell.annotation.ShellMethod;
import com.wl4g.devops.shell.annotation.ShellOption;
import com.wl4g.devops.shell.bean.AdditionArgument;
import com.wl4g.devops.shell.bean.AdditionResult;
import com.wl4g.devops.shell.service.ExampleService;

@ShellComponent
public class ExampleExporter {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private ExampleService exampleService;

	@ShellMethod(keys = { "add" }, group = "Example command", help = "Addition (Shell method using java bean parameters)")
	public AdditionResult add(AdditionArgument add) {
		return exampleService.add(add);
	}

	@ShellMethod(keys = { "add1" }, group = "Example command", help = "Addition (Shell method using native parameters)")
	public AdditionResult add1(@ShellOption(opt = "a", lopt = "add1", help = "加数") int a,
			@ShellOption(opt = "b", lopt = "add2", help = "被加数（默认：1）", defaultValue = "1") int b) {
		return exampleService.add(new AdditionArgument(a, b));
	}

}

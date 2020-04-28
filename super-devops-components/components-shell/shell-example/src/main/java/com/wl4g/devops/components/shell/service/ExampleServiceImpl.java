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
package com.wl4g.devops.components.shell.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.wl4g.devops.components.shell.console.args.SumArgument;
import com.wl4g.devops.components.shell.console.args.SumResult;

@Service
public class ExampleServiceImpl implements ExampleService {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public SumResult add(SumArgument add) {
		int sum = add.getAdd1() + add.getAdd2();
		System.out.println("计算结果>>>... +" + sum);
		return new SumResult(sum);
	}

}
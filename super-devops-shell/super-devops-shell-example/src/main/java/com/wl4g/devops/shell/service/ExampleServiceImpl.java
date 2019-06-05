/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.shell.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.wl4g.devops.shell.bean.SumArgument;
import com.wl4g.devops.shell.bean.SumResult;

@Service
public class ExampleServiceImpl implements ExampleService {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public SumResult add(SumArgument add) {
		int sum = add.getAdd1() + add.getAdd2();
		log.info("计算结果>>>... {}", sum);
		return new SumResult(sum);
	}

}
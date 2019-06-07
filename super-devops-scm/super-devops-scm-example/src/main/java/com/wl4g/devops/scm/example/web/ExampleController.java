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
package com.wl4g.devops.scm.example.web;

import com.wl4g.devops.scm.example.service.ExampleService;
import com.wl4g.devops.scm.example.service.ExampleService2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ExampleController {
	final private static Logger log = LoggerFactory.getLogger(ExampleController.class);

	@Autowired
	private ExampleService exampleService;

	@Autowired
	private ExampleService2 exampleService2;

	/*@Autowired
	private ServerTokenClient serverTokenClient;*/

	@Value("#{'${spring.application.name}'}")
	private String from;


	@RequestMapping("start")
	public String start() {
		log.info("ExampleService Request starting... " + exampleService);
		this.exampleService.start();
		return "ExampleService Started";
	}

	@RequestMapping("stop")
	public String stop() {
		log.info("ExampleService Request stoping... ");
		this.exampleService.stop();
		return "ExampleService Stoped";
	}

	@RequestMapping("start2")
	public String start2() {
		log.info("ExampleService2 Request starting... " + exampleService);
		this.exampleService2.start();
		return "ExampleService2 Started";
	}

	@RequestMapping("stop2")
	public String stop2() {
		log.info("ExampleService2 Request stoping... ");
		this.exampleService2.stop();
		return "ExampleService2 Stoped";
	}


	@RequestMapping("token")
	public String token() {
		log.info("ExampleService2 Request stoping... ");
		String to = "scm";
		//return serverTokenClient.getToken(from,to);
		return null;
	}

}
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
package com.wl4g.devops.iam.example.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wl4g.devops.iam.client.annotation.SecondaryAuthenticate;
import com.wl4g.devops.iam.example.authc.ExampleSecondaryAuthenticator;

/**
 * {@link ExampleSensitiveController}
 *
 * This is a sensitive interface example controller to demonstrate how to use
 * secondary authentication to protect sensitive APIs
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年4月24日
 * @since
 */
@Controller
@RequestMapping("/sensitive/")
public class ExampleSensitiveController {
	final private Logger log = LoggerFactory.getLogger(getClass());

	@RequestMapping("sensitiveApi1")
	@SecondaryAuthenticate(funcId = "funSensitiveApi", handleClass = ExampleSecondaryAuthenticator.class)
	@ResponseBody
	public String sensitiveApi(String name, HttpServletRequest request, HttpServletResponse response) {
		log.info("Request sensitiveApi... {}", name);
		return "Sensitive api processed successfully!";
	}

}
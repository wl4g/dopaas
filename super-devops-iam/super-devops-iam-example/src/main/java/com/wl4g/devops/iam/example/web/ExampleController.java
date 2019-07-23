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
package com.wl4g.devops.iam.example.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wl4g.devops.iam.client.annotation.SecondAuthenticate;
import com.wl4g.devops.iam.client.session.mgt.IamClientSessionManager;
import com.wl4g.devops.iam.example.ExampleClientSecurityCoprocessor;
import com.wl4g.devops.iam.example.handler.TestSecondAuthenticateHandler;
import com.wl4g.devops.iam.example.service.ExampleService;

@Controller
@RequestMapping("/public/")
public class ExampleController {
	final private static Logger log = LoggerFactory.getLogger(ExampleController.class);

	@Autowired
	private ExampleService exampleService;

	@Autowired
	private IamClientSessionManager manager;

	@RequestMapping("validateSessions")
	@ResponseBody
	public String validateSessions() {
		log.info("Request validateSessions... ");
		manager.validateSessions();
		return "ok";
	}

	@RequestMapping("test1")
	@ResponseBody
	public String test1(String name) {
		log.info("Request test1... {}", name);
		this.exampleService.test1(name);
		return "ok";
	}

	@RequiresPermissions(value = { "order:view:test2", "order:edit:test1" }, logical = Logical.OR)
	@RequestMapping("test2")
	@ResponseBody
	public String test2(String name) {
		log.info("Request test2... {}", name);
		return "ok";
	}

	@RequiresPermissions(value = { "order:view:test2", "order:edit:test3" }, logical = Logical.AND)
	@RequestMapping("test3")
	@ResponseBody
	public String test3(String name) {
		log.info("Request test3... {}", name);
		return "ok";
	}

	@RequiresPermissions(value = { "order:view:test3", "order:edit:*" }, logical = Logical.OR)
	@RequestMapping("test4")
	@ResponseBody
	public String test4(String name) {
		log.info("Request test4... {}", name);
		return "ok";
	}

	@RequiresPermissions(value = { "order:view:test3", "order:edit:*" }, logical = Logical.AND)
	@RequestMapping("test5")
	@ResponseBody
	public String test5(String name) {
		log.info("Request test5... {}", name);
		return "ok";
	}

	@RequestMapping("sensitiveApi")
	@SecondAuthenticate(funcId = "FunSensitiveApi", handleClass = TestSecondAuthenticateHandler.class)
	@ResponseBody
	public String sensitiveApi(String name, HttpServletRequest request, HttpServletResponse response) {
		log.info("Request sensitiveApi... {}", name);
		return "ok";
	}

	/*
	 * Example index page.
	 */
	@RequestMapping("index")
	public String indexView(HttpServletRequest request, HttpServletResponse response) {
		// response.setHeader("Access-Control-Allow-Origin", "*");
		String exampleKey1 = (String) SecurityUtils.getSubject().getSession()
				.getAttribute(ExampleClientSecurityCoprocessor.KEY_EXAMPLE_STORE_IN_SESSION);
		System.out.println("+++>>>" + exampleKey1);
		return "index";
	}

}
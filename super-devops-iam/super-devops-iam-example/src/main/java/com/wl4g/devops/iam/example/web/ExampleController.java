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

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wl4g.devops.iam.client.session.mgt.IamClientSessionManager;
import com.wl4g.devops.iam.common.subject.IamPrincipalInfo;
import com.wl4g.devops.iam.common.utils.IamSecurityHolder;
import com.wl4g.devops.iam.example.authc.ExampleClientSecurityCoprocessor;
import com.wl4g.devops.iam.example.service.ExampleService;

@Controller
@RequestMapping("/example/")
public class ExampleController {
	final private Logger log = LoggerFactory.getLogger(getClass());

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

	@RequestMapping("test6")
	@ResponseBody
	public String test6(HttpServletRequest request) {
		String encryptedMobilePhone = request.getParameter("encryptedMobilePhone");
		log.info("Request test6... encryptedMobilePhone: {}", encryptedMobilePhone);
		return encryptedMobilePhone;
	}

	@RequestMapping("test7")
	@ResponseBody
	public String test7(HttpServletRequest request) {
		IamPrincipalInfo info = IamSecurityHolder.getPrincipalInfo();
		log.info("Request test7... currentPrincipalInfo: {}", info);
		log.info("Request test7... currentPrincipalInfo.roles: {}", info.getRoles());
		log.info("Request test7... currentPrincipalInfo.permissions: {}", info.getPermissions());
		log.info("Request test7... currentPrincipalInfo.organization: {}", info.getOrganization());
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
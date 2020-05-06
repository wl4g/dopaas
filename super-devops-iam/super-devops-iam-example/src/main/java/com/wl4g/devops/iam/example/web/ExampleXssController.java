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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.iam.common.annotation.UnsafeXss;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/test/")
public class ExampleXssController extends BaseController {

	@GetMapping("xss1")
	@ResponseBody
	public RespBase<?> xss1(String name, HttpServletRequest request, HttpServletResponse response) throws Exception {
		RespBase<Object> resp = RespBase.create();
		System.out.println("On xss1 testing, parameters filtered by xss:\nname>> " + name + "\nrequest>> " + request);
		resp.forMap().put("name", name);
		return resp;
	}

	/**
	 * e.g.
	 *
	 * <pre>
	 * http://localhost:14040/devops-iam/test/xss2?name=&lt;script&gt;alert("fuck")&lt;/script&gt;
	 * </pre>
	 *
	 * @param name
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@GetMapping("xss2")
	@ResponseBody
	public RespBase<?> xss2(@UnsafeXss String name, HttpServletRequest request, HttpServletResponse response) throws Exception {
		RespBase<Object> resp = RespBase.create();
		System.out.println("On xss2 testing, parameters filtered by xss:\nname>> " + name + "\nrequest>> " + request);
		resp.forMap().put("name", name);
		return resp;
	}

}
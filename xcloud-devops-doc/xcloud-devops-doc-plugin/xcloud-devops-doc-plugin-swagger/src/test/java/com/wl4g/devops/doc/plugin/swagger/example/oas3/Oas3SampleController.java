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
package com.wl4g.devops.doc.plugin.swagger.example.oas3;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.components.core.web.versions.annotation.ApiVersion;
import com.wl4g.components.core.web.versions.annotation.ApiVersionMapping;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * {@link Oas3SampleController}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-08
 * @sine v1.0
 * @see
 */
@RestController
@RequestMapping("/demo/oas3")
@Api
public class Oas3SampleController {

	@ApiVersionMapping({ @ApiVersion(groups = "ios", value = "1.0.1") })
	@Operation(summary = "Obtain user info(V1)", tags = { "sys", "user" }, responses = {
			@ApiResponse(responseCode = "200", headers = @Header(name = "X-OAS3-DEMO")) })
	@RequestMapping(value = "getUserInfo", method = RequestMethod.GET)
	public String getUserInfo_V1(@Parameter Long userId) {
		return "This is user content content(V1)...";
	}

	@ApiVersionMapping({ @ApiVersion(groups = "android", value = "1.2.0") })
	@Operation(summary = "Obtain user info(V2)", tags = { "sys", "user" }, responses = {
			@ApiResponse(responseCode = "200", headers = @Header(name = "X-OAS3-DEMO")) })
	@RequestMapping(value = "getUserInfo", method = RequestMethod.GET)
	public String getUserInfo_V2(@Parameter Long userId) {
		return "This is user content content(V2)...";
	}

}
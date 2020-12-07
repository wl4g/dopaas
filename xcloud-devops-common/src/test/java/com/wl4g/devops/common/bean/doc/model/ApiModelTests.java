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
package com.wl4g.devops.common.bean.doc.model;

import static com.wl4g.components.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;
import static java.lang.System.out;

import org.junit.Test;

import com.wl4g.components.common.resource.ResourceUtils2;

/**
 * {@link ApiModelTests}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-07
 * @sine v1.0
 * @see 
 */
public class ApiModelTests {

	final static String DEMO_SWAGGER2_JSON = ResourceUtils2.getResourceString(ApiModelTests.class, "demo-swagger2.json");

	@Test
	public void circularRefSerializeCase1() {
		// out.println(DEMO_SWAGGER2_JSON);

		Swagger2ApiModel model = parseJSON(DEMO_SWAGGER2_JSON, Swagger2ApiModel.class);

		out.println("\n-------- App info: ---------");
		out.println(toJSONString(model.getInfo()));

		out.println("\n-------- Definitions: ---------");
		model.getDefinitions().forEach((defName, defInfo) -> {
			out.println("definitionName: " + defName);
			out.println("definitionInfo: " + toJSONString(defInfo));
		});

		out.println("\n-------- Paths: ---------");
		model.getPaths().forEach((address, pathInfo) -> {
			out.println("address: " + address);
			out.println("pathInfo: " + toJSONString(pathInfo));
		});

		out.println("\n-------- Re-toJSONString: ---------");
		out.println(toJSONString(model));

	}

}

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
package com.wl4g.devops.doc.plugin;

import com.wl4g.devops.doc.plugin.swagger.springfox.oas3.GenerateSpringfoxOas3Mojo;
import com.wl4g.devops.doc.plugin.swagger.springfox.swagger2.GenerateSpringfoxSwagger2Mojo;
import com.wl4g.devops.doc.plugin.swagger.jaxrs2.GenerateJaxrs2Mojo;
import com.wl4g.devops.doc.plugin.swagger.springdoc.GenerateSpringdocMojo;

/**
 * {@link MvnGenerateMojoTests}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-09
 * @sine v1.0
 * @see
 */
@SuppressWarnings("unused")
public class MvnGenerateMojoTests {

	// Simulate mvn plugin execution
	public static void main(String[] args) throws Exception {
		new GenerateSpringfoxSwagger2Mojo().execute();
		// new GenerateSpringfoxOas3Mojo().execute();
		// new GenerateSpringdocMojo().execute();
		// new GenerateJaxrs2Mojo().execute();
	}

}

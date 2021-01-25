/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g;

import com.wl4g.component.data.annotation.EnableComponentDBConfiguration;
import com.wl4g.iam.client.annotation.EnableIamClient;
import com.wl4g.shell.springboot.annotation.EnableShellServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * {@link LocalDevOpsServer}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2021-01-25
 * @sine v1.0
 * @see
 */
@EnableIamClient
@EnableShellServer
@EnableComponentDBConfiguration("com.wl4g.devops.*.data")
@SpringBootApplication
public class LocalDevOpsServer {

	public static void main(String[] args) {
		SpringApplication.run(LocalDevOpsServer.class, args);
	}

}
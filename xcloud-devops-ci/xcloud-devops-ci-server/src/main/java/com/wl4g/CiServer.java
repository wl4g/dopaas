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

import com.wl4g.components.data.annotation.EnableComponentsData;
import com.wl4g.iam.client.annotation.EnableIamClient;
import com.wl4g.shell.springboot.annotation.EnableShellServer;

import static org.springframework.context.annotation.AdviceMode.ASPECTJ;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableIamClient
@EnableShellServer
@MapperScan("com.wl4g.devops.ci.dao")
@EnableComponentsData
@EnableTransactionManagement(mode = ASPECTJ)
@SpringBootApplication
public class CiServer {

	public static void main(String[] args) {
		SpringApplication.run(CiServer.class, args);
	}

}
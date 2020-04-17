package com.wl4g.devops.components.webjars;
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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * {@link WebjarsExample} </br>
 * 
 * <pre>
 * http://127.0.0.1:14070/webjars-example/webide/groovy.html
 * </pre>
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年4月18日 v1.0.0
 * @see
 */
@SpringBootApplication
public class WebjarsExample {

	public static void main(String[] args) {
		SpringApplication.run(WebjarsExample.class, args);
	}

}
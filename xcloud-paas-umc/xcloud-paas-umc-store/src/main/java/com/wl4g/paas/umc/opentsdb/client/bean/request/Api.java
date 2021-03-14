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
package com.wl4g.paas.umc.opentsdb.client.bean.request;

/**
 * api地址
 *
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2019/2/23 下午12:49
 * @Version: 1.0
 */
public enum Api {

	/***
	 * path对应api地址
	 */
	PUT("/api/put"), PUT_DETAIL("/api/put?details=true"), QUERY("/api/query"), LAST("/api/query/last"), SUGGEST("/api/suggest");

	private String path;

	Api(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
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
package com.wl4g.devops.components.tools.common.remoting;

import java.net.URI;

import com.wl4g.devops.components.tools.common.remoting.HttpResponseEntity;
import com.wl4g.devops.components.tools.common.remoting.RestClient;

public class RestClientSimpleTests {

	public static void main(String[] args) {
		getForEntityTest1();
		getForObjectTest2();
	}

	public static void getForEntityTest1() {
		String uri = "http://api.map.baidu.com/telematics/v3/weather?location=嘉兴&output=json&ak=5slgyqGDENN7Sy7pw29IUvrZ";
		HttpResponseEntity<String> resp = new RestClient().getForEntity(URI.create(uri), String.class);
		System.out.println(resp.getBody());
	}

	public static void getForObjectTest2() {
		String uri = "http://api.map.baidu.com/telematics/v3/weather?location=嘉兴&output=json&ak=5slgyqGDENN7Sy7pw29IUvrZ";
		BaiduWeatherBean resp = new RestClient().getForObject(URI.create(uri), BaiduWeatherBean.class);
		System.out.println(resp);
	}

	public static class BaiduWeatherBean {

		private String status;
		private String message;

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		@Override
		public String toString() {
			return "BaiduWeatherBean [status=" + status + ", message=" + message + "]";
		}

	}

}
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
package com.wl4g.devops.umc.opentsdb.client.bean.response;

import java.text.MessageFormat;

/**
 * 错误信息
 *
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2019/2/22 下午7:50
 * @Version: 1.0
 */
public class ErrorResponse {

	private Error error;

	@Override
	public String toString() {
		return MessageFormat.format("调用OpenTSDB http api发生错误，响应码:{0},错误信息:{1}", error.getCode(), error.getMessage());
	}

	public static class Error {

		private int code;

		private String message;

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}

}
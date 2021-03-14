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
package com.wl4g.paas.umc.opentsdb.client.bean.response;

import java.util.List;

import com.wl4g.paas.umc.opentsdb.client.bean.request.Point;

/**
 * @Author: jinyao
 * @Description:
 * @CreateDate: 2019/2/24 下午8:07
 * @Version: 1.0
 */

public class DetailResult {

	private List<ErrorPoint> errors;

	private int failed;

	private int success;

	public static class ErrorPoint {

		private Point datapoint;

		private String error;

		public Point getDatapoint() {
			return datapoint;
		}

		public void setDatapoint(Point datapoint) {
			this.datapoint = datapoint;
		}

		public String getError() {
			return error;
		}

		public void setError(String error) {
			this.error = error;
		}
	}

	public List<ErrorPoint> getErrors() {
		return errors;
	}

	public void setErrors(List<ErrorPoint> errors) {
		this.errors = errors;
	}

	public int getFailed() {
		return failed;
	}

	public void setFailed(int failed) {
		this.failed = failed;
	}

	public int getSuccess() {
		return success;
	}

	public void setSuccess(int success) {
		this.success = success;
	}
}
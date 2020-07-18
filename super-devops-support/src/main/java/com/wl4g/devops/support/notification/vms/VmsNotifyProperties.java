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
package com.wl4g.devops.support.notification.vms;

import static com.wl4g.devops.components.tools.common.lang.Assert2.hasTextOf;

import javax.validation.constraints.NotBlank;

import com.wl4g.devops.support.notification.AbstractNotifyProperties;
import com.wl4g.devops.support.notification.NotifyProperties;

public class VmsNotifyProperties implements NotifyProperties {

	private AliyunVmsProperties aliyun = new AliyunVmsProperties();

	public AliyunVmsProperties getAliyun() {
		return aliyun;
	}

	public void setAliyun(AliyunVmsProperties aliyun) {
		this.aliyun = aliyun;
	}

	@Override
	public void validate() {
	}

	public static class AliyunVmsProperties extends AbstractNotifyProperties {

		/**
		 * Called show number
		 */
		@NotBlank
		private String calledShowNumber;

		/**
		 * e.g. cn-hangzhou
		 */
		@NotBlank
		private String regionId;

		@NotBlank
		private String accessKeyId;

		@NotBlank
		private String accessKeySecret;

		public String getCalledShowNumber() {
			return calledShowNumber;
		}

		public AliyunVmsProperties setCalledShowNumber(String calledShowNumber) {
			hasTextOf(calledShowNumber, "calledShowNumber");
			this.calledShowNumber = calledShowNumber;
			return this;
		}

		public String getRegionId() {
			return regionId;
		}

		public void setRegionId(String regionId) {
			this.regionId = regionId;
		}

		public String getAccessKeyId() {
			return accessKeyId;
		}

		public void setAccessKeyId(String accessKeyId) {
			this.accessKeyId = accessKeyId;
		}

		public String getAccessKeySecret() {
			return accessKeySecret;
		}

		public void setAccessKeySecret(String secret) {
			this.accessKeySecret = secret;
		}

	}

}
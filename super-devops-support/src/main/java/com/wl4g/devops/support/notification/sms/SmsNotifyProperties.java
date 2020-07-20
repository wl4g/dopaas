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
package com.wl4g.devops.support.notification.sms;

import javax.validation.constraints.NotBlank;

import com.wl4g.devops.support.notification.AbstractNotifyProperties;
import com.wl4g.devops.support.notification.NotifyProperties;

/**
 * {@link SmsNotifyProperties}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年1月9日 v1.0.0
 * @see
 */
public class SmsNotifyProperties implements NotifyProperties {

	private AliyunSmsNotifyProperties aliyun = new AliyunSmsNotifyProperties();

	public AliyunSmsNotifyProperties getAliyun() {
		return aliyun;
	}

	public void setAliyun(AliyunSmsNotifyProperties aliyun) {
		this.aliyun = aliyun;
	}

	@Override
	public void validate() {

	}

	public static class AliyunSmsNotifyProperties extends AbstractNotifyProperties {

		/**
		 * e.g. cn-hangzhou
		 */
		@NotBlank
		private String regionId = "cn-hangzhou";

		// @NotBlank
		// private String product = "Dysmsapi";
		// @NotBlank
		// private String domain = "dysmsapi.aliyuncs.com";

		@NotBlank
		private String accessKeyId;

		@NotBlank
		private String accessKeySecret;

		@NotBlank
		private String signName;

		@NotBlank
		private String defaultConnectTimeout = "5_000";

		@NotBlank
		private String defaultReadTimeout = "10_000";

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

		public void setAccessKeySecret(String accessKeySecret) {
			this.accessKeySecret = accessKeySecret;
		}

		public String getSignName() {
			return signName;
		}

		public void setSignName(String signName) {
			this.signName = signName;
		}

		public String getDefaultConnectTimeout() {
			return defaultConnectTimeout;
		}

		public void setDefaultConnectTimeout(String defaultConnectTimeout) {
			this.defaultConnectTimeout = defaultConnectTimeout;
		}

		public String getDefaultReadTimeout() {
			return defaultReadTimeout;
		}

		public void setDefaultReadTimeout(String defaultReadTimeout) {
			this.defaultReadTimeout = defaultReadTimeout;
		}
	}

}
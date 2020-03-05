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

import java.util.Properties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

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

	public static class BaseVmsProperties {

		@NotEmpty
		private Properties templates = new Properties(); // 文本转语音（TTS）模板ID

		public Properties getTemplates() {
			return templates;
		}

		public void setTemplates(Properties templates) {
			this.templates = templates;
		}

	}

	public static class AliyunVmsProperties extends BaseVmsProperties {

		/**
		 * e.g. cn-hangzhou
		 */
		@NotBlank
		private String regionId;
		@NotBlank
		private String accessKeyId;
		@NotBlank
		private String secret;

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

		public String getSecret() {
			return secret;
		}

		public void setSecret(String secret) {
			this.secret = secret;
		}

	}

}

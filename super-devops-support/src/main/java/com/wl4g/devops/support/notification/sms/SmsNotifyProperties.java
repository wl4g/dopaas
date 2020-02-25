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

	public static class AliyunSmsNotifyProperties {

		private String product;

		private String domain;

		private String accessKeyId;

		private String accessKeySecret;

		private String signName;

		private String templateCode;

		private String defaultConnectTimeout;

		private String defaultReadTimeout;

		public String getProduct() {
			return product;
		}

		public void setProduct(String product) {
			this.product = product;
		}

		public String getDomain() {
			return domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
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

		public String getTemplateCode() {
			return templateCode;
		}

		public void setTemplateCode(String templateCode) {
			this.templateCode = templateCode;
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

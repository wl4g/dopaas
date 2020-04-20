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
package com.wl4g.devops.iam.config.properties;

import static com.wl4g.devops.tool.common.lang.Assert2.isTrue;
import static com.wl4g.devops.tool.common.lang.StringUtils2.startsWithIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Social networking services configuration
 *
 * @author wangl.sir
 * @version v1.0 2019年1月8日
 * @since
 */
@ConfigurationProperties(prefix = "spring.cloud.devops.iam.sns")
public class SnsProperties {

	/**
	 * Number of SNS oauth2 milliseconds of oauth2 connect processing expiration
	 */
	private long oauth2ConnectExpireMs = 60_1000L;

	private QQSocialProperties qq = new QQSocialProperties();

	private WechatSocialProperties wechat = new WechatSocialProperties();

	private WechatMpSocialProperties wechatMp = new WechatMpSocialProperties();

	public long getOauth2ConnectExpireMs() {
		return oauth2ConnectExpireMs;
	}

	public void setOauth2ConnectExpireMs(long connectExpireMs) {
		this.oauth2ConnectExpireMs = connectExpireMs;
	}

	public QQSocialProperties getQq() {
		return qq;
	}

	public void setQq(QQSocialProperties qq) {
		this.qq = qq;
	}

	public WechatSocialProperties getWechat() {
		return wechat;
	}

	public void setWechat(WechatSocialProperties wechat) {
		this.wechat = wechat;
	}

	public WechatMpSocialProperties getWechatMp() {
		return wechatMp;
	}

	public void setWechatMp(WechatMpSocialProperties wechatMp) {
		this.wechatMp = wechatMp;
	}

	/**
	 * Abstract socical networking services platform configuration properties
	 *
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年2月17日
	 * @since
	 */
	public static abstract class AbstractSocialProperties {
		private String appId;
		private String appSecret;
		private String redirectUrl;

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public String getAppSecret() {
			return appSecret;
		}

		public void setAppSecret(String appSecret) {
			this.appSecret = appSecret;
		}

		public String getRedirectUrl() {
			return redirectUrl;
		}

		public void setRedirectUrl(String redirectUrl) {
			this.redirectUrl = redirectUrl;
		}

	}

	/**
	 * QQ open platform configuration properties
	 *
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年2月17日
	 * @since
	 */
	public static class QQSocialProperties extends AbstractSocialProperties {

	}

	/**
	 * Wechat open platform configuration properties
	 *
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年2月17日
	 * @since
	 */
	public static class WechatSocialProperties extends AbstractSocialProperties {

		/**
		 * Wechat has added a new page style that supports custom
		 * authorization.<br/>
		 * See:https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419316505&token=2f2444cf6d55676b11a5de1b8b348ba202dbba8c&lang=zh_CN
		 */
		private String href;

		public String getHref() {
			return href;
		}

		public void setHref(String href) {
			if (!isBlank(href)) {
				// Wechat oauth2 authorization interface only supports HTTPS
				isTrue(startsWithIgnoreCase(href, "HTTPS"), "The 'href' link must be the absolute path of HTTPS protocol");
				this.href = href;
			}
		}

	}

	/**
	 * Wechat public platform configuration properties
	 *
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年2月17日
	 * @since
	 */
	public static class WechatMpSocialProperties extends AbstractSocialProperties {

	}

}
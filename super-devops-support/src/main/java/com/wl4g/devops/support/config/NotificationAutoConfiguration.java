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
package com.wl4g.devops.support.config;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.support.notification.CompositeMessageNotifier;
import com.wl4g.devops.support.notification.MessageNotifier;
import com.wl4g.devops.support.notification.NotifyMessage;
import com.wl4g.devops.support.notification.apns.ApnsMessageNotifier;
import com.wl4g.devops.support.notification.apns.ApnsNotifyProperties;
import com.wl4g.devops.support.notification.bark.BarkMessageNotifier;
import com.wl4g.devops.support.notification.bark.BarkNotifyProperties;
import com.wl4g.devops.support.notification.dingtalk.DingtalkMessageNotifier;
import com.wl4g.devops.support.notification.dingtalk.DingtalkNotifyProperties;
import com.wl4g.devops.support.notification.facebook.FacebookMessageNotifier;
import com.wl4g.devops.support.notification.facebook.FacebookNotifyProperties;
import com.wl4g.devops.support.notification.mail.MailMessageNotifier;
import com.wl4g.devops.support.notification.mail.MailNotifyProperties;
import com.wl4g.devops.support.notification.qq.QqMessageNotifier;
import com.wl4g.devops.support.notification.qq.QqNotifyProperties;
import com.wl4g.devops.support.notification.sms.AliyunSmsMessageNotifier;
import com.wl4g.devops.support.notification.sms.SmsNotifyProperties;
import com.wl4g.devops.support.notification.twitter.TwitterMessageNotifier;
import com.wl4g.devops.support.notification.twitter.TwitterNotifyProperties;
import com.wl4g.devops.support.notification.vms.VmsMessageNotifier;
import com.wl4g.devops.support.notification.vms.VmsNotifyProperties;
import com.wl4g.devops.support.notification.wechat.WechatMessageNotifier;
import com.wl4g.devops.support.notification.wechat.WechatNotifyProperties;

/**
 * Notification message service auto configuration
 * 
 * @author wangl.sir
 * @version v1.0 2019年8月28日
 * @since
 */
@Configuration
public class NotificationAutoConfiguration {
	final public static String KEY_NOTIFY_PREFIX = "spring.cloud.devops.support.notification";

	// --- Notify configuration properties. ---

	@Bean
	@ConditionalOnProperty(name = KEY_NOTIFY_PREFIX + ".apns.enable", matchIfMissing = false)
	public ApnsNotifyProperties apnsNotifyProperties() {
		return new ApnsNotifyProperties();
	}

	@Bean
	@ConditionalOnProperty(name = KEY_NOTIFY_PREFIX + ".bark.enable", matchIfMissing = false)
	public BarkNotifyProperties barkNotifyProperties() {
		return new BarkNotifyProperties();
	}

	@Bean
	@ConditionalOnProperty(name = KEY_NOTIFY_PREFIX + ".dingtalk.enable", matchIfMissing = false)
	public DingtalkNotifyProperties dingtalkNotifyProperties() {
		return new DingtalkNotifyProperties();
	}

	@Bean
	@ConditionalOnProperty(name = KEY_NOTIFY_PREFIX + ".facebook.enable", matchIfMissing = false)
	public FacebookNotifyProperties facebookNotifyProperties() {
		return new FacebookNotifyProperties();
	}

	@Bean
	@ConditionalOnProperty(name = KEY_NOTIFY_PREFIX + ".mail.enable", matchIfMissing = false)
	public MailNotifyProperties mailNotifyProperties() {
		return new MailNotifyProperties();
	}

	@Bean
	@ConditionalOnProperty(name = KEY_NOTIFY_PREFIX + ".qq.enable", matchIfMissing = false)
	public QqNotifyProperties qqNotifyProperties() {
		return new QqNotifyProperties();
	}

	@Bean
	@ConditionalOnProperty(name = KEY_NOTIFY_PREFIX + ".sms.enable", matchIfMissing = false)
	public SmsNotifyProperties smsNotifyProperties() {
		return new SmsNotifyProperties();
	}

	@Bean
	@ConditionalOnProperty(name = KEY_NOTIFY_PREFIX + ".vms.enable", matchIfMissing = false)
	public VmsNotifyProperties vmsNotifyProperties() {
		return new VmsNotifyProperties();
	}

	@Bean
	@ConditionalOnProperty(name = KEY_NOTIFY_PREFIX + ".wechat.enable", matchIfMissing = false)
	public WechatNotifyProperties wechatNotifyProperties() {
		return new WechatNotifyProperties();
	}

	@Bean
	@ConditionalOnProperty(name = KEY_NOTIFY_PREFIX + ".twitter.enable", matchIfMissing = false)
	public TwitterNotifyProperties twitterNotifyProperties() {
		return new TwitterNotifyProperties();
	}

	// --- Notifier configuration. ---

	@Bean
	@ConditionalOnBean({ MessageNotifier.class })
	public CompositeMessageNotifier compositeMessageNotifier(List<MessageNotifier<NotifyMessage>> operators) {
		return new CompositeMessageNotifier(operators);
	}

	@Bean
	@ConditionalOnBean(ApnsNotifyProperties.class)
	public ApnsMessageNotifier apnsMessageNotifier() {
		return new ApnsMessageNotifier();
	}

	@Bean
	@ConditionalOnBean(BarkMessageNotifier.class)
	public BarkMessageNotifier barkMessageNotifier() {
		return new BarkMessageNotifier();
	}

	@Bean
	@ConditionalOnBean(DingtalkMessageNotifier.class)
	public DingtalkMessageNotifier dingtalkMessageNotifier() {
		return new DingtalkMessageNotifier();
	}

	@Bean
	@ConditionalOnBean(FacebookMessageNotifier.class)
	public FacebookMessageNotifier facebookMessageNotifier() {
		return new FacebookMessageNotifier();
	}

	@Bean
	@ConditionalOnBean(MailNotifyProperties.class)
	public MailMessageNotifier mailMessageNotifier() {
		return new MailMessageNotifier();
	}

	@Bean
	@ConditionalOnBean(QqMessageNotifier.class)
	public QqMessageNotifier qqMessageNotifier() {
		return new QqMessageNotifier();
	}

	@Bean
	@ConditionalOnBean(SmsNotifyProperties.class)
	public AliyunSmsMessageNotifier aliyunSmsMessageNotifier(SmsNotifyProperties config) {
		return new AliyunSmsMessageNotifier();
	}

	@Bean
	@ConditionalOnBean(VmsMessageNotifier.class)
	public VmsMessageNotifier vmsMessageNotifier() {
		return new VmsMessageNotifier();
	}

	@Bean
	@ConditionalOnBean(WechatMessageNotifier.class)
	public WechatMessageNotifier wechatMessageNotifier() {
		return new WechatMessageNotifier();
	}

	@Bean
	@ConditionalOnBean(TwitterNotifyProperties.class)
	public TwitterMessageNotifier twitterMessageNotifier() {
		return new TwitterMessageNotifier();
	}

}
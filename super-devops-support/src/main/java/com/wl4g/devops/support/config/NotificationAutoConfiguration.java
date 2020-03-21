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

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.support.notification.MessageNotifier;
import com.wl4g.devops.support.notification.MessageNotifier.NotifierKind;
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
import com.wl4g.devops.support.notification.vms.AliyunVmsMessageNotifier;
import com.wl4g.devops.support.notification.vms.VmsNotifyProperties;
import com.wl4g.devops.support.notification.wechat.WechatMessageNotifier;
import com.wl4g.devops.support.notification.wechat.WechatNotifyProperties;
import static com.wl4g.devops.support.notification.NoOpMessageNotifier.*;

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

	// --- Notify properties. ---

	@Bean(name = "apnsNotifyProperties")
	@ConditionalOnProperty(name = KEY_NOTIFY_PREFIX + ".apns.enable", matchIfMissing = false)
	@ConfigurationProperties(prefix = KEY_NOTIFY_PREFIX + ".apns")
	public ApnsNotifyProperties apnsNotifyProperties() {
		return new ApnsNotifyProperties();
	}

	@Bean(name = "barkNotifyProperties")
	@ConditionalOnProperty(name = KEY_NOTIFY_PREFIX + ".bark.enable", matchIfMissing = false)
	@ConfigurationProperties(prefix = KEY_NOTIFY_PREFIX + ".bark")
	public BarkNotifyProperties barkNotifyProperties() {
		return new BarkNotifyProperties();
	}

	@Bean(name = "dingtalkNotifyProperties")
	@ConditionalOnProperty(name = KEY_NOTIFY_PREFIX + ".dingtalk.enable", matchIfMissing = false)
	@ConfigurationProperties(prefix = KEY_NOTIFY_PREFIX + ".dingtalk")
	public DingtalkNotifyProperties dingtalkNotifyProperties() {
		return new DingtalkNotifyProperties();
	}

	@Bean(name = "facebookNotifyProperties")
	@ConditionalOnProperty(name = KEY_NOTIFY_PREFIX + ".facebook.enable", matchIfMissing = false)
	@ConfigurationProperties(prefix = KEY_NOTIFY_PREFIX + ".facebook")
	public FacebookNotifyProperties facebookNotifyProperties() {
		return new FacebookNotifyProperties();
	}

	@Bean(name = "mailNotifyProperties")
	@ConditionalOnProperty(name = KEY_NOTIFY_PREFIX + ".mail.enable", matchIfMissing = false)
	@ConfigurationProperties(prefix = KEY_NOTIFY_PREFIX + ".mail")
	public MailNotifyProperties mailNotifyProperties() {
		return new MailNotifyProperties();
	}

	@Bean(name = "qqNotifyProperties")
	@ConditionalOnProperty(name = KEY_NOTIFY_PREFIX + ".qq.enable", matchIfMissing = false)
	@ConfigurationProperties(prefix = KEY_NOTIFY_PREFIX + ".qq")
	public QqNotifyProperties qqNotifyProperties() {
		return new QqNotifyProperties();
	}

	@Bean(name = "smsNotifyProperties")
	@ConditionalOnProperty(name = KEY_NOTIFY_PREFIX + ".sms.enable", matchIfMissing = false)
	@ConfigurationProperties(prefix = KEY_NOTIFY_PREFIX + ".sms")
	public SmsNotifyProperties smsNotifyProperties() {
		return new SmsNotifyProperties();
	}

	@Bean(name = "vmsNotifyProperties")
	@ConditionalOnProperty(name = KEY_NOTIFY_PREFIX + ".vms.enable", matchIfMissing = false)
	@ConfigurationProperties(prefix = KEY_NOTIFY_PREFIX + ".vms")
	public VmsNotifyProperties vmsNotifyProperties() {
		return new VmsNotifyProperties();
	}

	@Bean(name = "wechatNotifyProperties")
	@ConditionalOnProperty(name = KEY_NOTIFY_PREFIX + ".wechat.enable", matchIfMissing = false)
	@ConfigurationProperties(prefix = KEY_NOTIFY_PREFIX + ".wechat")
	public WechatNotifyProperties wechatNotifyProperties() {
		return new WechatNotifyProperties();
	}

	@Bean(name = "twitterNotifyProperties")
	@ConditionalOnProperty(name = KEY_NOTIFY_PREFIX + ".twitter.enable", matchIfMissing = false)
	@ConfigurationProperties(prefix = KEY_NOTIFY_PREFIX + ".twitter")
	public TwitterNotifyProperties twitterNotifyProperties() {
		return new TwitterNotifyProperties();
	}

	// --- Message notifier. ---

	@Bean
	@ConditionalOnBean(ApnsNotifyProperties.class)
	public ApnsMessageNotifier apnsMessageNotifier(ApnsNotifyProperties config) {
		return new ApnsMessageNotifier(config);
	}

	@Bean
	@ConditionalOnBean(BarkNotifyProperties.class)
	public BarkMessageNotifier barkMessageNotifier(BarkNotifyProperties config) {
		return new BarkMessageNotifier(config);
	}

	@Bean
	@ConditionalOnBean(DingtalkNotifyProperties.class)
	public DingtalkMessageNotifier dingtalkMessageNotifier(DingtalkNotifyProperties config) {
		return new DingtalkMessageNotifier(config);
	}

	@Bean
	@ConditionalOnBean(FacebookNotifyProperties.class)
	public FacebookMessageNotifier facebookMessageNotifier(FacebookNotifyProperties config) {
		return new FacebookMessageNotifier(config);
	}

	@Bean
	@ConditionalOnBean(MailNotifyProperties.class)
	public MailMessageNotifier mailMessageNotifier(MailNotifyProperties config) {
		return new MailMessageNotifier(config);
	}

	@Bean
	@ConditionalOnBean(QqNotifyProperties.class)
	public QqMessageNotifier qqMessageNotifier(QqNotifyProperties config) {
		return new QqMessageNotifier(config);
	}

	@Bean
	@ConditionalOnBean(SmsNotifyProperties.class)
	public AliyunSmsMessageNotifier aliyunSmsMessageNotifier(SmsNotifyProperties config) {
		return new AliyunSmsMessageNotifier(config);
	}

	@Bean
	@ConditionalOnBean(VmsNotifyProperties.class)
	public AliyunVmsMessageNotifier aliyunVmsMessageNotifier(VmsNotifyProperties config) {
		return new AliyunVmsMessageNotifier(config);
	}

	@Bean
	@ConditionalOnBean(WechatNotifyProperties.class)
	public WechatMessageNotifier wechatMessageNotifier(WechatNotifyProperties config) {
		return new WechatMessageNotifier(config);
	}

	@Bean
	@ConditionalOnBean(TwitterNotifyProperties.class)
	public TwitterMessageNotifier twitterMessageNotifier(TwitterNotifyProperties config) {
		return new TwitterMessageNotifier(config);
	}

	/**
	 * 1, Cannot inject at this time:
	 * 
	 * <pre>
	 * &#64;Bean
	 * public CompositeMessageNotifier compositeMessageNotifier(List&lt;MessageNotifier&lt;NotifyMessage&gt;&gt; notifiers) {
	 *   ...
	 * }
	 * </pre>
	 * 
	 * 2, Can operate correctly:
	 * 
	 * <pre>
	 * &#64;Bean
	 * public CompositeMessageNotifier compositeMessageNotifier(List&lt;MessageNotifier&lt;? extends NotifyMessage&gt;&gt; notifiers) {
	 *   ...
	 * }
	 * </pre>
	 * 
	 * @param notifiers
	 * @return
	 */
	@Bean
	public GenericOperatorAdapter<NotifierKind, MessageNotifier> compositeMessageNotifierAdapter(
			@Autowired(required = false) List<MessageNotifier> notifiers) {
		if (isNull(notifiers))
			return defaultMessageNotifier;

		return new GenericOperatorAdapter<NotifierKind, MessageNotifier>(
				notifiers.stream().map(n -> ((MessageNotifier) n)).collect(toList()), DefaultNoOp) {
		};
	}

	/**
	 * Default message notifier. {@link MessageNotifier}
	 */
	final private static GenericOperatorAdapter<NotifierKind, MessageNotifier> defaultMessageNotifier = new GenericOperatorAdapter<NotifierKind, MessageNotifier>(
			DefaultNoOp) {
	};

}
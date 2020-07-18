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
package com.wl4g.devops.support.notification;

import com.wl4g.devops.common.framework.operator.Operator;
import com.wl4g.devops.support.notification.mail.MailMessageNotifier;

import static com.wl4g.devops.support.notification.MessageNotifier.NotifierKind;

/**
 * {@link MessageNotifier} notification.
 * 
 * <pre>
 * <b>- application-dev.yml:</b>
 * 
 * spring:
 *   cloud:
 *     devops:
 *       support:
 *         notification:
 *           mail:
 *             enable: true # Default[false]
 *             properties:
 *               mail.smtp.auth: true
 *               mail.smtp.ssl.enable: true
 *               mail.smtp.timeout: 15000
 *               mail.smtp.starttls.enable: true
 *               mail.smtp.starttls.required: true
 *             templates: # http://www.bejson.com/convert/unicode_chinese
 *                 tpl1: Application health notification：${appInfo} status changed from ${fromStatus} to ${toStatus}, details：${msg}
 *                 tpl1: Your verification code is：${code} valid for 5 minutes. You can't tell anyone if you're dead. Thank you for using!
 *           sms:
 *             enable: true # Default[false]
 *             aliyun:
 *               regionId: cn-hangzhou
 *               accessKeyId: LTAI4Fk9pjU7ezN2yVeiffYm
 *               accessKeySecret: {ALIYUN_SECRET}
 *               signName: Super Devops\u7edf\u4e00\u76d1\u63a7\u5e73\u53f0
 *               templates: # https://dysms.console.aliyun.com/dysms.htm#/domestic/text/template
 *                 tpl1: SMS_140726862 # Alarm notice template.
 *                 tpl2: SMS_109490228 # VerificationCode notice template.
 *           vms:
 *             enable: true # Default[false]
 *             aliyun:
 *               regionId: cn-hangzhou
 *               accessKeyId: LTAI4Fk9pjU7ezN2yVeiffYm
 *               accessKeySecret: {ALIYUN_SECRET}
 *               calledShowNumber: 055162153866
 *               templates:
 *                 tpl1: TTS_184825642 # Alarm notice template.
 *                 tpl2: TTS_184820765 # VerificationCode notice template.
 * 
 * <b>- Use for example:</b>
 * 
 *{@link @Service}
 * public class ExampleAlerter {
 *
 *    // Injection message notifier adapter.
 *   {@link @Autowired}
 *    private GenericOperatorAdapter&lt;{@link NotifierKind}, {@link MessageNotifier}&gt; notifierAdapter;
 *    
 *    public void doNotify() {
 *        {@link GenericNotifyMessage} msg = new {@link GenericNotifyMessage}("wanglsir@gmail.com", "alaramTpl1");
 *
 *        // Sets common parameters. <font color=red><b>(Required)</b></font>
 *        msg.addParameter("appName", "bizService1");
 *        msg.addParameter("status", "DOWN");
 *        msg.addParameter("cause", "Host.cpu.utilization > 200%");
 *        // More customize variables(Note: just match the template) ...
 *
 *        // Sets mail special parameters. <font color=
red><b>(Optional)</b></font>
 *        //msg.addParameter({@link MailMessageNotifier#KEY_MAILMSG_SUBJECT}, "This is a test message");
 *        //msg.addParameter({@link MailMessageNotifier#KEY_MAILMSG_CC}, "test1@gmail.com");
 *        //msg.addParameter({@link MailMessageNotifier#KEY_MAILMSG_BCC}, "test2@qq.com");
 *        //msg.addParameter({@link MailMessageNotifier#KEY_MAILMSG_REPLYTO}, "test3@163.com");
 *
 *        // Do sent
 *        notifierAdapter.forOperator({@link NotifierKind#AliyunSms}).send(msg);
 *        //notifierAdapter.forOperator({@link NotifierKind#Mail}).send(msg);
 *        // ...
 *    }
 * 
 * }
 * 
 * </pre>
 * 
 * @param <T>
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年1月9日 v1.0.0
 * @see
 */
public interface MessageNotifier extends Operator<NotifierKind> {

	/**
	 * Sending notification message.
	 * 
	 * @param <T>
	 * @param msg
	 */
	<T extends NotifyMessage> void send(GenericNotifyMessage msg);

	/**
	 * Sending notification message for complete reply.
	 * 
	 * @param <T>
	 * @param <R>
	 * @param msg
	 * @return
	 */
	<R> R sendForReply(GenericNotifyMessage msg);

	/**
	 * Notification privoder kind.
	 * 
	 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version 2020年1月8日 v1.0.0
	 * @see
	 */
	public static enum NotifierKind {

		/**
		 * MessageNotifier that must be instantiated. The default implementation
		 * when all other message notifiers are not available solves the spring
		 * bean injection problem.
		 * 
		 * @see {@link com.wl4g.devops.support.notification.NoOpMessageNotifier}
		 */
		NoOp,

		Apns,

		Bark,

		Dingtalk,

		Facebook,

		Mail,

		Qq,

		AliyunSms,

		AliyunVms,

		WechatMp,

		Twitter;

	}

	/**
	 * Notification message sendDate keyname.
	 */
	final public static String KEY_MSG_SENDDATE = "msgSendDate";

}
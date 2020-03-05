package com.wl4g.devops.support.notification;

import com.wl4g.devops.common.framework.operator.Operator;

import static com.wl4g.devops.support.notification.MessageNotifier.NotifierKind;

/**
 * {@link MessageNotifier} notification.
 * 
 * @param <T>
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年1月9日 v1.0.0
 * @see
 */
public interface MessageNotifier<T extends NotifyMessage> extends Operator<NotifierKind> {

	/**
	 * Sending notification message.
	 * 
	 * @param <T>
	 * @param message
	 */
	void send(T message);

	/**
	 * Sending notification message for complete reply.
	 * 
	 * @param <T>
	 * @param <R>
	 * @param message
	 * @return
	 */
	<R> R sendForReply(T message);

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

}

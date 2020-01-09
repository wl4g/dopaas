package com.wl4g.devops.support.notification;

import java.util.List;

import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.support.notification.MessageNotifier.NotifierKind;

/**
 * {@link CompositeMessageNotifier}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年1月9日 v1.0.0
 * @see
 */
public class CompositeMessageNotifier extends GenericOperatorAdapter<NotifierKind, MessageNotifier<NotifyMessage>>
		implements MessageNotifier<NotifyMessage> {

	public CompositeMessageNotifier(List<MessageNotifier<NotifyMessage>> operators) {
		super(operators);
	}

	@Override
	public void send(NotifyMessage message) {
		getAdapted().send(message);
	}

	@Override
	public <R> R sendForReply(NotifyMessage message) {
		return getAdapted().sendForReply(message);
	}

}

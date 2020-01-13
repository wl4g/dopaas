package com.wl4g.devops.support.notification;

import com.wl4g.devops.support.notification.EmptyForMustCheckImplMessageNotifier.EmptyNotifyProperties;
import com.wl4g.devops.support.notification.EmptyForMustCheckImplMessageNotifier.EmptyMessage;

/**
 * {@link MessageNotifier} that must be instantiated.</br>
 * The default implementation when all other message notifiers are not available
 * solves the spring bean injection problem.
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年1月10日 v1.0.0
 * @see
 */
public class EmptyForMustCheckImplMessageNotifier extends AbstractMessageNotifier<EmptyNotifyProperties, EmptyMessage> {

	public EmptyForMustCheckImplMessageNotifier() {
		super(new EmptyNotifyProperties());
	}

	@Override
	public NotifierKind kind() {
		return NotifierKind.Empty;
	}

	@Override
	public void send(EmptyMessage message) {
		throw new UnsupportedOperationException(
				"This is an empty message notifier implementation. Please check whether the real message notifier is configured correctly!");
	}

	@Override
	public <R> R sendForReply(EmptyMessage message) {
		throw new UnsupportedOperationException(
				"This is an empty message notifier implementation. Please check whether the real message notifier is configured correctly!");
	}

	public static class EmptyNotifyProperties {

	}

	public static class EmptyMessage implements NotifyMessage {
		private static final long serialVersionUID = 6991694645851661886L;

	}

}
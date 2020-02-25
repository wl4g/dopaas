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

public class EmptyMessageNotifier extends AbstractMessageNotifier<NotifyProperties, NotifyMessage> {

	public EmptyMessageNotifier(NotifyProperties config) {
		super(config);
	}

	@Override
	public NotifierKind kind() {
		return NotifierKind.Empty;
	}

	@Override
	public void send(NotifyMessage message) {
		throw new UnsupportedOperationException(
				"This is an empty message notifier implementation. Please check whether the real message notifier is configured correctly!");
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public Object sendForReply(NotifyMessage message) {
		throw new UnsupportedOperationException(
				"This is an empty message notifier implementation. Please check whether the real message notifier is configured correctly!");
	}

}

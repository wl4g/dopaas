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
package com.wl4g.devops.umc.notification;

import java.util.Arrays;
import java.util.Map;

/**
 * Alarm notifier
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public interface AlarmNotifier {

	AlarmType alarmType();

	/**
	 * Send simple notify message.
	 * 
	 * @param message
	 */
	void simpleNotify(SimpleAlarmMessage message);

	/**
	 * Send template notify message.
	 * 
	 * @param message
	 */
	void templateNotify(TeampleAlarmMessage message);

	/**
	 * Simple alarm message
	 * 
	 * @author Wangl.sir
	 * @version v1.0 2019年7月24日
	 * @since
	 */
	public static class SimpleAlarmMessage {

		final private String[] owners;

		final private String message;

		final private String alarmType;

		public SimpleAlarmMessage(String message, String alarmType, String... owners) {
			super();
			this.message = message;
			this.alarmType = alarmType;
			this.owners = owners;
		}

		public String getMessage() {
			return message;
		}

		public String getAlarmType() {
			return alarmType;
		}

		@Override
		public String toString() {
			return "AlarmMessage [owners=" + Arrays.toString(owners) + ", message=" + message + ", alarmType=" + alarmType + "]";
		}

	}

	/**
	 * Template alarm message
	 * 
	 * @author Wangl.sir
	 * @version v1.0 2019年7月24日
	 * @since
	 */
	public static class TeampleAlarmMessage extends SimpleAlarmMessage {

		final private String templateId;

		final private Map<String, Object> parameters;

		public TeampleAlarmMessage(String message, String alarmType, String templateId, Map<String, Object> parameters,
				String... owners) {
			super(message, alarmType, owners);
			this.templateId = templateId;
			this.parameters = parameters;
		}

		public String getTemplateId() {
			return templateId;
		}

		public Map<String, Object> getParameters() {
			return parameters;
		}

		@Override
		public String toString() {
			return "TeampleAlarmMessage [templateId=" + templateId + ", parameters=" + parameters + "]";
		}

	}

}
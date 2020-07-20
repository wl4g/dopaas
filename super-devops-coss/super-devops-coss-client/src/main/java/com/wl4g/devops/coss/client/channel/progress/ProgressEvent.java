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
package com.wl4g.devops.coss.client.channel.progress;

/**
 * {@link ProgressEvent}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年7月1日
 * @since
 */
public class ProgressEvent {

	final private long bytes;
	final private ProgressEventType eventType;

	public ProgressEvent(ProgressEventType eventType) {
		this(eventType, 0);
	}

	public ProgressEvent(ProgressEventType eventType, long bytes) {
		if (eventType == null) {
			throw new IllegalArgumentException("eventType must not be null.");
		}
		if (bytes < 0) {
			throw new IllegalArgumentException("bytes transferred must be non-negative");
		}
		this.eventType = eventType;
		this.bytes = bytes;
	}

	public ProgressEventType getEventType() {
		return eventType;
	}

	public long getBytes() {
		return bytes;
	}

	@Override
	public String toString() {
		return eventType + ", bytes: " + bytes;
	}

	/**
	 * {@link ProgressEventType}
	 *
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2020年7月1日
	 * @since
	 */
	public static enum ProgressEventType {

		/**
		 * Event of the content length to be sent in a request.
		 */
		REQUEST_CONTENT_LENGTH_EVENT,

		/**
		 * Event of the content length received in a response.
		 */
		RESPONSE_CONTENT_LENGTH_EVENT,

		/**
		 * Used to indicate the number of bytes to be sent to OSS.
		 */
		REQUEST_BYTE_TRANSFER_EVENT,

		/**
		 * Used to indicate the number of bytes received from OSS.
		 */
		RESPONSE_BYTE_TRANSFER_EVENT,

		TRANSFER_PREPARING_EVENT,

		TRANSFER_STARTED_EVENT,

		TRANSFER_COMPLETED_EVENT,

		TRANSFER_FAILED_EVENT,

		TRANSFER_CANCELED_EVENT,

		TRANSFER_PART_STARTED_EVENT,

		TRANSFER_PART_COMPLETED_EVENT,

		TRANSFER_PART_FAILED_EVENT

	}

}
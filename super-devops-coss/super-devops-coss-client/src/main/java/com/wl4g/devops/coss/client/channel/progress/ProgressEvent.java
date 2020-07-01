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

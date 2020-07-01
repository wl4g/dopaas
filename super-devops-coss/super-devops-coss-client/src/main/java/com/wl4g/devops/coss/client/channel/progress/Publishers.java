package com.wl4g.devops.coss.client.channel.progress;

import static com.wl4g.devops.coss.client.channel.progress.ProgressEvent.ProgressEventType.*;
import com.wl4g.devops.coss.client.channel.progress.ProgressEvent.ProgressEventType;

/**
 * {@link Publishers}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年7月1日
 * @since
 */
public abstract class Publishers {

	public static void publishProgress(final ProgressListener listener, final ProgressEventType eventType) {
		if (listener == ProgressListener.NOOP || listener == null || eventType == null) {
			return;
		}
		listener.progressChanged(new ProgressEvent(eventType));
	}

	public static void publishSelectProgress(final ProgressListener listener, final ProgressEventType eventType,
			final long scannedBytes) {
		if (listener == ProgressListener.NOOP || listener == null || eventType == null) {
			return;
		}
		listener.progressChanged(new ProgressEvent(eventType, scannedBytes));
	}

	public static void publishRequestContentLength(final ProgressListener listener, final long bytes) {
		publishByteCountEvent(listener, REQUEST_CONTENT_LENGTH_EVENT, bytes);
	}

	public static void publishRequestBytesTransferred(final ProgressListener listener, final long bytes) {
		publishByteCountEvent(listener, REQUEST_BYTE_TRANSFER_EVENT, bytes);
	}

	public static void publishResponseContentLength(final ProgressListener listener, final long bytes) {
		publishByteCountEvent(listener, RESPONSE_CONTENT_LENGTH_EVENT, bytes);
	}

	public static void publishResponseBytesTransferred(final ProgressListener listener, final long bytes) {
		publishByteCountEvent(listener, RESPONSE_BYTE_TRANSFER_EVENT, bytes);
	}

	private static void publishByteCountEvent(final ProgressListener listener, final ProgressEventType eventType,
			final long bytes) {
		if (listener == ProgressListener.NOOP || listener == null || bytes <= 0) {
			return;
		}
		listener.progressChanged(new ProgressEvent(eventType, bytes));
	}

}

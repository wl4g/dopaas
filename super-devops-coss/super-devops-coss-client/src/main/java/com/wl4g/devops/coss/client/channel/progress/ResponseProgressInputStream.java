package com.wl4g.devops.coss.client.channel.progress;

import java.io.InputStream;

import static com.wl4g.devops.coss.client.channel.progress.Publishers.publishResponseBytesTransferred;

/**
 * {@link ResponseProgressInputStream}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年7月1日
 * @since
 */
public class ResponseProgressInputStream extends ProgressInputStream {

	public ResponseProgressInputStream(InputStream is, ProgressListener listener) {
		super(is, listener);
	}

	@Override
	protected void onEOF() {
		onNotifyBytesRead();
	}

	@Override
	protected void onNotifyBytesRead() {
		publishResponseBytesTransferred(getListener(), getUnnotifiedByteCount());
	}

}

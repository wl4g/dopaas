package com.wl4g.devops.coss.client.channel.progress;

import java.io.InputStream;

import static com.wl4g.devops.coss.client.channel.progress.Publishers.publishRequestBytesTransferred;

/**
 * {@link RequestProgressInputStream}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年7月1日
 * @since
 */
public class RequestProgressInputStream extends ProgressInputStream {

	public RequestProgressInputStream(InputStream is, ProgressListener listener) {
		super(is, listener);
	}

	@Override
	protected void onEOF() {
		onNotifyBytesRead();
	}

	@Override
	protected void onNotifyBytesRead() {
		publishRequestBytesTransferred(getListener(), getUnnotifiedByteCount());
	}

}

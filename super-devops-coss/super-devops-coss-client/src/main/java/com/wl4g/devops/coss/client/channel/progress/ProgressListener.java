package com.wl4g.devops.coss.client.channel.progress;

/**
 * {@link ProgressListener}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年7月1日
 * @since
 */
public interface ProgressListener {

	/**
	 * Progress changed
	 * 
	 * @param event
	 */
	void progressChanged(ProgressEvent event);

	public static final ProgressListener NOOP = new ProgressListener() {
		@Override
		public void progressChanged(ProgressEvent progressEvent) {
			// Ignore
		}
	};

}

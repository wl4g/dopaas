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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * {@link ProgressInputStream}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年7月1日
 * @since
 */
abstract class ProgressInputStream extends FilterInputStream {

	final private ProgressListener listener;
	final private int notifyThresHold;

	private int unnotifiedByteCount;
	private boolean hasBeenRead;
	private boolean doneEOF;
	private long notifiedByteCount;

	public ProgressInputStream(InputStream is, ProgressListener listener) {
		this(is, listener, DEFAULT_NOTIFICATION_THRESHOLD);
	}

	public ProgressInputStream(InputStream is, ProgressListener listener, int notifyThresHold) {
		super(is);
		if (is == null || listener == null) {
			throw new IllegalArgumentException();
		}
		this.listener = listener;
		this.notifyThresHold = notifyThresHold;
	}

	@Override
	public int read() throws IOException {
		if (!hasBeenRead) {
			onFirstRead();
			hasBeenRead = true;
		}
		int ch = super.read();
		if (ch == -1) {
			eof();
		} else {
			onBytesRead(1);
		}
		return ch;
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		onReset();
		unnotifiedByteCount = 0;
		notifiedByteCount = 0;
	}

	@Override
	public int read(byte b[]) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (!hasBeenRead) {
			onFirstRead();
			hasBeenRead = true;
		}
		int bytesRead = super.read(b, off, len);
		if (bytesRead == -1) {
			eof();
		} else {
			onBytesRead(bytesRead);
		}
		return bytesRead;
	}

	protected void onFirstRead() {
	}

	protected void onEOF() {
	}

	protected void onClose() {
		eof();
	}

	protected void onReset() {
	}

	protected void onNotifyBytesRead() {
	}

	/**
	 * On bytes read
	 * 
	 * @param bytesRead
	 */
	private void onBytesRead(int bytesRead) {
		unnotifiedByteCount += bytesRead;
		if (unnotifiedByteCount >= notifyThresHold) {
			onNotifyBytesRead();
			notifiedByteCount += unnotifiedByteCount;
			unnotifiedByteCount = 0;
		}
	}

	/**
	 * On EOF
	 */
	private void eof() {
		if (doneEOF)
			return;
		onEOF();
		unnotifiedByteCount = 0;
		doneEOF = true;
	}

	final public InputStream getOrigInputStream() {
		return in;
	}

	final protected int getUnnotifiedByteCount() {
		return unnotifiedByteCount;
	}

	final protected long getNotifiedByteCount() {
		return notifiedByteCount;
	}

	@Override
	public void close() throws IOException {
		onClose();
		super.close();
	}

	/**
	 * Gets {@link ProgressListener}
	 * 
	 * @return
	 */
	final public ProgressListener getListener() {
		return listener;
	}

	final private static int DEFAULT_NOTIFICATION_THRESHOLD = 8 * 1024;

}
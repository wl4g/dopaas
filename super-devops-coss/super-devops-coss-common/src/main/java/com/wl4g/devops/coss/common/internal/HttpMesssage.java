package com.wl4g.devops.coss.common.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

/**
 * Common class for both HTTP request and HTTP response.
 */
public abstract class HttpMesssage {

	private Map<String, String> headers = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);;
	private InputStream content;
	private long contentLength;

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public void addHeader(String key, String value) {
		this.headers.put(key, value);
	}

	public InputStream getContent() {
		return content;
	}

	public void setContent(InputStream content) {
		this.content = content;
	}

	public long getContentLength() {
		return contentLength;
	}

	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}

	public void close() throws IOException {
		if (content != null) {
			content.close();
			content = null;
		}
	}

}
package com.wl4g.devops.coss.client.channel.netty.codec;

import java.io.IOException;
import java.io.InputStream;

import com.wl4g.devops.coss.client.channel.netty.HttpHeaders;

/**
 * {@link HttpInputMessage} that can eventually stores a Jackson view that will
 * be used to deserialize the message.
 */
public class MappingJacksonInputMessage implements HttpInputMessage {

	private final InputStream body;

	private final HttpHeaders headers;

	private Class<?> deserializationView;

	public MappingJacksonInputMessage(InputStream body, HttpHeaders headers) {
		this.body = body;
		this.headers = headers;
	}

	public MappingJacksonInputMessage(InputStream body, HttpHeaders headers, Class<?> deserializationView) {
		this(body, headers);
		this.deserializationView = deserializationView;
	}

	@Override
	public InputStream getBody() throws IOException {
		return this.body;
	}

	@Override
	public HttpHeaders getHeaders() {
		return this.headers;
	}

	public void setDeserializationView(Class<?> deserializationView) {
		this.deserializationView = deserializationView;
	}

	public Class<?> getDeserializationView() {
		return this.deserializationView;
	}

}

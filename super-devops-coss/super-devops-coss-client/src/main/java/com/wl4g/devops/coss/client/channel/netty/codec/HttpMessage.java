package com.wl4g.devops.coss.client.channel.netty.codec;

import com.wl4g.devops.coss.client.channel.netty.HttpHeaders;

/**
 * Represents the base interface for HTTP request and response messages.
 * Consists of {@link HttpHeaders}, retrievable via {@link #getHeaders()}.
 *
 */
public interface HttpMessage {

	/**
	 * Return the headers of this message.
	 * 
	 * @return a corresponding HttpHeaders object (never {@code null})
	 */
	HttpHeaders getHeaders();

}

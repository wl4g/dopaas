package com.wl4g.devops.components.tools.common.remoting;

import java.io.IOException;
import java.io.OutputStream;

import com.wl4g.devops.components.tools.common.remoting.parse.HttpOutputMessage;

/**
 * Represents an HT TP output message that allows for setting a streaming body.
 * Note that such messages typically do not support {@link #getBody()} access.
 *
 * @see #setBody
 */
public interface StreamingHttpOutputMessage extends HttpOutputMessage {

	/**
	 * Set the streaming body callback for this message.
	 * 
	 * @param body
	 *            the streaming body callback
	 */
	void setBody(Body body);

	/**
	 * Defines the contract for bodies that can be written directly to an
	 * {@link OutputStream}. Useful with HTTP client libraries that provide
	 * indirect access to an {@link OutputStream} via a callback mechanism.
	 */
	@FunctionalInterface
	interface Body {

		/**
		 * Write this body to the given {@link OutputStream}.
		 * 
		 * @param outputStream
		 *            the output stream to write to
		 * @throws IOException
		 *             in case of I/O errors
		 */
		void writeTo(OutputStream outputStream) throws IOException;
	}

}

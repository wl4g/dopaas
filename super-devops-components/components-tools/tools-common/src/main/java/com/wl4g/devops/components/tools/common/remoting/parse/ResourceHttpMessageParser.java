package com.wl4g.devops.components.tools.common.remoting.parse;

import static java.lang.String.format;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.wl4g.devops.components.tools.common.annotation.Nullable;
import com.wl4g.devops.components.tools.common.io.ByteStreamUtils;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpMediaType;
import com.wl4g.devops.components.tools.common.remoting.standard.MediaTypeFactory;
import com.wl4g.devops.components.tools.common.resource.ByteArrayStreamResource;
import com.wl4g.devops.components.tools.common.resource.InputStreamResource;
import com.wl4g.devops.components.tools.common.resource.StreamResource;

/**
 * Implementation of {@link HttpMessageConverter} that can read/write
 * {@link Resource Resources} and supports byte range requests.
 *
 * <p>
 * By default, this converter can read all media types. The
 * {@link MediaTypeFactory} is used to determine the {@code Content-Type} of
 * written resources.
 */
public class ResourceHttpMessageParser extends AbstractHttpMessageParser<StreamResource> {

	private final boolean supportsReadStreaming;

	/**
	 * Create a new instance of the {@code ResourceHttpMessageConverter} that
	 * supports read streaming, i.e. can convert an {@code HttpInputMessage} to
	 * {@code InputStreamResource}.
	 */
	public ResourceHttpMessageParser() {
		super(HttpMediaType.ALL);
		this.supportsReadStreaming = true;
	}

	/**
	 * Create a new instance of the {@code ResourceHttpMessageConverter}.
	 * 
	 * @param supportsReadStreaming
	 *            whether the converter should support read streaming, i.e.
	 *            convert to {@code InputStreamResource}
	 * @since 5.0
	 */
	public ResourceHttpMessageParser(boolean supportsReadStreaming) {
		super(HttpMediaType.ALL);
		this.supportsReadStreaming = supportsReadStreaming;
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return StreamResource.class.isAssignableFrom(clazz);
	}

	@Override
	protected StreamResource readInternal(Class<? extends StreamResource> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {

		if (this.supportsReadStreaming && InputStreamResource.class == clazz) {
			return new InputStreamResource(inputMessage.getBody()) {
				@Override
				public String getFilename() {
					return inputMessage.getHeaders().getContentDisposition().getFilename();
				}

				@Override
				public long contentLength() throws IOException {
					long length = inputMessage.getHeaders().getContentLength();
					return (length != -1 ? length : super.contentLength());
				}
			};
		} else if (StreamResource.class == clazz || ByteArrayStreamResource.class.isAssignableFrom(clazz)) {
			InputStream in = inputMessage.getBody();
			long bytes = in.available();
			if (bytes >= DEFAULT_MEMORY_FILE_BYTES_LIMIT) {
				throw new IOException(format(
						"This file object is too large, about %s bytes. It is recommended to use the request with accept ranges header to support interrupt download",
						bytes));
			}
			byte[] body = ByteStreamUtils.copyToByteArray(in);
			return new ByteArrayStreamResource(body) {
				@Override
				@Nullable
				public String getFilename() {
					return inputMessage.getHeaders().getContentDisposition().getFilename();
				}
			};
		} else {
			throw new HttpMessageNotReadableException("Unsupported resource class: " + clazz);
		}
	}

	@Override
	protected HttpMediaType getDefaultContentType(StreamResource resource) {
		return MediaTypeFactory.getMediaType(resource).orElse(HttpMediaType.APPLICATION_OCTET_STREAM);
	}

	@Override
	protected Long getContentLength(StreamResource resource, @Nullable HttpMediaType contentType) throws IOException {
		// Don't try to determine contentLength on InputStreamResource - cannot
		// be read afterwards...
		// Note: custom InputStreamResource subclasses could provide a
		// pre-calculated content length!
		if (InputStreamResource.class == resource.getClass()) {
			return null;
		}
		long contentLength = resource.contentLength();
		return (contentLength < 0 ? null : contentLength);
	}

	@Override
	protected void writeInternal(StreamResource resource, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {

		writeContent(resource, outputMessage);
	}

	protected void writeContent(StreamResource resource, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		try {
			InputStream in = resource.getInputStream();
			try {
				ByteStreamUtils.copy(in, outputMessage.getBody());
			} catch (NullPointerException ex) {
				// ignore, see SPR-13620
			} finally {
				try {
					in.close();
				} catch (Throwable ex) {
					// ignore, see SPR-12999
				}
			}
		} catch (FileNotFoundException ex) {
			// ignore, see SPR-12999
		}
	}

	final public static long DEFAULT_MEMORY_FILE_BYTES_LIMIT = 1024 * 1024 * 10;

}
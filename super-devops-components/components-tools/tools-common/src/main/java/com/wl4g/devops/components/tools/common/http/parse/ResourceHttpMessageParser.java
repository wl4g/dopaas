package com.wl4g.devops.components.tools.common.http.parse;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

import com.wl4g.devops.components.tools.common.http.HttpMediaType;
import com.wl4g.devops.components.tools.common.io.ByteStreamUtils;
import com.wl4g.devops.components.tools.common.lang.ClassUtils2;
import com.wl4g.devops.components.tools.common.resource.ByteArrayStreamResource;
import com.wl4g.devops.components.tools.common.resource.ClassPathStreamResource;
import com.wl4g.devops.components.tools.common.resource.InputStreamResource;
import com.wl4g.devops.components.tools.common.resource.StreamResource;

/**
 * Implementation of {@link HttpMessageParser} that can read/write
 * {@link Resource Resources} and supports byte range requests.
 *
 * <p>
 * By default, this converter can read all media types. The Java Activation
 * Framework (JAF) - if available - is used to determine the
 * {@code Content-Type} of written resources. If JAF is not available,
 * {@code application/octet-stream} is used.
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @author Kazuki Shimizu
 * @since 3.0.2
 */
public class ResourceHttpMessageParser extends AbstractHttpMessageParser<StreamResource> {

	private static final boolean jafPresent = ClassUtils2.isPresent("javax.activation.FileTypeMap",
			ResourceHttpMessageParser.class.getClassLoader());

	public ResourceHttpMessageParser() {
		super(HttpMediaType.ALL);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return StreamResource.class.isAssignableFrom(clazz);
	}

	@Override
	protected StreamResource readInternal(Class<? extends StreamResource> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {

		if (InputStreamResource.class == clazz) {
			return new InputStreamResource(inputMessage.getBody());
		} else if (clazz.isAssignableFrom(ByteArrayStreamResource.class)) {
			byte[] body = ByteStreamUtils.copyToByteArray(inputMessage.getBody());
			return new ByteArrayStreamResource(body);
		} else {
			throw new IllegalStateException("Unsupported resource class: " + clazz);
		}
	}

	@Override
	protected HttpMediaType getDefaultContentType(StreamResource resource) {
		if (jafPresent) {
			return ActivationMediaTypeFactory.getMediaType(resource);
		} else {
			return HttpMediaType.APPLICATION_OCTET_STREAM;
		}
	}

	@Override
	protected Long getContentLength(StreamResource resource, HttpMediaType contentType) throws IOException {
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

	/**
	 * Resolve {@code MediaType} for a given {@link Resource} using JAF.
	 */
	static class ActivationMediaTypeFactory {

		private static final FileTypeMap fileTypeMap;

		static {
			fileTypeMap = loadFileTypeMapFromContextSupportModule();
		}

		private static FileTypeMap loadFileTypeMapFromContextSupportModule() {
			// See if we can find the extended mime.types from the
			// context-support
			// module...
			StreamResource mappingLocation = new ClassPathStreamResource("org/springframework/mail/javamail/mime.types");
			if (mappingLocation.exists()) {
				InputStream inputStream = null;
				try {
					inputStream = mappingLocation.getInputStream();
					return new MimetypesFileTypeMap(inputStream);
				} catch (IOException ex) {
					// ignore
				} finally {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (IOException ex) {
							// ignore
						}
					}
				}
			}
			return FileTypeMap.getDefaultFileTypeMap();
		}

		public static HttpMediaType getMediaType(StreamResource resource) {
			String filename = resource.getFilename();
			if (filename != null) {
				String mediaType = fileTypeMap.getContentType(filename);
				if (!isBlank(mediaType)) {
					return HttpMediaType.parseMediaType(mediaType);
				}
			}
			return null;
		}
	}

}

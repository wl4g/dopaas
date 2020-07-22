/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wl4g.devops.components.tools.common.remoting.parse;

import static com.wl4g.devops.components.tools.common.lang.Assert2.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeUtility;

import com.wl4g.devops.components.tools.common.annotation.Nullable;
import com.wl4g.devops.components.tools.common.collection.CollectionUtils2;
import com.wl4g.devops.components.tools.common.collection.multimap.LinkedMultiValueMap;
import com.wl4g.devops.components.tools.common.collection.multimap.MultiValueMap;
import com.wl4g.devops.components.tools.common.io.ByteStreamUtils;
import com.wl4g.devops.components.tools.common.lang.StringUtils2;
import com.wl4g.devops.components.tools.common.remoting.HttpEntity;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpHeaders;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpMediaType;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpMimeType.MimeTypeUtils;
import com.wl4g.devops.components.tools.common.resource.StreamResource;

/**
 * Implementation of {@link HttpMessageParser} to read and write 'normal' HTML
 * forms and also to write (but not read) multipart data (e.g. file uploads).
 *
 * <p>
 * In other words, this converter can read and write the
 * {@code "application/x-www-form-urlencoded"} media type as
 * {@link MultiValueMap MultiValueMap&lt;String, String&gt;}, and it can also
 * write (but not read) the {@code "multipart/form-data"} and
 * {@code "multipart/mixed"} media types as {@link MultiValueMap
 * MultiValueMap&lt;String, Object&gt;}.
 *
 * <h3>Multipart Data</h3>
 *
 * <p>
 * By default, {@code "multipart/form-data"} is used as the content type when
 * {@linkplain #write writing} multipart data. As it is also possible to write
 * multipart data using other multipart subtypes such as
 * {@code "multipart/mixed"} and {@code "multipart/related"}, as long as the
 * multipart subtype is registered as a {@linkplain #getSupportedHttpMediaTypes
 * supported media type} <em>and</em> the desired multipart subtype is specified
 * as the content type when {@linkplain #write writing} the multipart data. Note
 * that {@code "multipart/mixed"} is registered as a supported media type by
 * default.
 *
 * <p>
 * When writing multipart data, this converter uses other
 * {@link HttpMessageParser HttpMessageParsers} to write the respective MIME
 * parts. By default, basic converters are registered for byte array,
 * {@code String}, and {@code Resource}. These can be overridden via
 * {@link #setPartParsers} or augmented via {@link #addPartParser}.
 *
 * <h3>Examples</h3>
 *
 * <p>
 * The following snippet shows how to submit an HTML form using the
 * {@code "multipart/form-data"} content type.
 *
 * <pre class="code">
 * RestClient restClient = new RestClient();
 * // AllEncompassingFormHttpMessageParser is configured by default
 *
 * MultiValueMap&lt;String, Object&gt; form = new LinkedMultiValueMap&lt;&gt;();
 * form.add("field 1", "value 1");
 * form.add("field 2", "value 2");
 * form.add("field 2", "value 3");
 * form.add("field 3", 4); // non-String form values supported as of 5.1.4
 *
 * restClient.postForLocation("https://example.com/myForm", form);
 * </pre>
 *
 * <p>
 * The following snippet shows how to do a file upload using the
 * {@code "multipart/form-data"} content type.
 *
 * <pre class="code">
 * MultiValueMap&lt;String, Object&gt; parts = new LinkedMultiValueMap&lt;&gt;();
 * parts.add("field 1", "value 1");
 * parts.add("file", new ClassPathResource("myFile.jpg"));
 *
 * restClient.postForLocation("https://example.com/myFileUpload", parts);
 * </pre>
 *
 * <p>
 * The following snippet shows how to do a file upload using the
 * {@code "multipart/mixed"} content type.
 *
 * <pre class="code">
 * MultiValueMap&lt;String, Object&gt; parts = new LinkedMultiValueMap&lt;&gt;();
 * parts.add("field 1", "value 1");
 * parts.add("file", new ClassPathResource("myFile.jpg"));
 *
 * HttpHeaders requestHeaders = new HttpHeaders();
 * requestHeaders.setContentType(HttpMediaType.MULTIPART_MIXED);
 *
 * restClient.postForLocation("https://example.com/myFileUpload", new HttpEntity&lt;&gt;(parts, requestHeaders));
 * </pre>
 *
 * <p>
 * The following snippet shows how to do a file upload using the
 * {@code "multipart/related"} content type.
 *
 * <pre class="code">
 * HttpMediaType multipartRelated = new HttpMediaType("multipart", "related");
 *
 * restClient.getMessageParsers().stream().filter(FormHttpMessageParser.class::isInstance).map(FormHttpMessageParser.class::cast)
 * 		.findFirst().orElseThrow(() -&gt; new IllegalStateException("Failed to find FormHttpMessageParser"))
 * 		.addSupportedHttpMediaTypes(multipartRelated);
 *
 * MultiValueMap&lt;String, Object&gt; parts = new LinkedMultiValueMap&lt;&gt;();
 * parts.add("field 1", "value 1");
 * parts.add("file", new ClassPathResource("myFile.jpg"));
 *
 * HttpHeaders requestHeaders = new HttpHeaders();
 * requestHeaders.setContentType(multipartRelated);
 *
 * restClient.postForLocation("https://example.com/myFileUpload", new HttpEntity&lt;&gt;(parts, requestHeaders));
 * </pre>
 *
 * <h3>Miscellaneous</h3>
 *
 * <p>
 * Some methods in this class were inspired by
 * {@code org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity}.
 *
 * @see {@link AllEncompassingFormHttpMessageParser}
 * @see {@link MultiValueMap}
 */
public class FormHttpMessageParser implements HttpMessageParser<MultiValueMap<String, ?>> {

	/**
	 * The default charset used by the converter.
	 */
	public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

	static final HttpMediaType MULTIPART_ALL = new HttpMediaType("multipart", "*");

	private static final HttpMediaType DEFAULT_FORM_DATA_MEDIA_TYPE = new HttpMediaType(HttpMediaType.APPLICATION_FORM_URLENCODED,
			DEFAULT_CHARSET);

	private List<HttpMediaType> supportedHttpMediaTypes = new ArrayList<>();

	private List<HttpMessageParser<?>> partParsers = new ArrayList<>();

	private Charset charset = DEFAULT_CHARSET;

	@Nullable
	private Charset multipartCharset;

	public FormHttpMessageParser() {
		this.supportedHttpMediaTypes.add(HttpMediaType.APPLICATION_FORM_URLENCODED);
		this.supportedHttpMediaTypes.add(HttpMediaType.MULTIPART_FORM_DATA);
		this.supportedHttpMediaTypes.add(HttpMediaType.MULTIPART_MIXED);

		this.partParsers.add(new ByteArrayHttpMessageParser());
		this.partParsers.add(new StringHttpMessageParser());
		this.partParsers.add(new ResourceHttpMessageParser());

		applyDefaultCharset();
	}

	/**
	 * Set the list of {@link HttpMediaType} objects supported by this
	 * converter.
	 * 
	 * @see #addSupportedHttpMediaTypes(HttpMediaType...)
	 * @see #getSupportedHttpMediaTypes()
	 */
	public void setSupportedHttpMediaTypes(List<HttpMediaType> supportedHttpMediaTypes) {
		notNull(supportedHttpMediaTypes, "'supportedHttpMediaTypes' must not be null");
		// Ensure internal list is mutable.
		this.supportedHttpMediaTypes = new ArrayList<>(supportedHttpMediaTypes);
	}

	/**
	 * Add {@link HttpMediaType} objects to be supported by this converter.
	 * <p>
	 * The supplied {@code HttpMediaType} objects will be appended to the list
	 * of {@linkplain #getSupportedHttpMediaTypes() supported HttpMediaType
	 * objects}.
	 * 
	 * @param supportedHttpMediaTypes
	 *            a var-args list of {@code HttpMediaType} objects to add
	 * @since 5.2
	 * @see #setSupportedHttpMediaTypes(List)
	 */
	public void addSupportedHttpMediaTypes(HttpMediaType... supportedHttpMediaTypes) {
		notNull(supportedHttpMediaTypes, "'supportedHttpMediaTypes' must not be null");
		noNullElements(supportedHttpMediaTypes, "'supportedHttpMediaTypes' must not contain null elements");
		Collections.addAll(this.supportedHttpMediaTypes, supportedHttpMediaTypes);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #setSupportedHttpMediaTypes(List)
	 * @see #addSupportedHttpMediaTypes(HttpMediaType...)
	 */
	@Override
	public List<HttpMediaType> getSupportedMediaTypes() {
		return Collections.unmodifiableList(this.supportedHttpMediaTypes);
	}

	/**
	 * Set the message body converters to use. These converters are used to
	 * convert objects to MIME parts.
	 */
	public void setPartParsers(List<HttpMessageParser<?>> partParsers) {
		notEmpty(partParsers, "'partParsers' must not be empty");
		this.partParsers = partParsers;
	}

	/**
	 * Add a message body converter. Such a converter is used to convert objects
	 * to MIME parts.
	 */
	public void addPartParser(HttpMessageParser<?> partParser) {
		notNull(partParser, "'partParser' must not be null");
		this.partParsers.add(partParser);
	}

	/**
	 * Set the default character set to use for reading and writing form data
	 * when the request or response {@code Content-Type} header does not
	 * explicitly specify it.
	 * <p>
	 * As of 4.3, this is also used as the default charset for the conversion of
	 * text bodies in a multipart request.
	 * <p>
	 * As of 5.0, this is also used for part headers including
	 * {@code Content-Disposition} (and its filename parameter) unless (the
	 * mutually exclusive) {@link #setMultipartCharset multipartCharset} is also
	 * set, in which case part headers are encoded as ASCII and <i>filename</i>
	 * is encoded with the {@code encoded-word} syntax from RFC 2047.
	 * <p>
	 * By default this is set to "UTF-8".
	 */
	public void setCharset(@Nullable Charset charset) {
		if (charset != this.charset) {
			this.charset = (charset != null ? charset : DEFAULT_CHARSET);
			applyDefaultCharset();
		}
	}

	/**
	 * Apply the configured charset as a default to registered part converters.
	 */
	private void applyDefaultCharset() {
		for (HttpMessageParser<?> candidate : this.partParsers) {
			if (candidate instanceof AbstractHttpMessageParser) {
				AbstractHttpMessageParser<?> converter = (AbstractHttpMessageParser<?>) candidate;
				// Only override default charset if the converter operates with
				// a charset to begin with...
				if (converter.getDefaultCharset() != null) {
					converter.setDefaultCharset(this.charset);
				}
			}
		}
	}

	/**
	 * Set the character set to use when writing multipart data to encode file
	 * names. Encoding is based on the {@code encoded-word} syntax defined in
	 * RFC 2047 and relies on {@code MimeUtility} from {@code javax.mail}.
	 * <p>
	 * As of 5.0 by default part headers, including {@code Content-Disposition}
	 * (and its filename parameter) will be encoded based on the setting of
	 * {@link #setCharset(Charset)} or {@code UTF-8} by default.
	 * 
	 * @since 4.1.1
	 * @see <a href=
	 *      "https://en.wikipedia.org/wiki/MIME#Encoded-Word">Encoded-Word</a>
	 */
	public void setMultipartCharset(Charset charset) {
		this.multipartCharset = charset;
	}

	@Override
	public boolean canRead(Class<?> clazz, @Nullable HttpMediaType mediaType) {
		if (!MultiValueMap.class.isAssignableFrom(clazz)) {
			return false;
		}
		if (mediaType == null) {
			return true;
		}
		for (HttpMediaType supportedHttpMediaType : getSupportedMediaTypes()) {
			if (MULTIPART_ALL.includes(supportedHttpMediaType)) {
				// We can't read multipart, so skip this supported media type.
				continue;
			}
			if (supportedHttpMediaType.includes(mediaType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canWrite(Class<?> clazz, @Nullable HttpMediaType mediaType) {
		if (!MultiValueMap.class.isAssignableFrom(clazz)) {
			return false;
		}
		if (mediaType == null || HttpMediaType.ALL.equals(mediaType)) {
			return true;
		}
		for (HttpMediaType supportedHttpMediaType : getSupportedMediaTypes()) {
			if (supportedHttpMediaType.isCompatibleWith(mediaType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public MultiValueMap<String, String> read(@Nullable Class<? extends MultiValueMap<String, ?>> clazz,
			HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {

		HttpMediaType contentType = inputMessage.getHeaders().getContentType();
		Charset charset = (contentType != null && contentType.getCharset() != null ? contentType.getCharset() : this.charset);
		String body = ByteStreamUtils.copyToString(inputMessage.getBody(), charset);

		String[] pairs = StringUtils2.tokenizeToStringArray(body, "&");
		MultiValueMap<String, String> result = new LinkedMultiValueMap<>(pairs.length);
		for (String pair : pairs) {
			int idx = pair.indexOf('=');
			if (idx == -1) {
				result.add(URLDecoder.decode(pair, charset.name()), null);
			} else {
				String name = URLDecoder.decode(pair.substring(0, idx), charset.name());
				String value = URLDecoder.decode(pair.substring(idx + 1), charset.name());
				result.add(name, value);
			}
		}
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void write(MultiValueMap<String, ?> map, @Nullable HttpMediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {

		if (isMultipart(map, contentType)) {
			writeMultipart((MultiValueMap<String, Object>) map, contentType, outputMessage);
		} else {
			writeForm((MultiValueMap<String, Object>) map, contentType, outputMessage);
		}
	}

	private boolean isMultipart(MultiValueMap<String, ?> map, @Nullable HttpMediaType contentType) {
		if (contentType != null) {
			return MULTIPART_ALL.includes(contentType);
		}
		for (List<?> values : map.values()) {
			for (Object value : values) {
				if (value != null && !(value instanceof String)) {
					return true;
				}
			}
		}
		return false;
	}

	private void writeForm(MultiValueMap<String, Object> formData, @Nullable HttpMediaType contentType,
			HttpOutputMessage outputMessage) throws IOException {

		contentType = getFormContentType(contentType);
		outputMessage.getHeaders().setContentType(contentType);

		Charset charset = contentType.getCharset();
		notNull(charset, "No charset"); // should never occur

		byte[] bytes = serializeForm(formData, charset).getBytes(charset);
		outputMessage.getHeaders().setContentLength(bytes.length);

		if (outputMessage instanceof StreamingHttpOutputMessage) {
			StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage) outputMessage;
			streamingOutputMessage.setBody(outputStream -> ByteStreamUtils.copy(bytes, outputStream));
		} else {
			ByteStreamUtils.copy(bytes, outputMessage.getBody());
		}
	}

	/**
	 * Return the content type used to write forms,notNull given the preferred
	 * content type. By default, this method returns the given content type, but
	 * adds the {@linkplain #setCharset(Charset) charset} if it does not have
	 * one. If {@code contentType} is {@code null},
	 * {@code application/x-www-form-urlencoded; charset=UTF-8} is returned.
	 * <p>
	 * Subclasses can override this method to change this behavior.
	 * 
	 * @param contentType
	 *            the preferred content type (can be {@code null})
	 * @return the content type to be used
	 * @since 5.2.2
	 */
	protected HttpMediaType getFormContentType(@Nullable HttpMediaType contentType) {
		if (contentType == null) {
			return DEFAULT_FORM_DATA_MEDIA_TYPE;
		} else if (contentType.getCharset() == null) {
			return new HttpMediaType(contentType, this.charset);
		} else {
			return contentType;
		}
	}

	protected String serializeForm(MultiValueMap<String, Object> formData, Charset charset) {
		StringBuilder builder = new StringBuilder();
		formData.forEach((name, values) -> {
			if (name == null) {
				isTrue(CollectionUtils2.isEmpty(values), "Null name in form data: " + formData);
				return;
			}
			values.forEach(value -> {
				try {
					if (builder.length() != 0) {
						builder.append('&');
					}
					builder.append(URLEncoder.encode(name, charset.name()));
					if (value != null) {
						builder.append('=');
						builder.append(URLEncoder.encode(String.valueOf(value), charset.name()));
					}
				} catch (UnsupportedEncodingException ex) {
					throw new IllegalStateException(ex);
				}
			});
		});

		return builder.toString();
	}

	private void writeMultipart(MultiValueMap<String, Object> parts, @Nullable HttpMediaType contentType,
			HttpOutputMessage outputMessage) throws IOException {

		// If the supplied content type is null, fall back to
		// multipart/form-data.
		// Otherwise rely on the fact that isMultipart() already verified the
		// supplied content type is multipart.
		if (contentType == null) {
			contentType = HttpMediaType.MULTIPART_FORM_DATA;
		}

		byte[] boundary = generateMultipartBoundary();
		Map<String, String> parameters = new LinkedHashMap<>(2);
		if (!isFilenameCharsetSet()) {
			parameters.put("charset", this.charset.name());
		}
		parameters.put("boundary", new String(boundary, StandardCharsets.US_ASCII));

		// Add parameters to output content type
		contentType = new HttpMediaType(contentType, parameters);
		outputMessage.getHeaders().setContentType(contentType);

		if (outputMessage instanceof StreamingHttpOutputMessage) {
			StreamingHttpOutputMessage streamingOutputMessage = (StreamingHttpOutputMessage) outputMessage;
			streamingOutputMessage.setBody(outputStream -> {
				writeParts(outputStream, parts, boundary);
				writeEnd(outputStream, boundary);
			});
		} else {
			writeParts(outputMessage.getBody(), parts, boundary);
			writeEnd(outputMessage.getBody(), boundary);
		}
	}

	/**
	 * When {@link #setMultipartCharset(Charset)} is configured (i.e. RFC 2047,
	 * {@code encoded-word} syntax) we need to use ASCII for part headers, or
	 * otherwise we encode directly using the configured
	 * {@link #setCharset(Charset)}.
	 */
	private boolean isFilenameCharsetSet() {
		return (this.multipartCharset != null);
	}

	private void writeParts(OutputStream os, MultiValueMap<String, Object> parts, byte[] boundary) throws IOException {
		for (Map.Entry<String, List<Object>> entry : parts.entrySet()) {
			String name = entry.getKey();
			for (Object part : entry.getValue()) {
				if (part != null) {
					writeBoundary(os, boundary);
					writePart(name, getHttpEntity(part), os);
					writeNewLine(os);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void writePart(String name, HttpEntity<?> partEntity, OutputStream os) throws IOException {
		Object partBody = partEntity.getBody();
		if (partBody == null) {
			throw new IllegalStateException("Empty body for part '" + name + "': " + partEntity);
		}
		Class<?> partType = partBody.getClass();
		HttpHeaders partHeaders = partEntity.getHeaders();
		HttpMediaType partContentType = partHeaders.getContentType();
		for (HttpMessageParser<?> messageParser : this.partParsers) {
			if (messageParser.canWrite(partType, partContentType)) {
				Charset charset = isFilenameCharsetSet() ? StandardCharsets.US_ASCII : this.charset;
				HttpOutputMessage multipartMessage = new MultipartHttpOutputMessage(os, charset);
				multipartMessage.getHeaders().setContentDispositionFormData(name, getFilename(partBody));
				if (!partHeaders.isEmpty()) {
					multipartMessage.getHeaders().putAll(partHeaders);
				}
				((HttpMessageParser<Object>) messageParser).write(partBody, partContentType, multipartMessage);
				return;
			}
		}
		throw new HttpMessageNotWritableException("Could not write request: no suitable HttpMessageParser "
				+ "found for request type [" + partType.getName() + "]");
	}

	/**
	 * Generate a multipart boundary.
	 * <p>
	 * This implementation delegates to
	 * {@link MimeTypeUtils#generateMultipartBoundary()}.
	 */
	protected byte[] generateMultipartBoundary() {
		return MimeTypeUtils.generateMultipartBoundary();
	}

	/**
	 * Return an {@link HttpEntity} for the given part Object.
	 * 
	 * @param part
	 *            the part to return an {@link HttpEntity} for
	 * @return the part Object itself it is an {@link HttpEntity}, or a newly
	 *         built {@link HttpEntity} wrapper for that part
	 */
	protected HttpEntity<?> getHttpEntity(Object part) {
		return (part instanceof HttpEntity ? (HttpEntity<?>) part : new HttpEntity<>(part));
	}

	/**
	 * Return the filename of the given multipart part. This value will be used
	 * for the {@code Content-Disposition} header.
	 * <p>
	 * The default implementation returns {@link Resource#getFilename()} if the
	 * part is a {@code Resource}, and {@code null} in other cases. Can be
	 * overridden in subclasses.
	 * 
	 * @param part
	 *            the part to determine the file name for
	 * @return the filename, or {@code null} if not known
	 */
	@Nullable
	protected String getFilename(Object part) {
		if (part instanceof StreamResource) {
			StreamResource resource = (StreamResource) part;
			String filename = resource.getFilename();
			if (filename != null && multipartCharset != null) {
				filename = MimeDelegate.encode(filename, this.multipartCharset.name());
			}
			return filename;
		} else {
			return null;
		}
	}

	private void writeBoundary(OutputStream os, byte[] boundary) throws IOException {
		os.write('-');
		os.write('-');
		os.write(boundary);
		writeNewLine(os);
	}

	private static void writeEnd(OutputStream os, byte[] boundary) throws IOException {
		os.write('-');
		os.write('-');
		os.write(boundary);
		os.write('-');
		os.write('-');
		writeNewLine(os);
	}

	private static void writeNewLine(OutputStream os) throws IOException {
		os.write('\r');
		os.write('\n');
	}

	/**
	 * Implementation of {@link HttpOutputMessage} used to write a MIME
	 * multipart.
	 */
	private static class MultipartHttpOutputMessage implements HttpOutputMessage {

		private final OutputStream outputStream;
		private final Charset charset;
		private final HttpHeaders headers = new HttpHeaders();
		private boolean headersWritten = false;

		public MultipartHttpOutputMessage(OutputStream outputStream, Charset charset) {
			this.outputStream = outputStream;
			this.charset = charset;
		}

		@Override
		public HttpHeaders getHeaders() {
			return (this.headersWritten ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers);
		}

		@Override
		public OutputStream getBody() throws IOException {
			writeHeaders();
			return this.outputStream;
		}

		private void writeHeaders() throws IOException {
			if (!this.headersWritten) {
				for (Map.Entry<String, List<String>> entry : this.headers.entrySet()) {
					byte[] headerName = getBytes(entry.getKey());
					for (String headerValueString : entry.getValue()) {
						byte[] headerValue = getBytes(headerValueString);
						this.outputStream.write(headerName);
						this.outputStream.write(':');
						this.outputStream.write(' ');
						this.outputStream.write(headerValue);
						writeNewLine(this.outputStream);
					}
				}
				writeNewLine(this.outputStream);
				this.headersWritten = true;
			}
		}

		private byte[] getBytes(String name) {
			return name.getBytes(this.charset);
		}
	}

	/**
	 * Inner class to avoid a hard dependency on the JavaMail API.
	 */
	private static class MimeDelegate {

		public static String encode(String value, String charset) {
			try {
				return MimeUtility.encodeText(value, charset, null);
			} catch (UnsupportedEncodingException ex) {
				throw new IllegalStateException(ex);
			}
		}
	}

}

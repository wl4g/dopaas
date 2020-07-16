package com.wl4g.devops.components.tools.common.remoting.uri;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.wl4g.devops.components.tools.common.annotation.Nullable;
import com.wl4g.devops.components.tools.common.io.ByteStreamUtils;
import com.wl4g.devops.components.tools.common.lang.ObjectUtils;
import com.wl4g.devops.components.tools.common.remoting.ClientHttpResponse;
import com.wl4g.devops.components.tools.common.remoting.RestClient.ResponseErrorHandler;
import com.wl4g.devops.components.tools.common.remoting.exception.HttpClientErrorException;
import com.wl4g.devops.components.tools.common.remoting.exception.HttpServerErrorException;
import com.wl4g.devops.components.tools.common.remoting.exception.UnknownHttpStatusCodeException;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpHeaders;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpMediaType;
import com.wl4g.devops.components.tools.common.remoting.standard.HttpStatus;

/**
 * Default implementation of the {@link ResponseErrorHandler} interface.
 *
 * <p>
 * This error handler checks for the status code on the
 * {@link ClientHttpResponse}: Any code with series
 * {@link HttpStatus.Series#CLIENT_ERROR} or
 * {@link HttpStatus.Series#SERVER_ERROR} is considered to be an error; this
 * behavior can be changed by overriding the {@link #hasError(HttpStatus)}
 * method. Unknown status codes will be ignored by
 * {@link #hasError(ClientHttpResponse)}.
 *
 * @see RestTemplate#setErrorHandler
 */
public class DefaultResponseErrorHandler implements ResponseErrorHandler {

	/**
	 * Delegates to {@link #hasError(HttpStatus)} (for a standard status enum
	 * value) or {@link #hasError(int)} (for an unknown status code) with the
	 * response status code.
	 * 
	 * @see ClientHttpResponse#getRawStatusCode()
	 * @see #hasError(HttpStatus)
	 * @see #hasError(int)
	 */
	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		int rawStatusCode = response.getRawStatusCode();
		HttpStatus statusCode = HttpStatus.resolve(rawStatusCode);
		return (statusCode != null ? hasError(statusCode) : hasError(rawStatusCode));
	}

	/**
	 * Template method called from {@link #hasError(ClientHttpResponse)}.
	 * <p>
	 * The default implementation checks {@link HttpStatus#isError()}. Can be
	 * overridden in subclasses.
	 * 
	 * @param statusCode
	 *            the HTTP status code as enum value
	 * @return {@code true} if the response indicates an error; {@code false}
	 *         otherwise
	 * @see HttpStatus#isError()
	 */
	protected boolean hasError(HttpStatus statusCode) {
		return statusCode.isError();
	}

	/**
	 * Template method called from {@link #hasError(ClientHttpResponse)}.
	 * <p>
	 * The default implementation checks if the given status code is
	 * {@link HttpStatus.Series#CLIENT_ERROR CLIENT_ERROR} or
	 * {@link HttpStatus.Series#SERVER_ERROR SERVER_ERROR}. Can be overridden in
	 * subclasses.
	 * 
	 * @param unknownStatusCode
	 *            the HTTP status code as raw value
	 * @return {@code true} if the response indicates an error; {@code false}
	 *         otherwise
	 * @since 4.3.21
	 * @see HttpStatus.Series#CLIENT_ERROR
	 * @see HttpStatus.Series#SERVER_ERROR
	 */
	protected boolean hasError(int unknownStatusCode) {
		HttpStatus.Series series = HttpStatus.Series.resolve(unknownStatusCode);
		return (series == HttpStatus.Series.CLIENT_ERROR || series == HttpStatus.Series.SERVER_ERROR);
	}

	/**
	 * Delegates to {@link #handleError(ClientHttpResponse, HttpStatus)} with
	 * the response status code.
	 * 
	 * @throws UnknownHttpStatusCodeException
	 *             in case of an unresolvable status code
	 * @see #handleError(ClientHttpResponse, HttpStatus)
	 */
	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		HttpStatus statusCode = HttpStatus.resolve(response.getRawStatusCode());
		if (statusCode == null) {
			String message = getErrorMessage(response.getRawStatusCode(), response.getStatusText(), getResponseBody(response),
					getCharset(response));
			throw new UnknownHttpStatusCodeException(message, response.getRawStatusCode(), response.getStatusText(),
					response.getHeaders(), getResponseBody(response), getCharset(response));
		}
		handleError(response, statusCode);
	}

	/**
	 * Return error message with details from the response body, possibly
	 * truncated:
	 * 
	 * <pre>
	 * 404 Not Found: [{'id': 123, 'message': 'my very long... (500 bytes)]
	 * </pre>
	 */
	private String getErrorMessage(int rawStatusCode, String statusText, @Nullable byte[] responseBody,
			@Nullable Charset charset) {

		String preface = rawStatusCode + " " + statusText + ": ";
		if (ObjectUtils.isEmpty(responseBody)) {
			return preface + "[no body]";
		}

		charset = charset == null ? StandardCharsets.UTF_8 : charset;
		int maxChars = 200;

		if (responseBody.length < maxChars * 2) {
			return preface + "[" + new String(responseBody, charset) + "]";
		}

		try {
			Reader reader = new InputStreamReader(new ByteArrayInputStream(responseBody), charset);
			CharBuffer buffer = CharBuffer.allocate(maxChars);
			reader.read(buffer);
			reader.close();
			buffer.flip();
			return preface + "[" + buffer.toString() + "... (" + responseBody.length + " bytes)]";
		} catch (IOException ex) {
			// should never happen
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * Handle the error in the given response with the given resolved status
	 * code.
	 * <p>
	 * The default implementation throws an {@link HttpClientErrorException} if
	 * the status code is {@link HttpStatus.Series#CLIENT_ERROR CLIENT_ERROR},
	 * an {@link HttpServerErrorException} if it is
	 * {@link HttpStatus.Series#SERVER_ERROR SERVER_ERROR}, or an
	 * {@link UnknownHttpStatusCodeException} in other cases.
	 * 
	 * @since 5.0
	 * @see HttpClientErrorException#create
	 * @see HttpServerErrorException#create
	 */
	protected void handleError(ClientHttpResponse response, HttpStatus statusCode) throws IOException {
		String statusText = response.getStatusText();
		HttpHeaders headers = response.getHeaders();
		byte[] body = getResponseBody(response);
		Charset charset = getCharset(response);
		String message = getErrorMessage(statusCode.value(), statusText, body, charset);

		switch (statusCode.series()) {
		case CLIENT_ERROR:
			throw HttpClientErrorException.create(message, statusCode, statusText, headers, body, charset);
		case SERVER_ERROR:
			throw HttpServerErrorException.create(message, statusCode, statusText, headers, body, charset);
		default:
			throw new UnknownHttpStatusCodeException(message, statusCode.value(), statusText, headers, body, charset);
		}
	}

	/**
	 * Determine the HTTP status of the given response.
	 * 
	 * @param response
	 *            the response to inspect
	 * @return the associated HTTP status
	 * @throws IOException
	 *             in case of I/O errors
	 * @throws UnknownHttpStatusCodeException
	 *             in case of an unknown status code that cannot be represented
	 *             with the {@link HttpStatus} enum
	 * @since 4.3.8
	 * @deprecated as of 5.0, in favor of
	 *             {@link #handleError(ClientHttpResponse, HttpStatus)}
	 */
	@Deprecated
	protected HttpStatus getHttpStatusCode(ClientHttpResponse response) throws IOException {
		HttpStatus statusCode = HttpStatus.resolve(response.getRawStatusCode());
		if (statusCode == null) {
			throw new UnknownHttpStatusCodeException(response.getRawStatusCode(), response.getStatusText(), response.getHeaders(),
					getResponseBody(response), getCharset(response));
		}
		return statusCode;
	}

	/**
	 * Read the body of the given response (for inclusion in a status
	 * exception).
	 * 
	 * @param response
	 *            the response to inspect
	 * @return the response body as a byte array, or an empty byte array if the
	 *         body could not be read
	 * @since 4.3.8
	 */
	protected byte[] getResponseBody(ClientHttpResponse response) {
		try {
			return ByteStreamUtils.copyToByteArray(response.getBody());
		} catch (IOException ex) {
			// ignore
		}
		return new byte[0];
	}

	/**
	 * Determine the charset of the response (for inclusion in a status
	 * exception).
	 * 
	 * @param response
	 *            the response to inspect
	 * @return the associated charset, or {@code null} if none
	 * @since 4.3.8
	 */
	@Nullable
	protected Charset getCharset(ClientHttpResponse response) {
		HttpHeaders headers = response.getHeaders();
		HttpMediaType contentType = headers.getContentType();
		return (contentType != null ? contentType.getCharset() : null);
	}

}

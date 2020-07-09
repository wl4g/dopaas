package com.wl4g.devops.components.tools.common.http.parse;

import java.io.IOException;
import java.lang.reflect.Type;

import com.wl4g.devops.components.tools.common.annotation.Nullable;
import com.wl4g.devops.components.tools.common.http.ClientHttpResponse;

/**
 * Generic callback interface u sed by {@link RestTemplate}'s retrieval methods
 * Implementations of this interface perform the actual work of extracting data
 * from a {@link ClientHttpResponse}, but don't need to worry about exception
 * handling or closing resources.
 *
 * <p>
 * Used internally by the {@link RestTemplate}, but also useful for application
 * code. There is one available factory method, see
 * {@link RestTemplate#responseEntityExtractor(Type)}.
 *
 * @param <T>
 *            the data type
 * @see RestTemplate#execute
 */
@FunctionalInterface
public interface ResponseExtractor<T> {

	/**
	 * Extract data from the given {@code ClientHttpResponse} and return it.
	 * 
	 * @param response
	 *            the HTTP response
	 * @return the extracted data
	 * @throws IOException
	 *             in case of I/O errors
	 */
	@Nullable
	T extractData(ClientHttpResponse response) throws IOException;

}

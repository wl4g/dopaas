package org.springframework.social.wechat.api.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.social.UncategorizedApiException;
import org.springframework.web.client.DefaultResponseErrorHandler;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * spring-social-wechat
 * 
 * @author <a href="mailto:larry7696@gmail.com">Larry</a>
 * @version 18.6.27
 */
public class WechatErrorHandler extends DefaultResponseErrorHandler {

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		HttpStatus statusCode = response.getStatusCode();
		Map<String, Object> errorDetails = extractErrorDetailsFromResponse(response);
		if (statusCode.series().equals(HttpStatus.Series.CLIENT_ERROR)) {
			String message = errorDetails.containsKey("errmsg") ? (String) errorDetails.get("errmsg") : "Unknown error";
			throw new UncategorizedApiException("wechat", message, null);
		}
		handleUncategorizedError(response);
	}

	private void handleUncategorizedError(ClientHttpResponse response) {
		try {
			super.handleError(response);
		} catch (Exception e) {
			throw new UncategorizedApiException("wechat", "Error consuming wechat REST api", e);
		}
	}

	private Map<String, Object> extractErrorDetailsFromResponse(ClientHttpResponse response) throws IOException {
		ObjectMapper mapper = new ObjectMapper(new JsonFactory());
		try {
			return mapper.<Map<String, Object>>readValue(response.getBody(), new TypeReference<Map<String, Object>>() {
			});
		} catch (JsonParseException e) {
			return Collections.emptyMap();
		}
	}

}

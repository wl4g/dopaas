package com.wl4g.devops.coss.client.utils;

import static com.wl4g.devops.coss.common.internal.define.COSSConstants.DEFAULT_CHARSET_NAME;
import static com.wl4g.devops.coss.common.internal.define.COSSConstants.OBJECT_NAME_MAX_LENGTH;
import static com.wl4g.devops.coss.common.internal.define.COSSConstants.RESOURCE_NAME_COMMON;
import static com.wl4g.devops.coss.common.internal.define.COSSConstants.RESOURCE_NAME_COSS;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.wl4g.devops.coss.client.config.ClientCossConfiguration;
import com.wl4g.devops.coss.common.exception.InconsistentException;
import com.wl4g.devops.coss.common.internal.ResponseHeaderOverrides;
import com.wl4g.devops.coss.common.internal.ResponseMessage;
import com.wl4g.devops.coss.common.model.ObjectMetadata;
import com.wl4g.devops.coss.common.utils.COSSHeaders;
import com.wl4g.devops.coss.common.utils.DateUtil;
import com.wl4g.devops.coss.common.utils.LocalizedManager;

public class COSSUtils {

	public static final LocalizedManager OSS_RESOURCE_MANAGER = LocalizedManager.getInstance(RESOURCE_NAME_COSS);
	public static final LocalizedManager COMMON_RESOURCE_MANAGER = LocalizedManager.getInstance(RESOURCE_NAME_COMMON);

	private static final String BUCKET_NAMING_REGEX = "^[a-z0-9][a-z0-9-]{1,61}[a-z0-9]$";

	/**
	 * Validate bucket name.
	 */
	public static boolean validateBucketName(String bucketName) {
		if (bucketName == null) {
			return false;
		}

		return bucketName.matches(BUCKET_NAMING_REGEX);
	}

	public static void ensureBucketNameValid(String bucketName) {
		if (!validateBucketName(bucketName)) {
			throw new IllegalArgumentException(OSS_RESOURCE_MANAGER.getFormattedString("BucketNameInvalid", bucketName));
		}
	}

	/**
	 * Validate object name.
	 */
	public static boolean validateObjectKey(String key) {
		if (key == null || key.length() == 0) {
			return false;
		}

		byte[] bytes = null;
		try {
			bytes = key.getBytes(DEFAULT_CHARSET_NAME);
		} catch (UnsupportedEncodingException e) {
			return false;
		}

		// Validate exculde xml unsupported chars
		char keyChars[] = key.toCharArray();
		char firstChar = keyChars[0];
		if (firstChar == '/' || firstChar == '\\') {
			return false;
		}

		return (bytes.length > 0 && bytes.length < OBJECT_NAME_MAX_LENGTH);
	}

	public static void ensureObjectKeyValid(String key) {
		if (!validateObjectKey(key)) {
			throw new IllegalArgumentException(OSS_RESOURCE_MANAGER.getFormattedString("ObjectKeyInvalid", key));
		}
	}

	public static void ensureLiveChannelNameValid(String liveChannelName) {
		if (!validateObjectKey(liveChannelName)) {
			throw new IllegalArgumentException(
					OSS_RESOURCE_MANAGER.getFormattedString("LiveChannelNameInvalid", liveChannelName));
		}
	}

	/**
	 * Make a third-level domain by appending bucket name to front of original
	 * endpoint if no binding to CNAME, otherwise use original endpoint as
	 * second-level domain directly.
	 */
	public static URI determineFinalEndpoint(URI endpoint, String bucket, ClientCossConfiguration clientConfig) {
		try {
			StringBuilder conbinedEndpoint = new StringBuilder();
			conbinedEndpoint.append(String.format("%s://", endpoint.getScheme()));
			conbinedEndpoint.append(buildCanonicalHost(endpoint, bucket, clientConfig));
			conbinedEndpoint.append(endpoint.getPort() != -1 ? String.format(":%s", endpoint.getPort()) : "");
			conbinedEndpoint.append(endpoint.getPath());
			return new URI(conbinedEndpoint.toString());
		} catch (URISyntaxException ex) {
			throw new IllegalArgumentException(ex.getMessage(), ex);
		}
	}

	private static String buildCanonicalHost(URI endpoint, String bucket, ClientCossConfiguration clientConfig) {
		String host = endpoint.getHost();

		boolean isCname = false;
		if (clientConfig.isSupportCname()) {
			isCname = cnameExcludeFilter(host, clientConfig.getCnameExcludeList());
		}

		StringBuffer cannonicalHost = new StringBuffer();
		if (bucket != null && !isCname && !clientConfig.isSLDEnabled()) {
			cannonicalHost.append(bucket).append(".").append(host);
		} else {
			cannonicalHost.append(host);
		}

		return cannonicalHost.toString();
	}

	private static boolean cnameExcludeFilter(String hostToFilter, List<String> excludeList) {
		if (hostToFilter != null && !hostToFilter.trim().isEmpty()) {
			String canonicalHost = hostToFilter.toLowerCase();
			for (String excl : excludeList) {
				if (canonicalHost.endsWith(excl)) {
					return false;
				}
			}
			return true;
		}
		throw new IllegalArgumentException("Host name can not be null.");
	}

	public static String determineResourcePath(String bucket, String key, boolean sldEnabled) {
		return sldEnabled ? makeResourcePath(bucket, key) : makeResourcePath(key);
	}

	/**
	 * Make a resource path from the object key, used when the bucket name
	 * pearing in the endpoint.
	 */
	public static String makeResourcePath(String key) {
		return key != null ? COSSUtils.urlEncodeKey(key) : null;
	}

	/**
	 * Make a resource path from the bucket name and the object key.
	 */
	public static String makeResourcePath(String bucket, String key) {
		if (bucket != null) {
			return bucket + "/" + (key != null ? COSSUtils.urlEncodeKey(key) : "");
		} else {
			return null;
		}
	}

	/**
	 * Encode object URI.
	 */
	private static String urlEncodeKey(String key) {
		StringBuffer resultUri = new StringBuffer();

		String[] keys = key.split("/");
		resultUri.append(HttpUtil.urlEncode(keys[0], DEFAULT_CHARSET_NAME));
		for (int i = 1; i < keys.length; i++) {
			resultUri.append("/").append(HttpUtil.urlEncode(keys[i], DEFAULT_CHARSET_NAME));
		}

		if (key.endsWith("/")) {
			// String#split ignores trailing empty strings,
			// e.g., "a/b/" will be split as a 2-entries array,
			// so we have to append all the trailing slash to the uri.
			for (int i = key.length() - 1; i >= 0; i--) {
				if (key.charAt(i) == '/') {
					resultUri.append("/");
				} else {
					break;
				}
			}
		}

		return resultUri.toString();
	}

	/**
	 * Populate metadata to headers.
	 */
	public static void populateRequestMetadata(Map<String, String> headers, ObjectMetadata metadata) {
		// Map<String, Object> rawMetadata = metadata.getRawMetadata();
		// if (rawMetadata != null) {
		// for (Entry<String, Object> entry : rawMetadata.entrySet()) {
		// if (entry.getKey() != null && entry.getValue() != null) {
		// String key = entry.getKey();
		// String value = entry.getValue().toString();
		// if (key != null)
		// key = key.trim();
		// if (value != null)
		// value = value.trim();
		// headers.put(key, value);
		// }
		// }
		// }

		Map<String, String> userMetadata = metadata.getUserMetadata();
		if (userMetadata != null) {
			for (Entry<String, String> entry : userMetadata.entrySet()) {
				if (entry.getKey() != null && entry.getValue() != null) {
					String key = entry.getKey();
					String value = entry.getValue();
					if (key != null)
						key = key.trim();
					if (value != null)
						value = value.trim();
					headers.put(COSSHeaders.OSS_USER_METADATA_PREFIX + key, value);
				}
			}
		}
	}

	public static void addHeader(Map<String, String> headers, String header, String value) {
		if (value != null) {
			headers.put(header, value);
		}
	}

	public static void addDateHeader(Map<String, String> headers, String header, Date value) {
		if (value != null) {
			headers.put(header, DateUtil.formatRfc822Date(value));
		}
	}

	public static void addStringListHeader(Map<String, String> headers, String header, List<String> values) {
		if (values != null && !values.isEmpty()) {
			headers.put(header, join(values));
		}
	}

	public static void removeHeader(Map<String, String> headers, String header) {
		if (header != null && headers.containsKey(header)) {
			headers.remove(header);
		}
	}

	public static String join(List<String> strings) {

		StringBuilder sb = new StringBuilder();
		boolean first = true;

		for (String s : strings) {
			if (!first) {
				sb.append(", ");
			}
			sb.append(s);

			first = false;
		}

		return sb.toString();
	}

	public static String trimQuotes(String s) {

		if (s == null) {
			return null;
		}

		s = s.trim();
		if (s.startsWith("\"")) {
			s = s.substring(1);
		}
		if (s.endsWith("\"")) {
			s = s.substring(0, s.length() - 1);
		}

		return s;
	}

	public static void populateResponseHeaderParameters(Map<String, String> params, ResponseHeaderOverrides responseHeaders) {
		if (responseHeaders != null) {
			if (responseHeaders.getCacheControl() != null) {
				params.put(ResponseHeaderOverrides.RESPONSE_HEADER_CACHE_CONTROL, responseHeaders.getCacheControl());
			}

			if (responseHeaders.getContentDisposition() != null) {
				params.put(ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_DISPOSITION, responseHeaders.getContentDisposition());
			}

			if (responseHeaders.getContentEncoding() != null) {
				params.put(ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_ENCODING, responseHeaders.getContentEncoding());
			}

			if (responseHeaders.getContentLangauge() != null) {
				params.put(ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_LANGUAGE, responseHeaders.getContentLangauge());
			}

			if (responseHeaders.getContentType() != null) {
				params.put(ResponseHeaderOverrides.RESPONSE_HEADER_CONTENT_TYPE, responseHeaders.getContentType());
			}

			if (responseHeaders.getExpires() != null) {
				params.put(ResponseHeaderOverrides.RESPONSE_HEADER_EXPIRES, responseHeaders.getExpires());
			}
		}
	}

	public static void safeCloseResponse(ResponseMessage response) {
		try {
			response.close();
		} catch (IOException e) {
		}
	}

	public static long determineInputStreamLength(InputStream instream, long hintLength) {

		if (hintLength <= 0 || !instream.markSupported()) {
			return -1;
		}

		return hintLength;
	}

	public static long determineInputStreamLength(InputStream instream, long hintLength, boolean useChunkEncoding) {

		if (useChunkEncoding) {
			return -1;
		}

		if (hintLength <= 0 || !instream.markSupported()) {
			return -1;
		}

		return hintLength;
	}

	public static String joinETags(List<String> eTags) {

		StringBuilder sb = new StringBuilder();
		boolean first = true;

		for (String eTag : eTags) {
			if (!first) {
				sb.append(", ");
			}
			sb.append(eTag);

			first = false;
		}

		return sb.toString();
	}

	/**
	 * Checks if OSS and SDK's checksum is same. If not, throws
	 * InconsistentException.
	 */
	public static void checkChecksum(Long clientChecksum, Long serverChecksum, String requestId) {
		if (clientChecksum != null && serverChecksum != null && !clientChecksum.equals(serverChecksum)) {
			throw new InconsistentException(clientChecksum, serverChecksum, requestId);
		}
	}

}

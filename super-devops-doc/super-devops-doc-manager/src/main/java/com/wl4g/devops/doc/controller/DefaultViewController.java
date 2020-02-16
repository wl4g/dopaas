/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.doc.controller;

import com.google.common.io.ByteStreams;
import com.wl4g.devops.common.web.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.devops.iam.common.config.AbstractIamProperties.DEFAULT_VIEW_BASE_URI;
import static com.wl4g.devops.tool.common.jvm.JvmRuntimeKit.isJVMDebugging;
import static com.wl4g.devops.tool.common.web.WebUtils2.isMediaRequest;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.endsWithAny;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static org.springframework.util.Assert.notNull;

/**
 * Default view controller
 *
 * @author wangl.sir
 * @version v1.0 2019年1月9日
 * @since
 */
@Controller
@RequestMapping("/view")
public class DefaultViewController extends BaseController {

	/**
	 * Default view page file cache buffer
	 */
	final private Map<String, byte[]> bufferCache = new ConcurrentHashMap<>();

	/**
	 * Reader static resource files
	 *
	 * @param filename
	 * @param response
	 */
	@GetMapping(path = "/**")
	public void readerResource(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String uri = request.getRequestURI();
		String filepath = uri.substring(uri.indexOf(DEFAULT_VIEW_BASE_URI) + DEFAULT_VIEW_BASE_URI.length());
		responseFile(filepath, response);
	}

	/**
	 * Response file
	 *
	 * @param filepath
	 * @param response
	 * @throws Exception
	 */
	protected void responseFile(String filepath, HttpServletResponse response) throws Exception {
		notNull(filepath, "'filename' must not be null");

		// Get buffer cache
		byte[] buf = bufferCache.get(filepath);
		if (isNull(buf)) {
			InputStream in = getResource(filepath);
			if (nonNull(in)) {
				log.debug("Read file path:[{}]", filepath);
				buf = ByteStreams.toByteArray(in);
				// Caching is enabled when in non-debug mode.
				if (!isJVMDebugging) {
					bufferCache.put(filepath, buf);
				}
			} else { // Not found
				write(response, NOT_FOUND.value(), TEXT_HTML_VALUE, "Not Found".getBytes(UTF_8));
				return;
			}
		}
		response.setDateHeader("Expires", System.currentTimeMillis() + 600_000);
		response.addHeader("Pragma", "Pragma");
		response.addHeader("Cache-Control", "public");
		response.addHeader("Last-Modified", String.valueOf(System.currentTimeMillis()));

		// Response file buffer.
		write(response, OK.value(), getContentType(filepath), buf);
	}

	/**
	 * Load resource file.
	 *
	 * @param filepath
	 * @return
	 */
	private InputStream getResource(String filepath) {
		String location = "/default-webapps/" + filepath;
		return getClass().getResourceAsStream(location);
	}

	/**
	 * Get content type by file path.
	 *
	 * @param filepath
	 * @return
	 */
	private String getContentType(String filepath) {
		filepath = filepath.toLowerCase(Locale.US);
		String contentType = EMPTY;
		if (endsWithAny(filepath, "html", "shtml", "htm")) {
			contentType = TEXT_HTML_VALUE;
		} else if (endsWithAny(filepath, "css")) {
			contentType = "text/css";
		} else if (endsWithAny(filepath, "js")) {
			contentType = "application/javascript";
		} else if (isMediaRequest(filepath)) {
			contentType = "image/" + org.springframework.util.StringUtils.getFilenameExtension(filepath);
		}
		return contentType;
	}

}
/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.iam.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.BEAN_DELEGATE_MSG_SOURCE;
import static com.wl4g.devops.common.utils.web.WebUtils2.cleanURI;
import static com.wl4g.devops.common.utils.web.WebUtils2.isMediaRequest;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.endsWithAny;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.wl4g.devops.common.kit.jvm.JVMRuntimeKit;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.iam.common.i18n.SessionDelegateMessageBundle;
import com.wl4g.devops.iam.config.IamProperties;

/**
 * Default view controller
 * 
 * @author wangl.sir
 * @version v1.0 2019年1月9日
 * @since
 */
@com.wl4g.devops.iam.annotation.DefaultViewController
public class DefaultViewController extends BaseController {

	final public static String URI_STATIC = "/static";

	/**
	 * Default view page file cache buffer
	 */
	final private Map<String, byte[]> bufferCache = new ConcurrentHashMap<>();

	/**
	 * Resource loader
	 */
	final private ResourceLoader loader = new DefaultResourceLoader();

	/**
	 * IAM server configuration
	 */
	@Autowired
	private IamProperties config;

	/**
	 * Session delegate message source bundle.
	 */
	@javax.annotation.Resource(name = BEAN_DELEGATE_MSG_SOURCE)
	protected SessionDelegateMessageBundle bundle;

	/**
	 * Reader view files
	 * 
	 * @param filename
	 * @param response
	 */
	@GetMapping(path = "**/{filename:.+}")
	public void readerView(@PathVariable("filename") String filename, HttpServletResponse response) throws Exception {
		responseFile(null, filename, response);
	}

	/**
	 * Reader static resource files
	 * 
	 * @param filename
	 * @param response
	 */
	@GetMapping(path = URI_STATIC + "/**")
	public void readerResource(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String uri = request.getRequestURI();
		String filepath = uri.substring(uri.indexOf(URI_STATIC) + URI_STATIC.length());
		responseFile(URI_STATIC, filepath, response);
	}

	/**
	 * Response file
	 * 
	 * @param basePath
	 * @param filepath
	 * @param response
	 * @throws Exception
	 */
	protected void responseFile(String basePath, String filepath, HttpServletResponse response) throws Exception {
		Assert.notNull(filepath, "'filename' must not be null");
		basePath = trimToEmpty(basePath);

		// Get buffer cache
		byte[] buf = bufferCache.get(filepath);
		if (Objects.isNull(buf)) {
			Resource resource = getResource(basePath, filepath);
			if (resource.exists()) {
				if (log.isInfoEnabled()) {
					log.info("Read file path:[{}]", resource.getURL());
				}
				buf = ByteStreams.toByteArray(resource.getInputStream());
				// Caching is enabled when in non-debug mode.
				if (!JVMRuntimeKit.isJVMDebugging) {
					bufferCache.put(filepath, buf);
					// response.setDateHeader("expires",
					// System.currentTimeMillis() + 600_000);
					// response.addHeader("Pragma", "Pragma");
					// response.addHeader("Cache-Control", "public");
					// response.addHeader("Last-Modified",
					// String.valueOf(System.currentTimeMillis()));
				}
			} else { // Not found
				write(response, HttpStatus.NOT_FOUND.value(), MediaType.TEXT_HTML_VALUE, "Not Found".getBytes(Charsets.UTF_8));
				return;
			}
		}

		// Response file buffer.
		write(response, HttpStatus.OK.value(), getContentType(filepath), buf);
	}

	/**
	 * Load resource file
	 * 
	 * @param basePath
	 * @param filepath
	 * @return
	 */
	private Resource getResource(String basePath, String filepath) {
		String location = new StringBuffer(config.getDefaultViewLoaderPath()).append(basePath).append("/").append(filepath)
				.toString();
		return loader.getResource(cleanURI(location));
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
			contentType = MediaType.TEXT_HTML_VALUE;
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
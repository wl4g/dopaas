/*
 * Copyright 2015 the original author or authors.
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.BEAN_DELEGATE_MSG_SOURCE;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.wl4g.devops.common.kit.jvm.JVMRuntimeKit;
import com.wl4g.devops.common.utils.web.WebUtils2;
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
	@GetMapping(path = "{filename:.+}")
	public void readerView(@PathVariable("filename") String filename, HttpServletResponse response) throws Exception {
		responseFile(null, filename, response);
	}

	/**
	 * Reader static resource files
	 * 
	 * @param filename
	 * @param response
	 */
	@GetMapping(path = URI_STATIC + "/{filename:.+}")
	public void readerResource(@PathVariable("filename") String filename, HttpServletResponse response) throws Exception {
		responseFile(URI_STATIC, filename, response);
	}

	/**
	 * Response file
	 * 
	 * @param basePath
	 * @param filename
	 * @param response
	 * @throws Exception
	 */
	protected void responseFile(String basePath, String filename, HttpServletResponse response) throws Exception {
		Assert.notNull(filename, "'filename' must not be null");
		basePath = basePath == null ? "" : basePath;

		// Get buffer cache
		byte[] buffer = bufferCache.get(filename);

		if (buffer == null) {
			Resource resource = getResource(basePath, filename);
			if (resource.exists()) {
				if (log.isInfoEnabled()) {
					log.info("Read file url:[{}]", resource.getURL());
				}
				buffer = ByteStreams.toByteArray(resource.getInputStream());

				// Caching is enabled when in non-debug mode.
				if (!JVMRuntimeKit.isJVMDebuging()) {
					bufferCache.put(filename, buffer);
				}
			}
			// Not found
			else {
				write(response, HttpStatus.NOT_FOUND.value(), MediaType.TEXT_HTML_VALUE, "Not Found".getBytes(Charsets.UTF_8));
				return;
			}
		}

		// Response file buffer
		String contentType = StringUtils.endsWithIgnoreCase(filename, "HTML") ? MediaType.TEXT_HTML_VALUE
				: MediaType.TEXT_PLAIN_VALUE;
		write(response, HttpStatus.OK.value(), contentType, buffer);
	}

	/**
	 * Load resource file
	 * 
	 * @param basePath
	 * @param filename
	 * @return
	 */
	protected Resource getResource(String basePath, String filename) {
		String location = new StringBuffer(config.getDefaultViewLoaderPath()).append(basePath).append("/").append(filename)
				.toString();
		return loader.getResource(WebUtils2.cleanURI(location));
	}

}
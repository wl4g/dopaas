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
package com.wl4g.devops.common.web.embedded;

import static org.springframework.util.StringUtils.*;

import org.springframework.web.bind.annotation.GetMapping;

import com.wl4g.devops.common.config.DefaultEmbeddedWebappsAutoConfiguration.GenericEmbeddedWebappsProperties;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.embedded.WebResourceCache.*;
import com.wl4g.devops.tool.common.resource.StreamResource;
import com.wl4g.devops.tool.common.resource.resolver.ClassPathResourcePatternResolver;
import com.wl4g.devops.tool.common.resource.resolver.ResourcePatternResolver;

import static com.google.common.io.ByteStreams.*;
import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.devops.tool.common.jvm.JvmRuntimeKit.*;
import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.tool.common.web.WebUtils2.*;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.http.MediaType.*;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.springframework.http.HttpStatus.*;

import java.io.InputStream;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Embedded webapps view controller
 *
 * @author wangl.sir
 * @version v1.0 2019年1月9日
 * @since
 */
public abstract class GenericEmbeddedWebappsEndpoint extends BaseController {

	/**
	 * Web file buffer cache
	 */
	final protected WebResourceCache cache;

	/**
	 * {@link ResourcePatternResolver}
	 */
	final protected ResourcePatternResolver resolver = new ClassPathResourcePatternResolver();

	/**
	 * {@link DefaultWebAppControllerProperties}
	 */
	final protected GenericEmbeddedWebappsProperties config;

	public GenericEmbeddedWebappsEndpoint(GenericEmbeddedWebappsProperties config) {
		this(config, new DefaultWebappsGuavaCache());
	}

	public GenericEmbeddedWebappsEndpoint(GenericEmbeddedWebappsProperties config, WebResourceCache cache) {
		notNullOf(config, "embeddedWebappControllerProperties");
		this.config = config;
		this.cache = cache;
	}

	/**
	 * Reader web resource files
	 *
	 * @param filename
	 * @param response
	 */
	@GetMapping(path = "/**")
	public void doWebResources(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String uri = request.getRequestURI();
		String filepath = uri.substring(uri.indexOf(config.getBaseUri()) + config.getBaseUri().length());
		doResponseFile(filepath, request, response);
	}

	/**
	 * Response file
	 *
	 * @param filepath
	 * @param response
	 * @throws Exception
	 */
	protected void doResponseFile(String filepath, HttpServletRequest request, HttpServletResponse response) throws Exception {
		notNullOf(filepath, "filepath");

		// Call pre-processing
		if (preResponesPropertiesSet(filepath, request)) {
			log.debug("Accessing file: {}", filepath);

			// Gets buffer cache
			byte[] buf = cache.get(filepath);
			if (isNull(buf)) {
				try (InputStream in = getResourceAsStream(filepath);) {
					if (nonNull(in)) {
						buf = toByteArray(in);
						// Decorate
						buf = decorateResource(filepath, buf);
						// Check cache?
						if (isCache(filepath, request)) {
							cache.put(filepath, buf);
						}
					} else {
						// Call post properties.
						postResponsePropertiesSet(response);
						// Response not found.
						write(response, NOT_FOUND.value(), TEXT_HTML_VALUE, "Not Found".getBytes(UTF_8));
						return;
					}
				}
			}
			// Call post properties.
			postResponsePropertiesSet(response);

			// Response file.
			write(response, OK.value(), getContentType(filepath), buf);
		} else {
			log.debug("Forbidden access file: {}", filepath);
			// Response forbidden.
			write(response, FORBIDDEN.value(), TEXT_HTML_VALUE, "Forbidden".getBytes(UTF_8));
		}
	}

	/**
	 * Enable caching or not
	 * 
	 * @param filepath
	 * @param request
	 * @return
	 */
	protected boolean isCache(String filepath, HttpServletRequest request) {
		return !isJVMDebugging;
	}

	/**
	 * Pre-processing response properties set.
	 * 
	 * @param filepath
	 * @param request
	 * @return
	 */
	protected boolean preResponesPropertiesSet(String filepath, HttpServletRequest request) {
		return true;
	}

	/**
	 * Decorate resources
	 * 
	 * @param filepath
	 * @param fileBuf
	 * @return
	 */
	protected byte[] decorateResource(String filepath, byte[] fileBuf) {
		return fileBuf;
	}

	/**
	 * Post response properties set.
	 * 
	 * @param response
	 */
	protected void postResponsePropertiesSet(HttpServletResponse response) {
		response.setDateHeader("Expires", currentTimeMillis() + 600_000);
		response.addHeader("Pragma", "Pragma");
		response.addHeader("Cache-Control", "public");
		response.addHeader("Last-Modified", valueOf(currentTimeMillis()));
	}

	/**
	 * Gets content type by file path.
	 *
	 * @param ext
	 * @return
	 */
	protected String getContentType(String ext) {
		ext = getFilenameExtension(ext.toLowerCase(Locale.US));
		return config.getMimeMapping().getProperty(ext);
	}

	/**
	 * Load resource input stream.
	 *
	 * @param path
	 * @return
	 * @throws Exception
	 */
	protected InputStream getResourceAsStream(String path) throws Exception {
		if (startsWith(path, "/")) {
			path = path.substring(1);
		}
		String location = config.getWebappLocation() + "/" + cleanURI(path);
		Set<StreamResource> ress = resolver.getResources(location);
		return !isEmpty(ress) ? ress.iterator().next().getInputStream() : null;
	}

}
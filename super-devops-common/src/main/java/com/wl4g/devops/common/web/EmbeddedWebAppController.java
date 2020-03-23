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
package com.wl4g.devops.common.web;

import static org.springframework.util.StringUtils.*;
import org.springframework.web.bind.annotation.GetMapping;

import com.wl4g.devops.common.config.EmbeddedWebAppAutoConfiguration.EmbeddedWebAppControllerProperties;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.tool.common.resource.StreamResource;
import com.wl4g.devops.tool.common.resource.resolver.ClassPathResourcePatternResolver;
import com.wl4g.devops.tool.common.resource.resolver.ResourcePatternResolver;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.devops.tool.common.jvm.JvmRuntimeKit.*;
import static com.wl4g.devops.tool.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.tool.common.web.WebUtils2.*;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.http.MediaType.*;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.springframework.http.HttpStatus.*;

import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.io.ByteStreams;

/**
 * Embedded webapps view controller
 *
 * @author wangl.sir
 * @version v1.0 2019年1月9日
 * @since
 */
public abstract class EmbeddedWebAppController extends BaseController {

	/**
	 * Default view page file cache buffer
	 */
	final private Map<String, byte[]> bufferCache = new ConcurrentHashMap<>();

	/**
	 * {@link ResourcePatternResolver}
	 */
	final private ResourcePatternResolver resolver = new ClassPathResourcePatternResolver();

	/**
	 * {@link DefaultWebAppControllerProperties}
	 */
	protected EmbeddedWebAppControllerProperties config;

	public EmbeddedWebAppController(EmbeddedWebAppControllerProperties config) {
		notNullOf(config, "embeddedWebappControllerProperties");
		this.config = config;
	}

	/**
	 * Reader static resource files
	 *
	 * @param filename
	 * @param response
	 */
	@GetMapping(path = "/**")
	public void readerResource(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String uri = request.getRequestURI();
		String filepath = uri.substring(uri.indexOf(config.getBaseUri()) + config.getBaseUri().length() + 1);
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
		notNull(filepath, "'filepath' must can't empty");

		// Get buffer cache
		byte[] buf = bufferCache.get(filepath);
		if (isNull(buf)) {
			try (InputStream in = getResourceAsStream(filepath);) {
				if (nonNull(in)) {
					log.debug("Request access file: {}", filepath);
					buf = ByteStreams.toByteArray(in);
					if (!isJVMDebugging) { // Debug mode is enabled
						bufferCache.put(filepath, buf);
					}
				} else { // Not found
					write(response, NOT_FOUND.value(), TEXT_HTML_VALUE, "Not Found".getBytes(UTF_8));
					return;
				}
			}
		}
		response.setDateHeader("Expires", currentTimeMillis() + 600_000);
		response.addHeader("Pragma", "Pragma");
		response.addHeader("Cache-Control", "public");
		response.addHeader("Last-Modified", valueOf(currentTimeMillis()));

		// Response file.
		write(response, OK.value(), getContentType(filepath), buf);
	}

	/**
	 * Get content type by file path.
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
	private InputStream getResourceAsStream(String path) throws Exception {
		if (startsWith(path, "/")) {
			path = path.substring(1);
		}
		String location = config.getWebappLocation() + "/" + cleanURI(path);
		Set<StreamResource> ress = resolver.getResources(location);
		return !isEmpty(ress) ? ress.iterator().next().getInputStream() : null;
	}

}
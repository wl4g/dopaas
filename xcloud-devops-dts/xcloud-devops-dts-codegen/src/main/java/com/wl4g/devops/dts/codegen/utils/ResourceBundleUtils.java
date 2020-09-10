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
package com.wl4g.devops.dts.codegen.utils;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static java.lang.String.format;
import static java.util.Objects.nonNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.constraints.NotBlank;

import org.springframework.util.ResourceUtils;

import com.google.common.io.Resources;
import com.wl4g.components.common.annotation.Nullable;

/**
 * {@link ResourceBundleUtils}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-10
 * @since
 */
public abstract class ResourceBundleUtils {

	/**
	 * Reading config resource file content.
	 * 
	 * @param useCache
	 * @param basePath
	 * @param type
	 * @param filename
	 * @param args
	 * @return
	 * @throws IOException
	 */
	public static String readResource(boolean useCache, @NotBlank String basePath, @NotBlank String type,
			@NotBlank String filename, @Nullable String... args) {
		hasTextOf(type, "basePath");
		hasTextOf(type, "type");
		hasTextOf(filename, "filename");

		try {
			// First get from cache
			String path = basePath.concat(type).concat("/").concat(filename);

			String content = null;
			if (useCache) {
				content = resourcesCache.get(path);
				if (nonNull(content)) {
					return content;
				}
			}

			File sqlFile = ResourceUtils.getFile("classpath:" + path);
			content = Resources.toString(sqlFile.toURI().toURL(), UTF_8);

			// Storage resource content
			if (useCache) {
				resourcesCache.put(path, content = format(content, nonNull(args) ? args : null));
			}

			return content;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	/** Resources cache map. */
	private static final Map<String, String> resourcesCache = new ConcurrentHashMap<>(4);

}

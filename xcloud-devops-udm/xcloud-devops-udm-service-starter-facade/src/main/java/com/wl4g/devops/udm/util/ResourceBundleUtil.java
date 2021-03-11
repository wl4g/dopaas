/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.devops.udm.util;

import com.google.common.io.Resources;
import com.wl4g.component.common.resource.StreamResource;
import com.wl4g.component.common.resource.resolver.ClassPathResourcePatternResolver;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.component.common.collection.CollectionUtils2.safeSet;
import static com.wl4g.component.common.lang.Assert2.hasTextOf;
import static com.wl4g.component.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.util.Objects.nonNull;

/**
 * {@link ResourceBundleUtil}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-09-10
 * @since
 */
public abstract class ResourceBundleUtil {

	/**
	 * Reading config resource file content.
	 * 
	 * @param useCache
	 * @param withLoadClass
	 * @param type
	 * @param filename
	 * @param args
	 * @return
	 * @throws IOException
	 */
	public static String readResource(@NotNull Class<?> withLoadClass, @NotBlank String subPath,
			@NotBlank String filename, boolean useCache, @Nullable String... args) {
		notNullOf(withLoadClass, "withLoadClass");
		hasTextOf(subPath, "subPath");
		hasTextOf(filename, "filename");

		try {
			// First get from cache
			String basePath = withLoadClass.getName().replace(".", "/").replace(withLoadClass.getSimpleName(), "")
					.concat(subPath.endsWith("/") ? subPath.substring(0, subPath.length() - 1) : subPath);
			String path = basePath.concat("/").concat(filename);

			String sqlStr = null;
			if (useCache) {
				sqlStr = resourcesCache.get(path);
				if (nonNull(sqlStr)) {
					return format(sqlStr, nonNull(args) ? args : null);
				}
			}

			StreamResource res = safeSet(new ClassPathResourcePatternResolver().getResources("classpath:" + path)).stream()
					.findFirst().get();
			sqlStr = Resources.toString(res.getURL(), UTF_8);

			// Storage resource content
			if (useCache) {
				resourcesCache.put(path, sqlStr);
			}

			return format(sqlStr, nonNull(args) ? args : null);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	/** Resources cache map. */
	private static final Map<String, String> resourcesCache = new ConcurrentHashMap<>(4);

}
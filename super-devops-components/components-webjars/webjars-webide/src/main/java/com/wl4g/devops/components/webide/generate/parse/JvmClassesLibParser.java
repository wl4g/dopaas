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
package com.wl4g.devops.components.webide.generate.parse;

import static com.wl4g.devops.tool.common.lang.StringUtils2.endsWithIgnoreCase;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.replace;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * {@link JvmClassesLibParser}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年4月19日 v1.0.0
 * @see
 */
public abstract class JvmClassesLibParser extends ClassLoader implements LibParser {

	/**
	 * Gets classes all in lib(jar)
	 * 
	 * @param lib
	 * @return
	 */
	protected List<Class<?>> getClassesAll(File lib) {
		List<Class<?>> classes = new ArrayList<>();
		try (JarFile jar = new JarFile(lib)) {
			Enumeration<JarEntry> en = jar.entries();
			while (en.hasMoreElements()) {
				JarEntry ent = en.nextElement();
				// Gets class name.
				String className = getPathClassName(ent.getName());
				if (isNull(className)) {
					continue;
				}
				try (InputStream in = jar.getInputStream(ent)) {
					int total = in.available();
					int len = 0;
					byte[] classBuf = new byte[total];
					while (len < total) {
						len += in.read(classBuf, len, total - len);
					}
					Class<?> clazz = defineClass(className, classBuf, 0, classBuf.length);
					resolveClass(clazz);
					classes.add(clazz);
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return classes;
	}

	/**
	 * Jar entry pathname to class name.
	 * 
	 * @param entryPath
	 * @return
	 */
	private String getPathClassName(String entryPath) {
		if (!endsWithIgnoreCase(entryPath, ".class"))
			return null;
		return replace(entryPath, File.separator, ".").replace(".class", "");
	}

}

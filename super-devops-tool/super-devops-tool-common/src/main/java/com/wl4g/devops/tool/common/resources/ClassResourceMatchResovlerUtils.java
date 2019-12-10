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
package com.wl4g.devops.tool.common.resources;

import static java.util.Objects.isNull;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.wl4g.devops.tool.common.lang.Assert.*;

/**
 * Based on CLASSPATH matching resolve scanner utility tools.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年12月3日
 * @since
 */
public abstract class ClassResourceMatchResovlerUtils {

	/**
	 * {@link AntPathMatcher} cache.
	 */
	final private static ThreadLocal<AntPathMatcher> matcherCache = ThreadLocal.withInitial(() -> new AntPathMatcher("/"));

	/**
	 * JAR matching resolve scanner.
	 * 
	 * @param locationPattern
	 *            the location pattern to resolve
	 * @param classLoader
	 * @param processor
	 * @param predicate
	 */
	public static void doSearch(String locationPattern, ClassLoader classLoader, ResolveProcessor processor,
			Predicate<String> predicate) {
		hasText(locationPattern, "Matching locationPattern can't empty.");
		notNull(processor, "ResolveProcessor can't null");
		try {
			classLoader = isNull(classLoader) ? Thread.currentThread().getContextClassLoader() : classLoader;
			Enumeration<URL> urlEn = classLoader.getResources(locationPattern.replace(".", "/"));
			while (urlEn.hasMoreElements()) {
				// Example:[jar:file:/C:/Users/ibm/.m2/repository/junit/junit/4.12/junit-4.12.jar!/org/junit]
				URL url = urlEn.nextElement();
				String protocol = url.getProtocol();
				if ("jar".equalsIgnoreCase(protocol)) {
					JarURLConnection conn = (JarURLConnection) url.openConnection();
					if (isNull(conn)) {
						continue;
					}
					JarFile jarFile = conn.getJarFile();
					if (isNull(jarFile)) {
						continue;
					}
					// Obtain jar entity files.
					Enumeration<JarEntry> jarEntryEn = jarFile.entries();
					while (jarEntryEn.hasMoreElements()) {
						// Example:[org/, org/junit/, org/junit/rules/]
						JarEntry jarEntry = jarEntryEn.nextElement();
						String pathname = jarEntry.getName();
						if (pathname.replaceAll("/", ".").startsWith(locationPattern)) {
							// if(jarEntryName.endsWith(".class"))
							// String className = jarEntryName.substring(0,
							// jarEntryName.lastIndexOf(".")).replace("/", ".");
							if (isNull(predicate) || predicate.test(pathname)) {
								processor.doResolve(ResourceType.JAR, classLoader, pathname);
							}
						}
					}
				} else if ("file".equalsIgnoreCase(protocol)) {
					String baseFilePath = locationPattern.replace(".", File.separator);
					String classPath = url.getPath().replace(locationPattern.replace(".", "/"), "");
					String rootFile = classPath + baseFilePath;
					doFindHierarchyPath(new File(rootFile), locationPattern, classLoader, processor, predicate, true);
				}
			}
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	/**
	 * Do find hierarchy matching of path.
	 * 
	 * @param rootFile
	 * @param locationPattern
	 * @param classLoader
	 * @param processor
	 * @param predicate
	 * @param flag
	 */
	private final static void doFindHierarchyPath(File rootFile, String locationPattern, ClassLoader classLoader,
			ResolveProcessor processor, Predicate<String> predicate, boolean flag) {
		if (rootFile.isDirectory()) {
			File[] files = rootFile.listFiles();
			if (!flag) {
				locationPattern = locationPattern + "." + rootFile.getName();
			}
			for (File f : files) {
				doFindHierarchyPath(f, locationPattern, classLoader, processor, predicate, false);
			}
		} else {
			try {
				if (matcherCache.get().match(locationPattern, rootFile.getName())) {
					if (isNull(predicate) || predicate.test(rootFile.getName())) {
						processor.doResolve(ResourceType.JAR, classLoader, rootFile.getName());
					}
				}
			} catch (Exception e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
	}

	public static enum ResourceType {
		JAR, FILE;
	}

	public static interface ResolveProcessor {
		void doResolve(ResourceType type, ClassLoader classLoader, String pathname);
	}

}

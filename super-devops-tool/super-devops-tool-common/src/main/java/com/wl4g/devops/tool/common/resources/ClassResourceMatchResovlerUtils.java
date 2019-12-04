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

import com.wl4g.devops.tool.common.lang.Assert;

/**
 * Based on CLASSPATH matching resolve scanner utility tools.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年12月3日
 * @since
 */
public abstract class ClassResourceMatchResovlerUtils {

	/**
	 * JAR matching resolve scanner.
	 * 
	 * @param packageName
	 * @param classLoader
	 * @param processor
	 * @param predicate
	 */
	public static void doSearch(String packageName, ClassLoader classLoader, ResolveProcessor processor,
			Predicate<String> predicate) {
		try {
			Assert.hasText(packageName, "Matching package name can't empty.");
			Assert.notNull(processor, "ResolveProcessor can't null");

			classLoader = isNull(classLoader) ? Thread.currentThread().getContextClassLoader() : classLoader;
			Enumeration<URL> urlEn = classLoader.getResources(packageName.replace(".", "/"));
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
						JarEntry entry = jarEntryEn.nextElement();
						String jarEntryName = entry.getName();
						if (jarEntryName.replaceAll("/", ".").startsWith(packageName)) {
							// if(jarEntryName.endsWith(".class"))
							// String className = jarEntryName.substring(0,
							// jarEntryName.lastIndexOf(".")).replace("/", ".");
							if (isNull(predicate) || predicate.test(jarEntryName)) {
								processor.doResolve(ResourceType.JAR, classLoader, jarEntryName);
							}
						}
					}
				} else if ("file".equalsIgnoreCase(protocol)) {
					String basePackPath = packageName.replace(".", File.separator);
					// 将包名转换为路径名
					String classPath = url.getPath().replace(packageName.replace(".", "/"), "");
					String searchPath = classPath + basePackPath;
					doFindHierarchyPath(new File(searchPath), packageName, classLoader, processor, predicate, true);
				}
			}
		} catch (Exception e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	/**
	 * File matching resolve scanner.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年12月3日
	 * @since
	 */
	private final static void doFindHierarchyPath(File file, String packageName, ClassLoader classLoader,
			ResolveProcessor processor, Predicate<String> predicate, boolean flag) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			if (!flag) {
				packageName = packageName + "." + file.getName();
			}
			for (File f : files) {
				doFindHierarchyPath(f, packageName, classLoader, processor, predicate, false);
			}
		} else {
			try {
				// if (file.getName().endsWith(".class"))
				// Class<?> clazz = Class
				// .forName(packageName + "." + file.getName().substring(0,
				// file.getName().lastIndexOf(".")));
				if (isNull(predicate) || predicate.test(file.getName())) {
					processor.doResolve(ResourceType.JAR, classLoader, file.getName());
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

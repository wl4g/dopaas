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
package com.wl4g.devops.tool.common.utils;

import static java.util.Objects.isNull;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Based on CLASSPATH matching resolve scanner utility tools.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年12月3日
 * @since
 */
public abstract class ClassResourceMatchingResovlerUtils {

	/**
	 * Matching resolve scanner.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年12月3日
	 * @since
	 */
	static interface Scan {

		String CLASS_SUFFIX = ".class";

		Set<Class<?>> doSearch(String packageName, Predicate<Class<?>> predicate);

		default Set<Class<?>> doSearch(String packageName) {
			return doSearch(packageName, null);
		}

	}

	/**
	 * JAR matching resolve scanner.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年12月3日
	 * @since
	 */
	static class JarScanner implements Scan {

		@Override
		public Set<Class<?>> doSearch(String packageName, Predicate<Class<?>> predicate) {
			Set<Class<?>> classes = new HashSet<>();

			try {
				// 通过当前线程得到类加载器从而得到URL的枚举
				Enumeration<URL> urlEnu = Thread.currentThread().getContextClassLoader()
						.getResources(packageName.replace(".", "/"));
				while (urlEnu.hasMoreElements()) {
					/**
					 * For example:
					 * jar:file:/C:/Users/ibm/.m2/repository/junit/junit/4.12/junit-4.12.jar!/org/junit
					 */
					URL url = urlEnu.nextElement();
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
						// 得到该jar文件下面的类实体
						Enumeration<JarEntry> jarEntryEnu = jarFile.entries();
						while (jarEntryEnu.hasMoreElements()) {
							// For example:
							// org/
							// org/junit/
							// org/junit/rules/
							JarEntry entry = jarEntryEnu.nextElement();
							String jarEntryName = entry.getName();
							// 过滤不是class文件和不在basePack包名下的类
							// TODO
							if (jarEntryName.contains(".class") && jarEntryName.replaceAll("/", ".").startsWith(packageName)) {
								String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replace("/", ".");
								Class<?> cls = Class.forName(className);
								if (isNull(predicate) || predicate.test(cls)) {
									classes.add(cls);
								}
							}
						}
					} else if ("file".equalsIgnoreCase(protocol)) {
						// 从maven子项目中扫描
						FileScanner fileScanner = new FileScanner();
						fileScanner.setDefaultClassPath(url.getPath().replace(packageName.replace(".", "/"), ""));
						classes.addAll(fileScanner.doSearch(packageName, predicate));
					}
				}
			} catch (ClassNotFoundException | IOException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
			return classes;
		}

	}

	/**
	 * File matching resolve scanner.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年12月3日
	 * @since
	 */
	static class FileScanner implements Scan {

		private String defaultClassPath = FileScanner.class.getResource("/").getPath();

		public String getDefaultClassPath() {
			return defaultClassPath;
		}

		public void setDefaultClassPath(String defaultClassPath) {
			this.defaultClassPath = defaultClassPath;
		}

		public FileScanner(String defaultClassPath) {
			this.defaultClassPath = defaultClassPath;
		}

		public FileScanner() {
		}

		private static class ClassSearcher {

			private Set<Class<?>> classPaths = new HashSet<>();

			private Set<Class<?>> doPath(File file, String packageName, Predicate<Class<?>> predicate, boolean flag) {

				if (file.isDirectory()) {
					// 文件夹我们就递归
					File[] files = file.listFiles();
					if (!flag) {
						packageName = packageName + "." + file.getName();
					}
					for (File f1 : files) {
						doPath(f1, packageName, predicate, false);
					}
				} else { // 标准文件
					// 标准文件我们就判断是否是class文件
					if (file.getName().endsWith(CLASS_SUFFIX)) {
						// 如果是class文件我们就放入我们的集合中。
						try {
							Class<?> clazz = Class
									.forName(packageName + "." + file.getName().substring(0, file.getName().lastIndexOf(".")));
							if (predicate == null || predicate.test(clazz)) {
								classPaths.add(clazz);
							}
						} catch (ClassNotFoundException e) {
							throw new IllegalStateException(e.getMessage(), e);
						}
					}
				}
				return classPaths;
			}

		}

		@Override
		public Set<Class<?>> doSearch(String packageName, Predicate<Class<?>> predicate) {
			// 先把包名转换为路径,首先得到项目的classpath
			String classpath = defaultClassPath;
			// 然后把我们的包名basPack转换为路径名
			String basePackPath = packageName.replace(".", File.separator);
			String searchPath = classpath + basePackPath;
			return new ClassSearcher().doPath(new File(searchPath), packageName, predicate, true);
		}

	}

	/**
	 * Search scan classes of package name.
	 * 
	 * @param packageName
	 * @return
	 */
	public static Set<Class<?>> searchClasses(String packageName) {
		return searchClasses(packageName, null);
	}

	/**
	 * Search scan classes of package name.
	 * 
	 * @param packageName
	 * @param predicate
	 * @return
	 */
	public static Set<Class<?>> searchClasses(String packageName, Predicate<Class<?>> predicate) {
		Scan fScan = new FileScanner();
		Set<Class<?>> fSearchs = fScan.doSearch(packageName, predicate);
		Scan jarScan = new JarScanner();
		Set<Class<?>> jarSearchs = jarScan.doSearch(packageName, predicate);
		fSearchs.addAll(jarSearchs);
		return fSearchs;
	}

}

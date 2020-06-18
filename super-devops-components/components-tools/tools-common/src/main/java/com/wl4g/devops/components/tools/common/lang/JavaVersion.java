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
package com.wl4g.devops.components.tools.common.lang;

/**
 * Java versions.
 */
public enum JavaVersion {

	/**
	 * Java 1.9.
	 */
	NINE(9, "1.9", "java.security.cert.URICertStoreParameters"),

	/**
	 * Java 1.8.
	 */
	EIGHT(8, "1.8", "java.util.function.Function"),

	/**
	 * Java 1.7.
	 */
	SEVEN(7, "1.7", "java.nio.file.Files"),

	/**
	 * Java 1.6.
	 */
	SIX(6, "1.6", "java.util.ServiceLoader");

	private final int value;

	private final String name;

	private final boolean available;

	private JavaVersion(int value, String name, String className) {
		this.value = value;
		this.name = name;
		this.available = isPresent(className, getClass().getClassLoader());
	}

	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return this.name;
	}

	/**
	 * Returns the {@link JavaVersion} of the current runtime.
	 * 
	 * @return the {@link JavaVersion}
	 */
	public static JavaVersion runtimeJavaVersion() {
		for (JavaVersion candidate : JavaVersion.values()) {
			if (candidate.available) {
				return candidate;
			}
		}
		return SIX;
	}

	/**
	 * Determine whether the {@link Class} identified by the supplied name is
	 * present and can be loaded. Will return {@code false} if either the class
	 * or one of its dependencies is not present or cannot be loaded.
	 * 
	 * @param className
	 *            the name of the class to check
	 * @param classLoader
	 *            the class loader to use (may be {@code null} which indicates
	 *            the default class loader)
	 * @return whether the specified class is present
	 */
	private static boolean isPresent(String className, ClassLoader classLoader) {
		try {
			Class.forName(className, false, classLoader);
			return true;
		} catch (Throwable ex) {
			// Class or one of its dependencies is not present...
			return false;
		}
	}

}
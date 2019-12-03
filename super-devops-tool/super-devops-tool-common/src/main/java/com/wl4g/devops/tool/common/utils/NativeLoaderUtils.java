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
import static org.apache.commons.lang3.SystemUtils.JAVA_IO_TMPDIR;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * * A simple library class which helps with loading dynamic libraries stored in
 * the JAR archive. These libraries usually contain implementation of some
 * methods in native code (using JNI - Java Native Interface).
 * 
 * @see <a href=
 *      "http://adamheinrich.com/blog/2012/how-to-load-native-jni-library-from-jar">http://adamheinrich.com/blog/2012/how-to-load-native-jni-library-from-jar</a>
 * @see <a href=
 *      "https://github.com/adamheinrich/native-utils">https://github.com/adamheinrich/native-utils</a>
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年12月3日
 * @since
 */
public abstract class NativeLoaderUtils {

	/**
	 * The minimum length a prefix for a file has to have according to
	 * {@link File#createTempFile(String, String)}}.
	 */
	private static final int MIN_PREFIX_LENGTH = 3;
	public static final String NATIVE_TMP_DIR = "native_libraries";

	/**
	 * Temporary directory which will contain the DLLs.
	 */
	private static File tmpDir;

	/**
	 * The file from JAR is copied into system temporary directory and then
	 * loaded. The temporary file is deleted after exiting. Method uses String
	 * as filename because the pathname is "abstract", not system-dependent.
	 * 
	 * @param pathname
	 *            To load the path of a dynamic library, you must start with
	 *            '/', such as / lib / mylib.so, and you must start with '/'
	 * @param loadClass
	 *            The class used to provide {@link ClassLoader} to load the
	 *            dynamic library. If it is null, use nativutils class
	 * @throws IOException
	 *             Dynamic library read write error
	 * @throws FileNotFoundException
	 *             The specified file was not found in the jar package
	 */
	public final synchronized static void loadLibraryFromJar(String pathname, Class<?> loadClass) throws IOException {
		if (null == pathname || !pathname.startsWith("/")) {
			throw new IllegalArgumentException("The path has to be absolute (start with '/').");
		}
		// Obtain filename from path
		String[] parts = pathname.split("/");
		String filename = (parts.length > 1) ? parts[parts.length - 1] : null;

		// Check if the filename is okay
		if (filename == null || filename.length() < MIN_PREFIX_LENGTH) {
			throw new IllegalArgumentException("The filename has to be at least 3 characters long.");
		}
		// Create temporary folder
		if (isNull(tmpDir)) {
			tmpDir = createNativeTmpDirectory(NATIVE_TMP_DIR);
			tmpDir.deleteOnExit();
		}

		// Dynamic library name under temporary folder
		File tmpFile = new File(tmpDir, filename);
		Class<?> clazz = isNull(loadClass) ? NativeLoaderUtils.class : loadClass;

		// Copy files from jar package to system temporary folder
		try (InputStream in = clazz.getResourceAsStream(pathname)) {
			Files.copy(in, tmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			tmpFile.delete();
			throw e;
		} catch (NullPointerException e) {
			tmpFile.delete();
			throw new FileNotFoundException("File " + pathname + " was not found inside JAR.");
		}

		// Load dynamic library in temporary folder
		try {
			System.load(tmpFile.getAbsolutePath());
		} finally { // Set to delete temporary files at the end of the JVM
			tmpFile.deleteOnExit();
		}
	}

	/**
	 * Create a temporary folder under the system temporary folder
	 * 
	 * @param basePath
	 * @return
	 * @throws IOException
	 */
	private static File createNativeTmpDirectory(String basePath) throws IOException {
		File nativeTmpDir = new File(JAVA_IO_TMPDIR, basePath + System.nanoTime());
		if (!nativeTmpDir.mkdir()) {
			throw new IOException("Failed to create temp directory " + nativeTmpDir.getName());
		}
		return nativeTmpDir;
	}

}

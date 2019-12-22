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
package com.wl4g.devops.tool.common.natives;

import static com.wl4g.devops.tool.common.lang.SystemUtils2.LOCAL_PROCESS_ID;
import static java.util.Collections.emptyMap;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.SystemUtils.JAVA_IO_TMPDIR;
import static org.apache.commons.lang3.SystemUtils.OS_ARCH;
import static org.apache.commons.lang3.SystemUtils.OS_NAME;
import static org.apache.commons.lang3.SystemUtils.USER_NAME;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.wl4g.devops.tool.common.lang.ClassUtils2.*;

import static com.wl4g.devops.tool.common.lang.StringUtils2.*;
import com.wl4g.devops.tool.common.resource.Resource;
import com.wl4g.devops.tool.common.resource.resolver.PathPatternResourceMatchingResolver;

import static com.wl4g.devops.tool.common.lang.Assert.*;

/**
 * The native class library auto-loader is based on the class path or file path,
 * jar file path, usually including x64/x86/amd64/ppc64/aarch64, etc (using JNI
 * - Java Native Interface).
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
public class PathPatternNativeLibraryLoader {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Loaded state flag.
	 */
	final private AtomicBoolean loadedState = new AtomicBoolean(false);

	/**
	 * Native library loader classPath location pattern.
	 */
	final private String libLocationPattern;

	/**
	 * For example definitions reference:
	 * 
	 * <pre>
	 * /org/xerial/snappy/native/AIX/ppc/libsnappyjava.a
	 * /org/xerial/snappy/native/AIX/ppc64/libsnappyjava.a
	 * /org/xerial/snappy/native/FreeBSD/x86_64/libsnappyjava.so
	 * /org/xerial/snappy/native/Linux/aarh64/libsnappyjava.so
	 * /org/xerial/snappy/native/Linux/android-arm/libsnappyjava.so
	 * /org/xerial/snappy/native/Linux/arm/libsnappyjava.so
	 * /org/xerial/snappy/native/Linux/armv6/libsnappyjava.so
	 * /org/xerial/snappy/native/Linux/armv7/libsnappyjava.so
	 * /org/xerial/snappy/native/Linux/ppc/libsnappyjava.so
	 * /org/xerial/snappy/native/Linux/ppc64/libsnappyjava.so
	 * /org/xerial/snappy/native/Linux/ppc64le/libsnappyjava.so
	 * /org/xerial/snappy/native/Linux/s390x/libsnappyjava.so
	 * /org/xerial/snappy/native/Linux/x86/libsnappyjava.so
	 * /org/xerial/snappy/native/Linux/x86_64/libsnappyjava.so
	 * /org/xerial/snappy/native/Mac/x86/libsnappyjava.jnilib
	 * /org/xerial/snappy/native/Mac/x86_64/libsnappyjava.jnilib
	 * /org/xerial/snappy/native/SunOS/sparc/libsnappyjava.jnilib
	 * /org/xerial/snappy/native/SunOS/x86/libsnappyjava.jnilib
	 * /org/xerial/snappy/native/SunOS/x86_64/libsnappyjava.jnilib
	 * /org/xerial/snappy/native/Windows/x86/libsnappyjava.dll
	 * /org/xerial/snappy/native/Windows/x86_64/libsnappyjava.dll
	 * </pre>
	 */
	final private Map<String, String> osArchMapping = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			put("AIX", "ppc");
			put("AIX", "ppc64");
			put("FreeBSD", "x86_64");
			put("Linux", "aarh64");
			put("Linux", "android-arm");
			put("Linux", "arm");
			put("Linux", "armv6");
			put("Linux", "armv7");
			put("Linux", "ppc");
			put("Linux", "ppc64");
			put("Linux", "ppc64le");
			put("Linux", "s390x");
			put("Linux", "x86");
			put("Linux", "x86_64");
			put("Linux", "amd64");
			put("Mac", "x86");
			put("Mac", "x86_64");
			put("SunOS", "sparc");
			put("SunOS", "x86");
			put("SunOS", "x86_64");
			put("Windows", "x86");
			put("Windows", "x86_64");
		}
	};

	public PathPatternNativeLibraryLoader(String libLocationPattern) {
		this(libLocationPattern, emptyMap());
	}

	/**
	 * For example:
	 * 
	 * <pre>
	 * new PathPatternNativeLibraryLoader("/org/xerial/snappy/native/××/×.×");
	 * </pre>
	 * 
	 * <font color=red>Note: Because of the Java multiline annotation problem, the
	 * "*" is replaced by "×"</font>
	 * 
	 * @param libLocationPattern
	 */
	public PathPatternNativeLibraryLoader(String libLocationPattern, Map<String, String> osArchMapping) {
		hasText(libLocationPattern, "Native library location pattern can't empty.");
		notNull(osArchMapping, "OS name and arch mapping can't null.");
		isTrue(libLocationPattern.startsWith("/"), "The path has to be absolute (start with '/').");
		// Check filename deep hierarchy is okay?
		int libPathDeep = libLocationPattern.split("/").length;
		if (libPathDeep < NATIVE_LIBS_PATH_LEN) {
			throw new IllegalArgumentException(
					"The filename has to be at least " + NATIVE_LIBS_PATH_LEN + " characters long.");
		}
		this.libLocationPattern = libLocationPattern;
		this.osArchMapping.putAll(osArchMapping);
	}

	/**
	 * The file from JAR(CLASSPATH) is copied into system temporary directory and
	 * then loaded. The temporary file is deleted after exiting. Method uses String
	 * as filename because the pathname is "abstract", not system-dependent.
	 * 
	 * @throws IOException           Dynamic library read write error
	 * @throws FileNotFoundException The specified file was not found in the jar
	 *                               package
	 */
	public final synchronized void loadLibrarys() throws IOException {
		loadLibrarys(getDefaultClassLoader());
	}

	/**
	 * The file from JAR(CLASSPATH) is copied into system temporary directory and
	 * then loaded. The temporary file is deleted after exiting. Method uses String
	 * as filename because the pathname is "abstract", not system-dependent.
	 * 
	 * @param classLoader {@link ClassLoader} for loading native class library
	 * @throws IOException           Dynamic library read write error
	 * @throws FileNotFoundException The specified file was not found in the jar
	 *                               package.
	 */
	public final synchronized void loadLibrarys(ClassLoader classLoader) throws IOException {
		notNull(classLoader, "Native library classLoader can't null.");
		if (!loadedState.compareAndSet(false, true)) { // Loaded?
			return;
		}
		// Copy files from jar package to system temporary folder.
		PathPatternResourceMatchingResolver resolver = new PathPatternResourceMatchingResolver(classLoader);
		boolean loaded = false;
		for (Resource r : resolver.getResources(libLocationPattern)) {
			if (!r.exists() || r.isOpen() || !r.isReadable()) {
				log.warn("Unable to load native class library file: {}", r.getURL().toString());
				continue;
			}
			if (!meetOSTypeAndArch(r.getURL())) {
				continue;
			}
			if (log.isInfoEnabled()) {
				log.info("Load native class library of: {}", r.getURL().toString());
			}
			loaded = true;

			File tmpFile = null; // Native library temporary file.
			try (InputStream in = r.getInputStream()) {
				tmpFile = new File(libNativeTmpDir, r.getFilename());
				// Copy to temporary directory.
				Files.copy(in, tmpFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

				// Load to JVM.
				System.load(tmpFile.getAbsolutePath());
			} catch (IOException e) {
				if (nonNull(tmpFile))
					tmpFile.delete();
				throw e;
			} catch (NullPointerException e) {
				if (nonNull(tmpFile))
					tmpFile.delete();
				throw new FileNotFoundException("File " + r.getURL() + " was not found inside JAR.");
			} finally { // Set to delete temporary files at the end of the JVM
				if (nonNull(tmpFile))
					tmpFile.deleteOnExit();
			}
		}

		// Check any loaded library?
		state(loaded, String.format("Failed to load native library, of os: %s arch: %s", OS_NAME, OS_ARCH));
	}

	/**
	 * Automatically match the current operating system type and architecture.
	 * 
	 * @return
	 */
	protected boolean meetOSTypeAndArch(URL path) {
		// TODO OS and arch matching?
		String[] parts = split(path.toString(), "/");
		String osName = parts[parts.length - 3];
		String osArch = parts[parts.length - 2];
		return equalsIgnoreCase(osArchMapping.get(osName), osArch) && equalsIgnoreCase(osName, OS_NAME)
				&& equalsIgnoreCase(osArch, OS_ARCH);
	}

	/**
	 * Get or create a temporary folder under the system temporary folder
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	protected final synchronized static File libsTmpDirectory0(String path) {
		File libsTmpDir = null;
		try {
			libsTmpDir = new File(JAVA_IO_TMPDIR, path);
			if (!libsTmpDir.exists()) {
				state(libsTmpDir.mkdirs(), "Failed to create temp directory [" + libsTmpDir.getName() + "]");
			}
			return libsTmpDir;
		} finally {
			if (nonNull(libsTmpDir)) {
				libsTmpDir.deleteOnExit();
			}
		}
	}

	/**
	 * The minimum length a prefix for a file has to have according to
	 * {@link File#createTempFile(String, String)}}.
	 */
	final public static int NATIVE_LIBS_PATH_LEN = 3;

	/**
	 * Java dynamic link native libraries temporary base directory path.
	 */
	final public static File libNativeTmpDir = libsTmpDirectory0(
			"/javanativelibs_" + USER_NAME + "/" + LOCAL_PROCESS_ID + "-" + System.currentTimeMillis());

}

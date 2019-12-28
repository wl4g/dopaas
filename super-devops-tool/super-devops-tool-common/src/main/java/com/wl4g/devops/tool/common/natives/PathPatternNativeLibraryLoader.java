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

import static java.lang.Runtime.*;
import static java.util.Arrays.asList;
import static java.util.Collections.sort;
import static java.util.Objects.nonNull;
import static java.nio.file.StandardCopyOption.*;

import org.slf4j.Logger;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;

import com.wl4g.devops.tool.common.resource.Resource;
import com.wl4g.devops.tool.common.resource.resolver.GenericPathPatternResourceMatchingResolver;

import static com.wl4g.devops.tool.common.lang.Assert2.*;
import static com.wl4g.devops.tool.common.lang.ClassUtils2.getDefaultClassLoader;

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
public class PathPatternNativeLibraryLoader extends PlatformInfo {
	final protected Logger log = getLogger(getClass());

	/**
	 * Loaded state flag.
	 */
	final private AtomicBoolean loadedState = new AtomicBoolean(false);

	/**
	 * Native library classLoader.
	 */
	final private ClassLoader classLoader;

	/**
	 * Current OS arch share lib folder path part(lowerCase).</br>
	 * e.g. Windows/x86, Windows/x86_64, Linux/x86, Linux/x86_64,
	 */
	final private String archShareLibFolderPathLowerCase;

	/**
	 * Matched load native library file resources.
	 */
	final private List<File> loadLibFiles = new ArrayList<>(8);

	public PathPatternNativeLibraryLoader() {
		this(getDefaultClassLoader());
	}

	/**
	 * For example:
	 * 
	 * <pre>
	 * new PathPatternNativeLibraryLoader("/opencv/native/××/×.×");
	 * </pre>
	 * 
	 * <font color=red>Note: Because of the Java multiline annotation problem,
	 * the "*" is replaced by "×"</font>
	 * 
	 * @param classLoader,
	 * @param archShareMapping
	 * @param libLocationPattern
	 */
	public PathPatternNativeLibraryLoader(ClassLoader classLoader) {
		notNull(classLoader, "Native library classLoader can't null.");
		this.classLoader = classLoader;
		this.archShareLibFolderPathLowerCase = getNativeLibFolderPathForCurrentOS().toLowerCase(Locale.US);
	}

	/**
	 * Check current {@link PathPatternNativeLibraryLoader} loaded?
	 * 
	 * @return
	 */
	public boolean isLoaded() {
		return loadedState.get();
	}

	/**
	 * The file from JAR(CLASSPATH) is copied into system temporary directory
	 * and then loaded. The temporary file is deleted after exiting. Method uses
	 * String as filename because the pathname is "abstract", not
	 * system-dependent.
	 * 
	 * @param classLoader
	 *            {@link ClassLoader} for loading native class library
	 * @param libLocationPatterns
	 *            lib Location patterns
	 * @throws IOException
	 *             Dynamic library read write error
	 * @throws LoadNativeLibraryError
	 *             The specified file was not found in the jar package.
	 */
	public final synchronized void loadLibrarys(String... libLocationPatterns) throws IOException, LoadNativeLibraryError {
		if (!loadedState.compareAndSet(false, true)) { // Loaded?
			return;
		}
		// Check location pattern.
		notNull(libLocationPatterns, "Native library location pattern can't null.");
		for (String pattern : libLocationPatterns) {
			// Check filename deep hierarchy is okay?
			int libPathDeep = pattern.split("/").length;
			if (libPathDeep < NATIVE_LIBS_PATH_LEN_MIN) {
				throw new IllegalArgumentException(
						"The filename has to be at least " + NATIVE_LIBS_PATH_LEN_MIN + " characters long.");
			}
		}

		// Scanning native library resources.
		GenericPathPatternResourceMatchingResolver resolver = new GenericPathPatternResourceMatchingResolver(classLoader);
		Set<Resource> resources = resolver.getResources(libLocationPatterns);
		// Sort resources url by ASCII dict.
		List<Resource> rss = asList(resources.toArray(new Resource[] {}));
		sort(rss, (r1, r2) -> {
			try {
				return r1.getURL().toString().compareTo(r2.getURL().toString());
			} catch (IOException e1) {
				throw new IllegalStateException(e1);
			}
		});
		// Matching native library by current os arch.
		for (Resource r : rss) {
			if (!r.exists() || r.isOpen() || !r.isReadable()) {
				log.warn("Unable to load native class library file: {}", r.getURL().toString());
				continue;
			}
			if (!matchCurrentOSArchPath(r.getURL())) {
				continue;
			}
			log.info("Load native class library of: {}", r.getURL().toString());

			File tmpLibFile = null; // Native library temporary file.
			try (InputStream in = r.getInputStream()) {
				tmpLibFile = new File(libNativeTmpDir, r.getFilename());
				loadLibFiles.add(tmpLibFile);

				// Copy match nativelib to temporary directory.
				Files.copy(in, tmpLibFile.toPath(), REPLACE_EXISTING);

				// Load to JVM.
				System.load(tmpLibFile.getAbsolutePath());
			} catch (IOException e) {
				if (nonNull(tmpLibFile))
					tmpLibFile.delete();
				throw e;
			} catch (NullPointerException e) {
				if (nonNull(tmpLibFile))
					tmpLibFile.delete();
				throw new FileNotFoundException("Library file [" + r.getURL() + "] was not found.");
			}
		}

		// Any loaded library?
		if (loadLibFiles.isEmpty()) {
			throw new LoadNativeLibraryError("No match native library, current os/arch: '" + OS_NAME + "/" + OS_ARCH
					+ "', Please check whether the path of the shared chain file conforms to the specification. "
					+ "Refer to os arch name transformation mapping: " + archMapping + ", \nall was found resources: "
					+ resources);
		}

		// Cleanup temporary lib files.
		/*
		 * It has been proved that when the tmpFile.deleteOnExit() method is
		 * called, the dynamic library file cannot be deleted after the system
		 * exits, because the program is occupied, so if you want to unload the
		 * dynamic library file when the program exits, you can only use hook
		 * (calling private properties and private methods through reflection)
		 */
		getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				for (File loadFile : loadLibFiles) {
					try {
						unloadNativeLibrary(loadFile);
					} catch (Throwable th) {
						log.warn(String.format("Failed to unload native library tmpfile: %", loadFile), th);
					}
				}
			}
		});

	}

	/**
	 * Automatically match the current os type and architecture.
	 * 
	 * @return
	 */
	protected boolean matchCurrentOSArchPath(URL path) {
		return path.toString().toLowerCase(Locale.US).contains(archShareLibFolderPathLowerCase);
	}

	/**
	 * Unload native library tmpFile.
	 * 
	 * @param tmpLibFile
	 * @throws Exception
	 * @see <a href="http://peiyuxin.blog.sohu.com/300313528.html">JVM takes up
	 *      local library file reference</a>
	 */
	@SuppressWarnings("unchecked")
	private final synchronized void unloadNativeLibrary(File tmpLibFile) throws Exception {
		Field field = ClassLoader.class.getDeclaredField("nativeLibraries");
		field.setAccessible(true);
		Vector<Object> libs = (Vector<Object>) field.get(classLoader);
		Iterator<Object> it = libs.iterator();
		while (it.hasNext()) {
			Object object = it.next();
			Field[] fs = object.getClass().getDeclaredFields();
			for (int k = 0; k < fs.length; k++) {
				if (fs[k].getName().equals("name")) {
					fs[k].setAccessible(true);
					String libPath = fs[k].get(object).toString();
					/**
					 * Use the canonical path, otherwise:
					 * 
					 * <pre>
					 * C:\Users\ADMINI~1\AppData\Local\Temp\javanativelibs_Administrator\6480-1577085319527\snappyjava.dll
					 * </pre>
					 */
					if (new File(libPath).getCanonicalPath().equals(tmpLibFile.getCanonicalPath())) {
						Method finalize = object.getClass().getDeclaredMethod("finalize");
						finalize.setAccessible(true);
						finalize.invoke(object);
						if (!tmpLibFile.delete()) {
							log.warn("Failed to cleanup tmp library file of: {}", tmpLibFile);
						}
					}
				}
			}
		}
	}

	/**
	 * Get or create a temporary folder under the system temporary folder
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	private final synchronized static File libsTmpDirectory0(String path) {
		File libsTmpDir = new File(JAVA_IO_TMPDIR, path);
		if (!libsTmpDir.exists()) {
			state(libsTmpDir.mkdirs(), "Failed to create temp directory [" + libsTmpDir.getName() + "]");
		}
		return libsTmpDir;
	}

	/**
	 * The minimum length a prefix for a file has to have according to
	 * {@link File#createTempFile(String, String)}}.
	 */
	final public static int NATIVE_LIBS_PATH_LEN_MIN = 3;

	/**
	 * Java dynamic link native libraries temporary base directory path.
	 */
	final public static File libNativeTmpDir = libsTmpDirectory0(File.separator + "javanativelibs_" + USER_NAME + File.separator
			+ LOCAL_PROCESS_ID + "-" + System.currentTimeMillis());

}

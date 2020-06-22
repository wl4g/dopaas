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
package com.wl4g.devops.components.tools.common.natives;

import static java.util.Collections.unmodifiableMap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import com.wl4g.devops.components.tools.common.lang.SystemUtils2;

/**
 * Provides OS name and architecture name.
 * 
 * @author wanglsir@gmail.com, 983708408@qq.com
 * @version 2019年11月28日 v1.0.0
 * @see {@link org.xerial.snappy.OSInfo} Thanks to the snappy team!
 */
public abstract class PlatformInfo extends SystemUtils2 {
	final public static Map<String, String> archMapping = unmodifiableMap(archMapping0());

	final public static String X86 = "x86";
	final public static String X86_64 = "x86_64";
	final public static String IA64_32 = "ia64_32";
	final public static String IA64 = "ia64";
	final public static String PPC = "ppc";
	final public static String PPC64 = "ppc64";
	final public static String IBMZ = "s390";
	final public static String IBMZ_64 = "s390x";
	final public static String AARCH_64 = "aarch64";

	/**
	 * General specification path to get shared file of native library.
	 * 
	 * @return
	 */
	public static String getNativeLibFolderPathForCurrentOS() {
		return translateOSNameToFolderName(OS_NAME) + "/" + getSpecArchName();
	}

	/**
	 * Get specification os arch name.
	 * 
	 * @see {@link #archMapping}
	 * @return
	 */
	public static String getSpecArchName() {
		String osArch = OS_ARCH;
		// For Android
		if (IS_ANDRIOD) {
			return "android-arm";
		}

		if (osArch.startsWith("arm")) {
			osArch = resolveArmArchType();
		} else {
			String lc = osArch.toLowerCase(Locale.US);
			if (archMapping.containsKey(lc))
				return archMapping.get(lc);
		}
		return translateArchNameToFolderName(osArch);
	}

	/**
	 * Initial arch mapping.
	 * 
	 * @return
	 */
	private static Map<String, String> archMapping0() {
		Map<String, String> archMapping = new HashMap<String, String>() {
			private static final long serialVersionUID = -7501326267627482105L;

			@Override
			public String toString() {
				Iterator<Entry<String, String>> i = entrySet().iterator();
				if (!i.hasNext())
					return "{}";

				StringBuilder sb = new StringBuilder();
				sb.append('{');
				for (;;) {
					Entry<String, String> e = i.next();
					String key = e.getKey();
					String value = e.getValue();
					sb.append(key);
					sb.append("=>");
					sb.append(value);
					if (!i.hasNext())
						return sb.append('}').toString();
					sb.append(',').append(' ');
				}
			}
		};

		// x86 mappings
		archMapping.put(X86, X86);
		archMapping.put("i386", X86);
		archMapping.put("i486", X86);
		archMapping.put("i586", X86);
		archMapping.put("i686", X86);
		archMapping.put("pentium", X86);

		// x86_64 mappings
		archMapping.put(X86_64, X86_64);
		archMapping.put("amd64", X86_64);
		archMapping.put("em64t", X86_64);
		archMapping.put("universal", X86_64); // Needed for openjdk7 in Mac

		// Itenium 64-bit mappings
		archMapping.put(IA64, IA64);
		archMapping.put("ia64w", IA64);

		// Itenium 32-bit mappings, usually an HP-UX construct
		archMapping.put(IA64_32, IA64_32);
		archMapping.put("ia64n", IA64_32);

		// PowerPC mappings
		archMapping.put(PPC, PPC);
		archMapping.put("power", PPC);
		archMapping.put("powerpc", PPC);
		archMapping.put("power_pc", PPC);
		archMapping.put("power_rs", PPC);

		// TODO: PowerPC 64bit mappings
		archMapping.put(PPC64, PPC64);
		archMapping.put("power64", PPC64);
		archMapping.put("powerpc64", PPC64);
		archMapping.put("power_pc64", PPC64);
		archMapping.put("power_rs64", PPC64);

		// IBM z mappings
		archMapping.put(IBMZ, IBMZ);

		// IBM z 64-bit mappings
		archMapping.put(IBMZ_64, IBMZ_64);

		// Aarch64 mappings
		archMapping.put(AARCH_64, AARCH_64);
		return archMapping;
	}

	private static String getHardwareName() {
		try {
			Process p = Runtime.getRuntime().exec("uname -m");
			p.waitFor();

			InputStream in = p.getInputStream();
			try {
				int readLen = 0;
				ByteArrayOutputStream b = new ByteArrayOutputStream();
				byte[] buf = new byte[32];
				while ((readLen = in.read(buf, 0, buf.length)) >= 0) {
					b.write(buf, 0, readLen);
				}
				return b.toString();
			} finally {
				if (in != null) {
					in.close();
				}
			}
		} catch (Throwable e) {
			System.err.println("Error while running uname -m: " + e.getMessage());
			return "unknown";
		}
	}

	private static String resolveArmArchType() {
		if (System.getProperty("os.name").contains("Linux")) {
			String armType = getHardwareName();
			// armType (uname -m) can be armv5t, armv5te, armv5tej, armv5tejl,
			// armv6, armv7,
			// armv7l, i686
			if (armType.startsWith("armv6")) {
				// Raspberry PI
				return "armv6";
			} else if (armType.startsWith("armv7")) {
				// Generic
				return "armv7";
			}

			// Java 1.8 introduces a system property to determine armel or armhf
			// http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8005545
			String abi = System.getProperty("sun.arch.abi");
			if (abi != null && abi.startsWith("gnueabihf")) {
				return "armv7";
			}

			// For java7, we stil need to if run some shell commands to
			// determine ABI of JVM
			try {
				// determine if first JVM found uses ARM hard-float ABI
				int exitCode = Runtime.getRuntime().exec("which readelf").waitFor();
				if (exitCode == 0) {
					String javaHome = System.getProperty("java.home");
					String[] cmdarray = { "/bin/sh", "-c", "find '" + javaHome
							+ "' -name 'libjvm.so' | head -1 | xargs readelf -A | " + "grep 'Tag_ABI_VFP_args: VFP registers'" };
					exitCode = Runtime.getRuntime().exec(cmdarray).waitFor();
					if (exitCode == 0) {
						return "armv7";
					}
				} else {
					System.err.println("WARNING! readelf not found. Cannot check if running on an armhf system, "
							+ "armel architecture will be presumed.");
				}
			} catch (IOException e) {
				// ignored: fall back to "arm" arch (soft-float ABI)
			} catch (InterruptedException e) {
				// ignored: fall back to "arm" arch (soft-float ABI)
			}
		}
		// Use armv5, soft-float ABI
		return "arm";
	}

	private static String translateOSNameToFolderName(String osName) {
		if (osName.contains("Windows")) {
			return "Windows";
		} else if (osName.contains("Mac")) {
			return "Mac";
		} else if (osName.contains("Linux")) {
			return "Linux";
		} else if (osName.contains("AIX")) {
			return "AIX";
		} else {
			return osName.replaceAll("\\W", "");
		}
	}

	private static String translateArchNameToFolderName(String archName) {
		return archName.replaceAll("\\W", "");
	}

	public static void main(String[] args) {
		if (args.length >= 1) {
			if ("--os".equals(args[0])) {
				System.out.print(OS_NAME);
				return;
			} else if ("--arch".equals(args[0])) {
				System.out.print(getSpecArchName());
				return;
			}
		}

		System.out.print(getNativeLibFolderPathForCurrentOS());
	}

}
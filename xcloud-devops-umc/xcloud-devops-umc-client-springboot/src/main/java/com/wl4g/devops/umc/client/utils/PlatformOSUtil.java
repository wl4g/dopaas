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
package com.wl4g.devops.umc.client.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Operating system performance information tools.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年6月4日
 * @since
 */
public class PlatformOSUtil {
	final private static int FAULT_LEN = 10;
	final private static long DEFAULT_FREQ = 5_00L;

	final private static String CPU_OF_UNIX = "/proc/stat";
	final private static String MEMORY_OF_UNIX = "/proc/meminfo";

	final private static String CPU_OF_WINDOWS = System.getenv("windir")
			+ "\\system32\\wbem\\wmic.exe process get Caption,CommandLine,KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount";

	/**
	 * Getting the current usage of the Unix/Windows system memory.
	 * 
	 * @return (kB)
	 * @throws Exception
	 */
	public static MemInfo memInfo() throws Exception {
		if (OSInfo.isLinux()) {
			return memInfoOfUnix();
		} else if (OSInfo.isWindows()) {
			return memInfoOfWin();
		}
		throw new UnsupportedOperationException("Illegal operation");
	}

	/**
	 * Getting the current usage of the Unix/Windows system CPU.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static float cpuUsage() throws Exception {
		if (OSInfo.isLinux()) {
			return cpuUnixUsage();
		} else if (OSInfo.isWindows()) {
			return cpuWindowsUsage();
		}
		throw new UnsupportedOperationException("Illegal operation");
	}

	//
	// For Unix
	//

	/**
	 * Get current system memory information.
	 * 
	 * @return Unit KB
	 * @throws Exception
	 */
	private static MemInfo memInfoOfUnix() throws Exception {
		Map<String, Long> ret = readUnixMemoryInfo();
		// long memTotal = ret.getOrDefault("MemTotal", 0L);
		// long memFree = ret.getOrDefault("MemFree", 0L);
		// long buffers = ret.getOrDefault("Buffers", 0L);
		// long cached = ret.getOrDefault("Cached", 0L);
		// long memused = memTotal - memFree;
		// float usage = (float) (memused - buffers - cached) / memTotal;
		// return BigDecimal.valueOf(usage).setScale(2,
		// RoundingMode.HALF_EVEN).floatValue();
		UnixMemInfo unixMem = new UnixMemInfo();
		unixMem.setMemTotal(ret.getOrDefault("MemTotal", 0L));
		unixMem.setMemFree(ret.getOrDefault("MemFree", 0L));
		unixMem.setBuffers(ret.getOrDefault("Buffers", 0L));
		unixMem.setCached(ret.getOrDefault("Cached", 0L));
		unixMem.setSwapCached(ret.getOrDefault("SwapCached", 0L));
		unixMem.setActive(ret.getOrDefault("Active", 0L));
		unixMem.setInactive(ret.getOrDefault("Inactive", 0L));
		unixMem.setActiveAnon(ret.getOrDefault("Active(anon)", 0L));
		unixMem.setInactiveAnon(ret.getOrDefault("Inactive(anon)", 0L));
		unixMem.setActiveFile(ret.getOrDefault("Active(file)", 0L));
		unixMem.setInactiveFile(ret.getOrDefault("Inactive(file)", 0L));
		unixMem.setUnevictable(ret.getOrDefault("Unevictable", 0L));
		unixMem.setMlocked(ret.getOrDefault("Mlocked", 0L));
		unixMem.setSwapTotal(ret.getOrDefault("SwapTotal", 0L));
		unixMem.setSwapFree(ret.getOrDefault("SwapFree", 0L));
		unixMem.setDirty(ret.getOrDefault("Dirty", 0L));
		unixMem.setWriteback(ret.getOrDefault("Writeback", 0L));
		unixMem.setAnonPages(ret.getOrDefault("AnonPages", 0L));
		unixMem.setMapped(ret.getOrDefault("Mapped", 0L));
		unixMem.setShmem(ret.getOrDefault("Shmem", 0L));
		unixMem.setSlab(ret.getOrDefault("Slab", 0L));
		unixMem.setSReclaimable(ret.getOrDefault("SReclaimable", 0L));
		unixMem.setSUnreclaim(ret.getOrDefault("SUnreclaim", 0L));
		unixMem.setKernelStack(ret.getOrDefault("KernelStack", 0L));
		unixMem.setPageTables(ret.getOrDefault("PageTables", 0L));
		unixMem.setNFS_Unstable(ret.getOrDefault("NFS_Unstable", 0L));
		unixMem.setBounce(ret.getOrDefault("Bounce", 0L));
		unixMem.setWritebackTmp(ret.getOrDefault("WritebackTmp", 0L));
		unixMem.setCommitLimit(ret.getOrDefault("CommitLimit", 0L));
		unixMem.setCommitted_AS(ret.getOrDefault("Committed_AS", 0L));
		unixMem.setVmallocTotal(ret.getOrDefault("VmallocTotal", 0L));
		unixMem.setVmallocUsed(ret.getOrDefault("VmallocUsed", 0L));
		unixMem.setVmallocChunk(ret.getOrDefault("VmallocChunk", 0L));
		unixMem.setHardwareCorrupted(ret.getOrDefault("HardwareCorrupted", 0L));
		unixMem.setAnonHugePages(ret.getOrDefault("AnonHugePages", 0L));
		unixMem.setHugePagesTotal(ret.getOrDefault("HugePagesTotal", 0L));
		unixMem.setHugePagesFree(ret.getOrDefault("HugePagesFree", 0L));
		unixMem.setHugePagesRsvd(ret.getOrDefault("HugePagesRsvd", 0L));
		unixMem.setHugePagesSurp(ret.getOrDefault("HugePagesSurp", 0L));
		unixMem.setHugepagesize(ret.getOrDefault("Hugepagesize", 0L));
		unixMem.setDirectMap4k(ret.getOrDefault("DirectMap4k", 0L));
		unixMem.setDirectMap2M(ret.getOrDefault("DirectMap2M", 0L));
		unixMem.setDirectMap1G(ret.getOrDefault("DirectMap1G", 0L));
		return unixMem;
	}

	/**
	 * Getting the current system memory usage information. <br/>
	 * EG: <br/>
	 * MemTotal: 8009028 kB <br/>
	 * MemFree: 4244732 kB <br/>
	 * MemAvailable: 4902512 kB <br/>
	 * Buffers: 155656 kB <br/>
	 * Cached: 703356 kB <br/>
	 * SwapCached: 0 kB <br/>
	 * Active: 3349764 kB <br/>
	 * Inactive: 265684 kB <br/>
	 * Active(anon): 2756668 kB <br/>
	 * Inactive(anon): 8336 kB <br/>
	 * Active(file): 593096 kB <br/>
	 * Inactive(file): 257348 kB <br/>
	 * Unevictable: 0 kB <br/>
	 * Mlocked: 0 kB <br/>
	 * SwapTotal: 0 kB <br/>
	 * SwapFree: 0 kB <br/>
	 * Dirty: 1228 kB <br/>
	 * Writeback: 0 kB <br/>
	 * AnonPages: 2756604 kB <br/>
	 * Mapped: 52592 kB <br/>
	 * Shmem: 8572 kB <br/>
	 * Slab: 68056 kB <br/>
	 * SReclaimable: 51516 kB <br/>
	 * SUnreclaim: 16540 kB <br/>
	 * KernelStack: 6096 kB <br/>
	 * PageTables: 9376 kB <br/>
	 * NFS_Unstable: 0 kB <br/>
	 * Bounce: 0 kB <br/>
	 * WritebackTmp: 0 kB <br/>
	 * CommitLimit: 4004512 kB <br/>
	 * Committed_AS: 4371012 kB <br/>
	 * VmallocTotal: 34359738367 kB <br/>
	 * VmallocUsed: 23968 kB <br/>
	 * VmallocChunk: 34359705340 kB <br/>
	 * HardwareCorrupted: 0 kB <br/>
	 * AnonHugePages: 2580480 kB <br/>
	 * HugePages_Total: 0 <br/>
	 * HugePages_Free: 0 <br/>
	 * HugePages_Rsvd: 0 <br/>
	 * HugePages_Surp: 0 <br/>
	 * Hugepagesize: 2048 kB <br/>
	 * DirectMap4k: 47104 kB <br/>
	 * DirectMap2M: 8341504 kB <br/>
	 * 
	 * @return
	 * @throws Exception
	 */
	private static Map<String, Long> readUnixMemoryInfo() throws Exception {
		Map<String, Long> ret = new HashMap<>();
		InputStreamReader inr = null;
		BufferedReader buffer = null;
		try {
			inr = new InputStreamReader(new FileInputStream(MEMORY_OF_UNIX));
			buffer = new BufferedReader(inr);
			String line = "";
			while (true) {
				line = buffer.readLine();
				if (line == null)
					break;
				int beginIndex = 0;
				int endIndex = line.indexOf(":");
				if (endIndex != -1) {
					String key = line.substring(beginIndex, endIndex);
					beginIndex = endIndex + 1;
					endIndex = line.length();
					String memory = line.substring(beginIndex, endIndex);
					String value = memory.replace("kB", "").trim();
					ret.put(key, Long.parseLong(value));
				}
			}
			return ret;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (buffer != null) {
					buffer.close();
				}
				if (inr != null) {
					inr.close();
				}
				buffer = null;
				inr = null;
			} catch (Exception e2) {
				throw e2;
			}
		}
	}

	/**
	 * Getting the current usage of the Unix system CPU.
	 * 
	 * @return
	 * @throws Exception
	 */
	private static float cpuUnixUsage() throws Exception {
		return cpuUnixUsage(DEFAULT_FREQ);
	}

	/**
	 * Getting the current usage of the Unix system CPU.
	 * 
	 * @param freqMs
	 *            Acquisition frequency interval
	 * @return
	 * @throws Exception
	 */
	private static float cpuUnixUsage(long freqMs) throws Exception {
		try {
			Map<String, String> map1 = readUnixCPUInfo();
			Thread.sleep(freqMs);
			Map<String, String> map2 = readUnixCPUInfo();

			long user1 = Long.parseLong(map1.getOrDefault("user", "0"));
			long nice1 = Long.parseLong(map1.getOrDefault("nice", "0"));
			long system1 = Long.parseLong(map1.getOrDefault("system", "0"));
			long idle1 = Long.parseLong(map1.getOrDefault("idle", "0"));

			long user2 = Long.parseLong(map2.getOrDefault("user", "0"));
			long nice2 = Long.parseLong(map2.getOrDefault("nice", "0"));
			long system2 = Long.parseLong(map2.getOrDefault("system", "0"));
			long idle2 = Long.parseLong(map2.getOrDefault("idle", "0"));

			long total1 = user1 + system1 + nice1;
			long total2 = user2 + system2 + nice2;
			float total = total2 - total1;

			long totalIdle1 = user1 + nice1 + system1 + idle1;
			long totalIdle2 = user2 + nice2 + system2 + idle2;
			float totalidle = totalIdle2 - totalIdle1;

			return BigDecimal.valueOf((total / totalidle)).setScale(2, RoundingMode.HALF_EVEN).floatValue();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Get the original information of the Unix system CPU.
	 * 
	 * @return
	 * @throws IOException
	 */
	private static Map<String, String> readUnixCPUInfo() throws IOException {
		if (!OSInfo.isLinux())
			throw new UnsupportedOperationException("Not supported.");

		InputStreamReader inr = null;
		BufferedReader buffer = null;
		Map<String, String> ret = new HashMap<>();
		try {
			inr = new InputStreamReader(new FileInputStream(CPU_OF_UNIX));
			buffer = new BufferedReader(inr);
			String line = "";
			while (true) {
				line = buffer.readLine();
				if (line == null) {
					break;
				}
				if (line.toUpperCase().startsWith("CPU")) {
					StringTokenizer tokenizer = new StringTokenizer(line);
					List<String> temp = new ArrayList<String>();
					while (tokenizer.hasMoreElements()) {
						String value = tokenizer.nextToken();
						temp.add(value);
					}
					ret.put("user", temp.get(1));
					ret.put("nice", temp.get(2));
					ret.put("system", temp.get(3));
					ret.put("idle", temp.get(4));
					ret.put("iowait", temp.get(5));
					ret.put("irq", temp.get(6));
					ret.put("softirq", temp.get(7));
					ret.put("stealstolen", temp.get(8));
					break;
				}
			}
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (buffer != null) {
					buffer.close();
				}
				if (inr != null) {
					inr.close();
				}
				buffer = null;
				inr = null;
			} catch (IOException e2) {
				throw e2;
			}
		}
		return ret;
	}

	//
	// For Windows
	//

	/**
	 * Getting the current usage of the Windowns system memory.
	 * 
	 * @return (bytes)
	 * @throws Exception
	 */
	@Deprecated
	private static MemInfo memInfoOfWin() throws Exception {
		long jvmTotal = Runtime.getRuntime().totalMemory() / 1024;
		long jvmMax = Runtime.getRuntime().maxMemory() / 1024;
		long jvmFree = Runtime.getRuntime().freeMemory() / 1024;
		long jvmUsed = jvmMax - jvmTotal - jvmFree;
		return new WinMemInfo(jvmMax, jvmFree, -1L, jvmUsed);
	}

	/**
	 * Get the original information of the Windows system CPU.
	 * 
	 * @return
	 * @throws Exception
	 */
	private static float cpuWindowsUsage() throws Exception {
		return cpuWindowsUsage(DEFAULT_FREQ);
	}

	/**
	 * Get the original information of the Windows system CPU.
	 * 
	 * @return
	 * @throws Exception
	 */
	private static float cpuWindowsUsage(long freqMs) throws Exception {
		if (!OSInfo.isWindows())
			throw new UnsupportedOperationException("Not supported.");

		try {
			long[] c0 = readWindowsCPUInfo(Runtime.getRuntime().exec(CPU_OF_WINDOWS));
			Thread.sleep(freqMs);
			long[] c1 = readWindowsCPUInfo(Runtime.getRuntime().exec(CPU_OF_WINDOWS));
			if ((c0 != null) && (c1 != null)) {
				long idletime = c1[0] - c0[0];
				long busytime = c1[1] - c0[1];
				return (float) ((busytime * 1f) / (busytime + idletime));
			} else {
				return 0;
			}
		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * Read Windows CPU information
	 * 
	 * @param proc
	 * @return
	 * @throws Exception
	 */
	private static long[] readWindowsCPUInfo(Process proc) throws Exception {
		long[] retn = new long[2];
		InputStream in = null;
		InputStreamReader inr = null;
		LineNumberReader lnr = null;
		try {
			proc.getOutputStream().close();
			in = proc.getInputStream();
			inr = new InputStreamReader(in);
			lnr = new LineNumberReader(inr);
			String line = lnr.readLine();
			if ((line == null) || (line.length() < FAULT_LEN)) {
				return null;
			}
			int capidx = line.indexOf("Caption");
			int cmdidx = line.indexOf("CommandLine");
			int rocidx = line.indexOf("ReadOperationCount");
			int umtidx = line.indexOf("UserModeTime");
			int kmtidx = line.indexOf("KernelModeTime");
			int wocidx = line.indexOf("WriteOperationCount");
			long idletime = 0;
			long kneltime = 0;
			long usertime = 0;

			while ((line = lnr.readLine()) != null) {
				if (line.length() < wocidx) {
					continue;
				}
				// 字段出现顺序：Caption,CommandLine,KernelModeTime,ReadOperationCount,
				// ThreadCount,UserModeTime,WriteOperation
				String caption = substring(line, capidx, cmdidx - 1).trim();
				String cmd = substring(line, cmdidx, kmtidx - 1).trim();
				if (cmd.indexOf("wmic.exe") >= 0) {
					continue;
				}
				String s1 = substring(line, kmtidx, rocidx - 1).trim();
				String s2 = substring(line, umtidx, wocidx - 1).trim();
				if (caption.equals("System Idle Process") || caption.equals("System")) {
					if (s1.length() > 0) {
						idletime += Long.valueOf(s1).longValue();
					}
					if (s2.length() > 0) {
						idletime += Long.valueOf(s2).longValue();
					}
					continue;
				}
				if (s1.length() > 0) {
					kneltime += Long.valueOf(s1).longValue();
				}
				if (s2.length() > 0) {
					usertime += Long.valueOf(s2).longValue();
				}
			}
			retn[0] = idletime;
			retn[1] = kneltime + usertime;

			return retn;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (lnr != null) {
					lnr.close();
				}
				if (inr != null) {
					inr.close();
				}
				if (in != null) {
					in.close();
				}
				lnr = null;
				inr = null;
				in = null;
			} catch (Exception e2) {
				throw e2;
			}
		}
	}

	/**
	 * 由于String.subString对汉字处理存在问题（把一个汉字视为一个字节)，因此在 包含汉字的字符串时存在隐患，现调整如下：
	 * 
	 * @param src
	 *            要截取的字符串
	 * @param start_idx
	 *            开始坐标（包括该坐标)
	 * @param end_idx
	 *            截止坐标（包括该坐标）
	 * @return
	 */
	private static String substring(String src, int start_idx, int end_idx) {
		byte[] b = src.getBytes();
		String tgt = "";
		for (int i = start_idx; i <= end_idx; i++) {
			tgt += (char) b[i];
		}
		return tgt;
	}

	//
	// Information wrapper.
	//

	public static class OSInfo {
		private static String OS = System.getProperty("os.name").toLowerCase();

		private OSInfo() {
		}

		public static boolean isLinux() {
			return OS.indexOf("linux") >= 0;
		}

		public static boolean isMacOS() {
			return OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0 && OS.indexOf("x") < 0;
		}

		public static boolean isMacOSX() {
			return OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0 && OS.indexOf("x") > 0;
		}

		public static boolean isWindows() {
			return OS.indexOf("windows") >= 0;
		}

		public static boolean isOS2() {
			return OS.indexOf("os/2") >= 0;
		}

		public static boolean isSolaris() {
			return OS.indexOf("solaris") >= 0;
		}

		public static boolean isSunOS() {
			return OS.indexOf("sunos") >= 0;
		}

		public static boolean isMPEiX() {
			return OS.indexOf("mpe/ix") >= 0;
		}

		public static boolean isHPUX() {
			return OS.indexOf("hp-ux") >= 0;
		}

		public static boolean isAix() {
			return OS.indexOf("aix") >= 0;
		}

		public static boolean isOS390() {
			return OS.indexOf("os/390") >= 0;
		}

		public static boolean isFreeBSD() {
			return OS.indexOf("freebsd") >= 0;
		}

		public static boolean isIrix() {
			return OS.indexOf("irix") >= 0;
		}

		public static boolean isDigitalUnix() {
			return OS.indexOf("digital") >= 0 && OS.indexOf("unix") > 0;
		}

		public static boolean isNetWare() {
			return OS.indexOf("netware") >= 0;
		}

		public static boolean isOSF1() {
			return OS.indexOf("osf1") >= 0;
		}

		public static boolean isOpenVMS() {
			return OS.indexOf("openvms") >= 0;
		}

		/**
		 * Getting the name of the operating system
		 * 
		 * @return
		 */
		public static OSType getOSType() {
			OSType osType = OSType.Others;
			if (isAix()) {
				osType = OSType.AIX;
			} else if (isDigitalUnix()) {
				osType = OSType.Digital_Unix;
			} else if (isFreeBSD()) {
				osType = OSType.FreeBSD;
			} else if (isHPUX()) {
				osType = OSType.HP_UX;
			} else if (isIrix()) {
				osType = OSType.Irix;
			} else if (isLinux()) {
				osType = OSType.Linux;
			} else if (isMacOS()) {
				osType = OSType.Mac_OS;
			} else if (isMacOSX()) {
				osType = OSType.Mac_OS_X;
			} else if (isMPEiX()) {
				osType = OSType.MPEiX;
			} else if (isNetWare()) {
				osType = OSType.NetWare_411;
			} else if (isOpenVMS()) {
				osType = OSType.OpenVMS;
			} else if (isOS2()) {
				osType = OSType.OS2;
			} else if (isOS390()) {
				osType = OSType.OS390;
			} else if (isOSF1()) {
				osType = OSType.OSF1;
			} else if (isSolaris()) {
				osType = OSType.Solaris;
			} else if (isSunOS()) {
				osType = OSType.SunOS;
			} else if (isWindows()) {
				osType = OSType.Windows;
			}
			return osType;
		}

		/**
		 * Operating system type.
		 */
		public enum OSType {
			Any("any"), Linux("Linux"), Mac_OS("Mac OS"), Mac_OS_X("Mac OS X"), Windows("Windows"), OS2("OS/2"), Solaris(
					"Solaris"), SunOS("SunOS"), MPEiX("MPE/iX"), HP_UX("HP-UX"), AIX("AIX"), OS390("OS/390"), FreeBSD(
							"FreeBSD"), Irix("Irix"), Digital_Unix(
									"Digital Unix"), NetWare_411("NetWare"), OSF1("OSF1"), OpenVMS("OpenVMS"), Others("Others");

			private OSType(String desc) {
				this.description = desc;
			}

			public String toString() {
				return description;
			}

			private String description;
		}

	}

	public static abstract class MemInfo {
		private long memTotal;
		private long memFree;
		private long buffers;
		private long cached;

		public MemInfo() {
			super();
		}

		public MemInfo(long memTotal, long memFree, long buffers, long cached) {
			super();
			this.memTotal = memTotal;
			this.memFree = memFree;
			this.buffers = buffers;
			this.cached = cached;
		}

		public long getMemTotal() {
			return memTotal;
		}

		public void setMemTotal(long memTotal) {
			this.memTotal = memTotal;
		}

		public long getMemFree() {
			return memFree;
		}

		public void setMemFree(long memFree) {
			this.memFree = memFree;
		}

		public long getBuffers() {
			return buffers;
		}

		public void setBuffers(long buffers) {
			this.buffers = buffers;
		}

		public long getCached() {
			return cached;
		}

		public void setCached(long cached) {
			this.cached = cached;
		}

		@Override
		public String toString() {
			return "MemInfo [memTotal=" + memTotal + ", memFree=" + memFree + ", buffers=" + buffers + ", cached=" + cached + "]";
		}

	}

	public static class UnixMemInfo extends MemInfo {
		private long swapCached;
		private long active;
		private long inactive;
		private long activeAnon;
		private long inactiveAnon;
		private long activeFile;
		private long inactiveFile;
		private long unevictable;
		private long mlocked;
		private long swapTotal;
		private long swapFree;
		private long dirty;
		private long writeback;
		private long anonPages;
		private long mapped;
		private long shmem;
		private long slab;
		private long sReclaimable;
		private long sUnreclaim;
		private long kernelStack;
		private long pageTables;
		private long nFS_Unstable;
		private long bounce;
		private long writebackTmp;
		private long commitLimit;
		private long committed_AS;
		private long vmallocTotal;
		private long vmallocUsed;
		private long vmallocChunk;
		private long hardwareCorrupted;
		private long anonHugePages;
		private long hugePagesTotal;
		private long hugePagesFree;
		private long hugePagesRsvd;
		private long hugePagesSurp;
		private long hugepagesize;
		private long directMap4k;
		private long directMap2M;
		private long directMap1G;

		public UnixMemInfo() {
			super();
		}

		public UnixMemInfo(long memTotal, long memFree, long buffers, long cached) {
			super(memTotal, memFree, buffers, cached);
		}

		public long getSwapCached() {
			return swapCached;
		}

		public void setSwapCached(long swapCached) {
			this.swapCached = swapCached;
		}

		public long getActive() {
			return active;
		}

		public void setActive(long active) {
			this.active = active;
		}

		public long getInactive() {
			return inactive;
		}

		public void setInactive(long inactive) {
			this.inactive = inactive;
		}

		public long getActiveAnon() {
			return activeAnon;
		}

		public void setActiveAnon(long activeAnon) {
			this.activeAnon = activeAnon;
		}

		public long getInactiveAnon() {
			return inactiveAnon;
		}

		public void setInactiveAnon(long inactiveAnon) {
			this.inactiveAnon = inactiveAnon;
		}

		public long getActiveFile() {
			return activeFile;
		}

		public void setActiveFile(long activeFile) {
			this.activeFile = activeFile;
		}

		public long getInactiveFile() {
			return inactiveFile;
		}

		public void setInactiveFile(long inactiveFile) {
			this.inactiveFile = inactiveFile;
		}

		public long getUnevictable() {
			return unevictable;
		}

		public void setUnevictable(long unevictable) {
			this.unevictable = unevictable;
		}

		public long getMlocked() {
			return mlocked;
		}

		public void setMlocked(long mlocked) {
			this.mlocked = mlocked;
		}

		public long getSwapTotal() {
			return swapTotal;
		}

		public void setSwapTotal(long swapTotal) {
			this.swapTotal = swapTotal;
		}

		public long getSwapFree() {
			return swapFree;
		}

		public void setSwapFree(long swapFree) {
			this.swapFree = swapFree;
		}

		public long getDirty() {
			return dirty;
		}

		public void setDirty(long dirty) {
			this.dirty = dirty;
		}

		public long getWriteback() {
			return writeback;
		}

		public void setWriteback(long writeback) {
			this.writeback = writeback;
		}

		public long getAnonPages() {
			return anonPages;
		}

		public void setAnonPages(long anonPages) {
			this.anonPages = anonPages;
		}

		public long getMapped() {
			return mapped;
		}

		public void setMapped(long mapped) {
			this.mapped = mapped;
		}

		public long getShmem() {
			return shmem;
		}

		public void setShmem(long shmem) {
			this.shmem = shmem;
		}

		public long getSlab() {
			return slab;
		}

		public void setSlab(long slab) {
			this.slab = slab;
		}

		public long getSReclaimable() {
			return sReclaimable;
		}

		public void setSReclaimable(long sReclaimable) {
			this.sReclaimable = sReclaimable;
		}

		public long getSUnreclaim() {
			return sUnreclaim;
		}

		public void setSUnreclaim(long sUnreclaim) {
			this.sUnreclaim = sUnreclaim;
		}

		public long getKernelStack() {
			return kernelStack;
		}

		public void setKernelStack(long kernelStack) {
			this.kernelStack = kernelStack;
		}

		public long getPageTables() {
			return pageTables;
		}

		public void setPageTables(long pageTables) {
			this.pageTables = pageTables;
		}

		public long getNFS_Unstable() {
			return nFS_Unstable;
		}

		public void setNFS_Unstable(long nFS_Unstable) {
			this.nFS_Unstable = nFS_Unstable;
		}

		public long getBounce() {
			return bounce;
		}

		public void setBounce(long bounce) {
			this.bounce = bounce;
		}

		public long getWritebackTmp() {
			return writebackTmp;
		}

		public void setWritebackTmp(long writebackTmp) {
			this.writebackTmp = writebackTmp;
		}

		public long getCommitLimit() {
			return commitLimit;
		}

		public void setCommitLimit(long commitLimit) {
			this.commitLimit = commitLimit;
		}

		public long getCommitted_AS() {
			return committed_AS;
		}

		public void setCommitted_AS(long committed_AS) {
			this.committed_AS = committed_AS;
		}

		public long getVmallocTotal() {
			return vmallocTotal;
		}

		public void setVmallocTotal(long vmallocTotal) {
			this.vmallocTotal = vmallocTotal;
		}

		public long getVmallocUsed() {
			return vmallocUsed;
		}

		public void setVmallocUsed(long vmallocUsed) {
			this.vmallocUsed = vmallocUsed;
		}

		public long getVmallocChunk() {
			return vmallocChunk;
		}

		public void setVmallocChunk(long vmallocChunk) {
			this.vmallocChunk = vmallocChunk;
		}

		public long getHardwareCorrupted() {
			return hardwareCorrupted;
		}

		public void setHardwareCorrupted(long hardwareCorrupted) {
			this.hardwareCorrupted = hardwareCorrupted;
		}

		public long getAnonHugePages() {
			return anonHugePages;
		}

		public void setAnonHugePages(long anonHugePages) {
			this.anonHugePages = anonHugePages;
		}

		public long getHugePagesTotal() {
			return hugePagesTotal;
		}

		public void setHugePagesTotal(long hugePagesTotal) {
			this.hugePagesTotal = hugePagesTotal;
		}

		public long getHugePagesFree() {
			return hugePagesFree;
		}

		public void setHugePagesFree(long hugePagesFree) {
			this.hugePagesFree = hugePagesFree;
		}

		public long getHugePagesRsvd() {
			return hugePagesRsvd;
		}

		public void setHugePagesRsvd(long hugePagesRsvd) {
			this.hugePagesRsvd = hugePagesRsvd;
		}

		public long getHugePagesSurp() {
			return hugePagesSurp;
		}

		public void setHugePagesSurp(long hugePagesSurp) {
			this.hugePagesSurp = hugePagesSurp;
		}

		public long getHugepagesize() {
			return hugepagesize;
		}

		public void setHugepagesize(long hugepagesize) {
			this.hugepagesize = hugepagesize;
		}

		public long getDirectMap4k() {
			return directMap4k;
		}

		public void setDirectMap4k(long directMap4k) {
			this.directMap4k = directMap4k;
		}

		public long getDirectMap2M() {
			return directMap2M;
		}

		public void setDirectMap2M(long directMap2M) {
			this.directMap2M = directMap2M;
		}

		public long getDirectMap1G() {
			return directMap1G;
		}

		public void setDirectMap1G(long directMap1G) {
			this.directMap1G = directMap1G;
		}

		@Override
		public String toString() {
			return "UnixMemInfo [swapCached=" + swapCached + ", active=" + active + ", inactive=" + inactive + ", activeAnon="
					+ activeAnon + ", inactiveAnon=" + inactiveAnon + ", activeFile=" + activeFile + ", inactiveFile="
					+ inactiveFile + ", unevictable=" + unevictable + ", mlocked=" + mlocked + ", swapTotal=" + swapTotal
					+ ", swapFree=" + swapFree + ", dirty=" + dirty + ", writeback=" + writeback + ", anonPages=" + anonPages
					+ ", mapped=" + mapped + ", shmem=" + shmem + ", slab=" + slab + ", sReclaimable=" + sReclaimable
					+ ", sUnreclaim=" + sUnreclaim + ", kernelStack=" + kernelStack + ", pageTables=" + pageTables
					+ ", nFS_Unstable=" + nFS_Unstable + ", bounce=" + bounce + ", writebackTmp=" + writebackTmp
					+ ", commitLimit=" + commitLimit + ", committed_AS=" + committed_AS + ", vmallocTotal=" + vmallocTotal
					+ ", vmallocUsed=" + vmallocUsed + ", vmallocChunk=" + vmallocChunk + ", hardwareCorrupted="
					+ hardwareCorrupted + ", anonHugePages=" + anonHugePages + ", hugePagesTotal=" + hugePagesTotal
					+ ", hugePagesFree=" + hugePagesFree + ", hugePagesRsvd=" + hugePagesRsvd + ", hugePagesSurp=" + hugePagesSurp
					+ ", hugepagesize=" + hugepagesize + ", directMap4k=" + directMap4k + ", directMap2M=" + directMap2M
					+ ", directMap1G=" + directMap1G + ", " + (super.toString() != null ? "toString()=" + super.toString() : "")
					+ "]";
		}

	}

	public static class WinMemInfo extends MemInfo {

		public WinMemInfo(long memTotal, long memFree, long buffers, long cached) {
			super(memTotal, memFree, buffers, cached);
		}

		@Override
		public String toString() {
			return "WinMemInfo [" + (super.toString() != null ? "toString()=" + super.toString() : "") + "]";
		}

	}

	public static void main(String[] args) throws Exception {
		for (int i = 0; i < 50; i++) {
			// System.out.println(cpuUsage());
			System.out.println(memInfo());
			Thread.sleep(8000L);
		}
	}

}
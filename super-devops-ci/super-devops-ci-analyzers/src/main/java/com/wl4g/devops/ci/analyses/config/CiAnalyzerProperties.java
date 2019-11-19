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
package com.wl4g.devops.ci.analyses.config;

import static com.wl4g.devops.common.utils.lang.SystemUtils2.LOCAL_PROCESS_ID;
import static com.wl4g.devops.common.utils.serialize.JacksonUtils.toJSONString;
import static java.lang.Thread.currentThread;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.SystemUtils.JAVA_IO_TMPDIR;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.ClassUtils.isPresent;

/**
 * CI analyses properties configuration.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月19日
 * @since
 */
public class CiAnalyzerProperties {

	private SpotbugsProperties spotbugs = new SpotbugsProperties();

	public SpotbugsProperties getSpotbugs() {
		return spotbugs;
	}

	public void setSpotbugs(SpotbugsProperties spotbugs) {
		this.spotbugs = spotbugs;
	}

	@Override
	public String toString() {
		return toJSONString(this);
	}

	/**
	 * SPOTBUGS properties configuration.
	 * 
	 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
	 * @version v1.0 2019年11月19日
	 * @since
	 */
	public static class SpotbugsProperties {

		private String jvmArgs = DEFAULT_JVM_ARGS;

		private String analyzerRunClass = "edu.umd.cs.findbugs.FindBugs2";

		public String getJvmArgs() {
			return jvmArgs;
		}

		public void setJvmArgs(String jvmArgs) {
			if (!isBlank(jvmArgs)) {
				this.jvmArgs = jvmArgs;
			}
		}

		public String getAnalyzerRunClass() {
			return analyzerRunClass;
		}

		public void setAnalyzerRunClass(String analyzerRunClass) {
			if (!isBlank(analyzerRunClass)) {
				isTrue(isPresent(analyzerRunClass, currentThread().getContextClassLoader()),
						String.format("Not found analyzerRunClass for: %s", analyzerRunClass));
				this.analyzerRunClass = analyzerRunClass;
			}
		}

	}

	// --- Default definitions. ---

	/** Default analyzer process JVM args. */
	final public static String DEFAULT_JVM_ARGS = defaultJvmArgString0();

	/**
	 * Get default analyzer process JVM args.
	 * 
	 * @return
	 */
	private final static String defaultJvmArgString0() {
		StringBuffer jvmArgs = new StringBuffer("-Xms256M -Xmx2G");
		jvmArgs.append(" -XX:+HeapDumpOnOutOfMemoryError");
		jvmArgs.append(" -XX:HeapDumpPath=" + JAVA_IO_TMPDIR + "/" + LOCAL_PROCESS_ID + "/jvm_dump.hprof");
		jvmArgs.append(" -XX:+UseG1GC");
		jvmArgs.append(" -XX:MaxGCPauseMillis=20");
		jvmArgs.append(" -XX:InitiatingHeapOccupancyPercent=35");
		jvmArgs.append(" -XX:+DisableExplicitGC");
		jvmArgs.append(" -verbose:gc");
		jvmArgs.append(" -Xloggc:" + JAVA_IO_TMPDIR + "/" + LOCAL_PROCESS_ID + "/gc.log");
		jvmArgs.append(" -XX:+PrintGCDetails");
		jvmArgs.append(" -XX:+PrintGCDateStamps");
		jvmArgs.append(" -XX:+PrintGCTimeStamps");
		jvmArgs.append(" -XX:+UseGCLogFileRotation");
		jvmArgs.append(" -XX:NumberOfGCLogFiles=10");
		jvmArgs.append(" -XX:GCLogFileSize=100M");
		jvmArgs.append(" -XX:GCLogFileSize=100M");
		jvmArgs.append(" -Dfile.encoding=UTF-8");
		jvmArgs.append(" -Djava.awt.headless=true");
		jvmArgs.append(" -Djava.security.egd=file:/dev/./urandom");
		return jvmArgs.toString();
	}

}

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
package com.wl4g.devops.ci.analyses;

import static java.util.Collections.singletonList;
import static org.springframework.util.Assert.hasText;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.devops.ci.analyses.model.SpotbugsProjectModel;
import com.wl4g.devops.common.task.GenericTaskRunner;
import com.wl4g.devops.common.task.RunnerProperties;
import com.wl4g.devops.support.cli.DestroableProcessManager;

/**
 * Abstract basic codes analyzers.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-11-18
 * @since
 */
public abstract class AbstractCodesAnalyzer extends GenericTaskRunner<RunnerProperties> implements CodesAnalyzer {

	@Autowired
	protected DestroableProcessManager processManager;

	@Override
	public void analyze(SpotbugsProjectModel model) throws Exception {

		submitForComplete(singletonList(() -> {
			String command = getAnalyzeProcessCommand(model);
			try {
				processManager.exec(model.getProjectName(), command, 15 * 60 * 1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}), 15 * 60 * 1000);

	}

	protected abstract void doAnalyze(SpotbugsProjectModel model) throws Exception;

	/**
	 * Get independent analyzer sub-process command-line string. </br>
	 * 
	 * <pre>
	 * java -Xms256M -Xmx2G -cp  .:/opt/apps/ci-analyers/lib  edu.umd.cs.findbugs.FindBugs2
	 * </pre>
	 * 
	 * @return
	 */
	protected String getAnalyzeProcessCommand(SpotbugsProjectModel model) {
		StringBuffer cmd = new StringBuffer("java ");
		cmd.append(getJvmArgs("/tmp/" + model.getProjectName()));
		cmd.append(" -cp .");
		cmd.append(File.pathSeparator);
		cmd.append(System.getProperty("java.class.path"));
		cmd.append(" ");
		return cmd.toString();
	}

	protected String getJvmArgs(String tmpDir) {
		hasText(tmpDir, "empty tmpDir");
		StringBuffer jvmArgs = new StringBuffer("java -Xms256M -Xmx2G");
		jvmArgs.append(" -XX:+HeapDumpOnOutOfMemoryError");
		jvmArgs.append(" -XX:HeapDumpPath=" + tmpDir + "/jvm_dump.hprof");
		jvmArgs.append(" -XX:+UseG1GC");
		jvmArgs.append(" -XX:MaxGCPauseMillis=20");
		jvmArgs.append(" -XX:InitiatingHeapOccupancyPercent=35");
		jvmArgs.append(" -XX:+DisableExplicitGC");
		jvmArgs.append(" -verbose:gc");
		jvmArgs.append(" -Xloggc:" + tmpDir + "/gc.log");
		jvmArgs.append(" -XX:+PrintGCDetails");
		jvmArgs.append(" -XX:+PrintGCDateStamps");
		jvmArgs.append(" -XX:+PrintGCTimeStamps");
		jvmArgs.append(" -XX:+UseGCLogFileRotation");
		jvmArgs.append(" -XX:NumberOfGCLogFiles=10");
		jvmArgs.append(" -XX:GCLogFileSize=100M");
		jvmArgs.append(" -XX:GCLogFileSize=100M");
		jvmArgs.append(" -Dfile.encoding=UTF-8");
		jvmArgs.append(" -Djava.awt.headless=true");
		jvmArgs.append(" -Dfile.encoding=UTF-8 -Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom");
		return jvmArgs.toString();
	}

}

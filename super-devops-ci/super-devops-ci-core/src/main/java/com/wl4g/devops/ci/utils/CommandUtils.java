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
package com.wl4g.devops.ci.utils;

import com.wl4g.devops.common.exception.support.IllegalProcessStateException;
import com.wl4g.devops.common.utils.io.FileIOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Shell utility tools.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月24日
 * @since
 */
public abstract class CommandUtils {
	final private static Logger log = LoggerFactory.getLogger(CommandUtils.class);

	public static Map<Integer, List<Process>> processMap = new HashMap<>();

	/**
	 * Execute commands in local
	 */
	public static void exec(String cmd) throws Exception {
		exec(cmd, null, null);
	}

	public static void exec(String cmd, String logPath, Integer taskId) throws Exception {
		exec(cmd, null, logPath, taskId);
	}

	public static void exec(String cmd, String dirPath, String logPath, Integer taskId) throws Exception {
		if (log.isInfoEnabled()) {
			log.info("Execution native command for '{}'", cmd);
		}
		// TODO filter command
		if (StringUtils.isNotBlank(logPath)) {
			cmd = cmd + " 2>&1 | tee -a " + logPath;
		}

		StringBuilder slog = new StringBuilder();
		StringBuilder serr = new StringBuilder();
		Process ps;
		if (StringUtils.isBlank(dirPath)) {
			ps = Runtime.getRuntime().exec(new String[] { "/bin/bash", "-c", cmd });
		} else {
			ps = Runtime.getRuntime().exec(new String[] { "/bin/bash", "-c", cmd }, null, new File(dirPath));
		}
		addProcess(taskId, ps);

		ps.waitFor();// wait for process exit
		int exitValue = ps.exitValue();

		if (exitValue != 0) {
			if (exitValue == 143) {
				throw new IllegalProcessStateException("Manual Stop Task");
			}
			throw new IllegalStateException(String.format("Failed to exec write file command=%s , logPath=%s)", cmd, logPath));
		}

	}

	public static void execFile(String cmd, String filePath, String logPath, Integer taskId) throws Exception {
		File file = new File(filePath);
		FileIOUtils.writeFile(file, cmd, false);
		exec("sh " + filePath, logPath, taskId);
	}

	private static void addProcess(Integer taskId, Process ps) {
		if (null == taskId || null == ps) {
			return;
		}
		List<Process> processes = processMap.get(taskId);
		if (null == processes) {
			processes = new ArrayList<>();
		}
		processes.add(ps);
		processMap.put(taskId, processes);
	}

	public static void killByTaskId(Integer taskId) {
		List<Process> processes = processMap.get(taskId);
		if (CollectionUtils.isEmpty(processes)) {
			return;
		}
		for (Process ps : processes) {
			ps.destroy();
		}
		// processMap.remove(taskId);
	}

	public static void main(String[] args) throws Exception {
		Process ps = Runtime.getRuntime().exec(new String[] { "/bin/bash", "-c",
				"sh /Users/vjay/Downloads/myTest.sh 2>&1 | tee -a /Users/vjay/Downloads/zz.log" });

		long time = System.currentTimeMillis();
		try (BufferedReader blog = new BufferedReader(new InputStreamReader(ps.getInputStream()));
				BufferedReader berr = new BufferedReader(new InputStreamReader(ps.getErrorStream()));
				OutputStream outputStream = ps.getOutputStream();) {
			String inlog;
			while (System.currentTimeMillis() - time <= 5000) {

			}
			System.out.println("stop");
			ps.destroyForcibly();
			/*
			 * while ((inlog = blog.readLine()) != null) {
			 * if(inlog!=null&&inlog.contains("[3/4]")){
			 * System.out.println("stop"); ps.destroyForcibly();
			 *//*
				 * char ctrlBreak = (char)3; outputStream.write(ctrlBreak);
				 * outputStream.flush();
				 *//*
					 * } System.out.println(inlog); } while ((inlog =
					 * berr.readLine()) != null) { System.out.println(inlog); }
					 */

			ps.waitFor();
			int exitValue = ps.exitValue();
			System.out.println(exitValue);
		}
	}

}
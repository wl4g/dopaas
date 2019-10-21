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
package com.wl4g.devops.support.cli.repository;

import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.annotations.Beta;
import com.wl4g.devops.common.exception.ci.NoSuchCommandLineProcessException;

/**
 * Command-line process repository interface.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-10-20
 * @since
 */
@Beta
public interface ProcessRepository {

	/**
	 * Registration to command-line process.
	 * 
	 * @param processId
	 * @param process
	 */
	void register(Serializable processId, ProcessInfo process);

	/**
	 * Get command-line process information.
	 * 
	 * @param processId
	 * @return
	 */
	ProcessInfo getProcessInfo(Serializable processId) throws NoSuchCommandLineProcessException;

	/**
	 * Remove cleanup command-line process information.
	 * 
	 * @param processId
	 * @return
	 */
	ProcessInfo cleanup(Serializable processId);

	/**
	 * Command-line process information bean.
	 * 
	 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version v1.0.0 2019-10-20
	 * @since
	 */
	public static class ProcessInfo implements Serializable {
		private static final long serialVersionUID = 1013208493410008301L;

		/** Process ID */
		final private Serializable processId;

		/** Process context directory */
		final private File pwdDir;

		/** Process commands */
		final private List<String> commands;

		/** Process commands standard output file */
		final private File stdout;

		/** Process object */
		@JsonIgnore
		final transient private Process process;

		public ProcessInfo(Serializable processId, File pwdDir, List<String> commands, File stdout, Process process) {
			notNull(processId, "Execution commands processId must not be null");
			notEmpty(commands, "Execution commands must not be empty");
			notNull(process, "Execution process must not be null");
			this.processId = processId;
			this.pwdDir = pwdDir;
			this.commands = commands;
			this.stdout = stdout;
			this.process = process;
		}

		public Serializable getProcessId() {
			return processId;
		}

		public File getPwdDir() {
			return pwdDir;
		}

		public List<String> getCommands() {
			return commands;
		}

		public File getStdout() {
			return stdout;
		}

		public Process getProcess() {
			return process;
		}

	}

}

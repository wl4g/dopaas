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
package com.wl4g.devops.ci.analyses.model;

import static org.apache.shiro.util.Assert.notEmpty;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;

import java.util.List;

import edu.umd.cs.findbugs.gui2.AnalysisCallback;

/**
 * SPHOTBUGS project model.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-11-18
 * @since
 */
public class SpotbugsProjectModel implements ProjectModel {

	final private transient AnalysisCallback callback;

	/**
	 * The analyze project name.
	 */
	final private String projectName;

	/**
	 * The list of source directories.
	 */
	final private List<String> args;

	public SpotbugsProjectModel(AnalysisCallback callback, String projectName, List<String> args) {
		notNull(callback, "null analysisCallback");
		notEmpty(args, "empty analysis arguments");
		hasText(projectName, "empty analysis projectName");
		this.callback = callback;
		this.args = args;
		this.projectName = projectName;
	}

	public AnalysisCallback getCallback() {
		return callback;
	}

	public String getProjectName() {
		return projectName;
	}

	public List<String> getArgs() {
		return args;
	}

}

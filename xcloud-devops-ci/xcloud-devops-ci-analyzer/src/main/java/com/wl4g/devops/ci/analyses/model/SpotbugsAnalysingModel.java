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
package com.wl4g.devops.ci.analyses.model;

import static com.wl4g.component.common.serialize.JacksonUtils.toJSONString;
import static org.apache.shiro.util.Assert.notEmpty;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.notNull;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.umd.cs.findbugs.gui2.AnalysisCallback;

/**
 * SPHOTBUGS project model.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2019-11-18
 * @since
 */
public class SpotbugsAnalysingModel extends AnalysingModel {
	private static final long serialVersionUID = 8592166141479118698L;

	/** Temporary analysis callback. */
	@JsonIgnore
	final private transient AnalysisCallback callback;

	/**
	 * The list of source directories.
	 */
	final private List<String> args;

	public SpotbugsAnalysingModel(AnalysisCallback callback, String projectName, List<String> args) {
		super(projectName);
		notNull(callback, "null analysisCallback");
		notEmpty(args, "empty analysis arguments");
		hasText(projectName, "empty analysis projectName");
		this.callback = callback;
		this.args = args;
	}

	public AnalysisCallback getCallback() {
		return callback;
	}

	public List<String> getArgs() {
		return args;
	}

	@Override
	public String toString() {
		return toJSONString(this);
	}

}
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

import static com.wl4g.devops.common.utils.serialize.JacksonUtils.toJSONString;

import java.io.Serializable;

/**
 * Analyzing stage progress model.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月20日
 * @since
 */
public class StageProgressModel implements Serializable {
	private static final long serialVersionUID = 8386866351185047399L;

	private String message;
	private int pass;
	private int goal;

	public StageProgressModel() {
		super();
	}

	public StageProgressModel(String message, int pass, int goal) {
		super();
		this.message = message;
		this.pass = pass;
		this.goal = goal;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getPass() {
		return pass;
	}

	public void setPass(int pass) {
		this.pass = pass;
	}

	public int getGoal() {
		return goal;
	}

	public void setGoal(int goal) {
		this.goal = goal;
	}

	@Override
	public String toString() {
		return "AnalyzingProgressModel [" + toJSONString(this) + "]";
	}

}

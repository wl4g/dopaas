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
package com.wl4g.devops.ci.core.param;

import javax.validation.constraints.NotNull;

import static org.springframework.util.Assert.notNull;

/**
 * New create pipeline handle parameter.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月12日
 * @since
 */
public class NewParameter extends GenericParameter {
	private static final long serialVersionUID = -79398460376632146L;

	/**
	 * [Extensible]</br>
	 * Pipeline Pipeline task processing depends on external task tracking ID
	 * (e.g. task ID of external project or business docs management system).
	 */
	@NotNull
	private String taskTraceId;

	/**
	 * External task tracking type.
	 * 
	 * @see {@link #taskTraceId}
	 */
	@NotNull
	private Integer taskTraceType;

	public NewParameter() {
		super();
	}

	public NewParameter(Integer taskId, String remark, @NotNull String taskTraceId, @NotNull Integer taskTraceType) {
		super(taskId, remark);
		setTaskTraceId(taskTraceId);
		setTaskTraceType(taskTraceType);
	}

	public String getTaskTraceId() {
		return taskTraceId;
	}

	public void setTaskTraceId(String taskTraceId) {
		notNull(taskTraceId, "Pipeline taskTraceId can't be null.");
		this.taskTraceId = taskTraceId;
	}

	public Integer getTaskTraceType() {
		return taskTraceType;
	}

	public void setTaskTraceType(Integer taskTraceType) {
		notNull(taskTraceType, "Pipeline taskTraceType can't be null.");
		this.taskTraceType = taskTraceType;
	}

}
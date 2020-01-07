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

import static com.wl4g.devops.tool.common.serialize.JacksonUtils.toJSONString;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Generic pipeline handle command parameter.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月12日
 * @since
 */
public abstract class GenericParameter implements Serializable {

	private static final long serialVersionUID = -5304513598862948298L;

	/**
	 * Pipeline taskId.
	 */
	@NotNull
	private Integer taskId;

	/**
	 * Pipeline task remark.
	 */
	@NotBlank
	private String remark;

	public GenericParameter() {
		super();
	}

	public GenericParameter(Integer taskId, String remark) {
		setTaskId(taskId);
		setRemark(remark);
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		notNull(taskId, "Pipeline taskId can't be null.");
		isTrue(taskId >= 0, "Pipeline taskId must be >=0.");
		this.taskId = taskId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		hasText(remark, "Pipiline task remark can't be empty.");
		this.remark = remark;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " => " + toJSONString(this);
	}

}
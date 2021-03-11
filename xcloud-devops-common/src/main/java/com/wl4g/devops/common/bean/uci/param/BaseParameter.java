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
package com.wl4g.devops.common.bean.uci.param;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

import static com.wl4g.component.common.serialize.JacksonUtils.toJSONString;
import static org.springframework.util.Assert.*;

/**
 * Generic pipeline handle command parameter.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月12日
 * @since
 */
public abstract class BaseParameter implements Serializable {

	private static final long serialVersionUID = -5304513598862948298L;

	/**
	 * Pipeline taskId.
	 */
	@NotNull
	private Long pipeId;

	/**
	 * Pipeline task remark.
	 */
	@NotBlank
	private String remark;

	public BaseParameter() {
		super();
	}

	public BaseParameter(Long pipeId, String remark) {
		setPipeId(pipeId);
		setRemark(remark);
	}

	public Long getPipeId() {
		return pipeId;
	}

	public void setPipeId(Long pipeId) {
		notNull(pipeId, "Pipeline pipeId can't be null.");
		isTrue(pipeId >= 0, "Pipeline pipeId must be >=0.");
		this.pipeId = pipeId;
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
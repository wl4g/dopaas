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
package com.wl4g.paas.common.bean.uci.param;

import com.wl4g.paas.common.bean.uci.model.PipelineModel;

/**
 * Roll-back pipeline handle parameter.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月12日
 * @since
 */
public class RollbackParameter extends BaseParameter {
	private static final long serialVersionUID = 1489325413465499589L;

	private PipelineModel pipeModel;

	public RollbackParameter() {
		super();
	}

	public RollbackParameter(Long pipeId, String remark, PipelineModel pipeModel) {
		super(pipeId, remark);
		this.pipeModel = pipeModel;
	}

	public PipelineModel getPipeModel() {
		return pipeModel;
	}

	public void setPipeModel(PipelineModel pipeModel) {
		this.pipeModel = pipeModel;
	}

}
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
package com.wl4g.devops.common.bean.ci;

import com.wl4g.component.core.bean.BaseBean;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * {@link OrchestrationHistory}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2019-11-06
 * @sine v1.0.0
 * @see
 */
@Getter
@Setter
public class OrchestrationHistory extends BaseBean {
	private static final long serialVersionUID = 6815608076300843748L;

	private String runId;
	private Integer status;
	private String info;
	private Long costTime;
	private List<PipelineHistory> pipeHistories;

}
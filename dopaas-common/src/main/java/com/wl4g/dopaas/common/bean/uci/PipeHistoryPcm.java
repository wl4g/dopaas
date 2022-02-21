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
package com.wl4g.dopaas.common.bean.uci;

import java.util.Date;

import com.wl4g.component.core.bean.BaseBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PipeHistoryPcm extends BaseBean {
	private static final long serialVersionUID = 6815608076300843748L;

	private Integer enable;
	private Long pipeId;
	private Long pcmId;
	private Long xProjectId;
	private String xTracker;
	private String xStatus;
	private String xSubject;
	private String xDescription;
	private String xPriority;
	private String xAssignTo;
	private Date xStartDate;
	private Long xExpectedTime;
	private String xCustomFields;
	private String xParentIssueId;

}
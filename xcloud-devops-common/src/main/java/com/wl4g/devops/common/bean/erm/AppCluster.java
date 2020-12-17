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
package com.wl4g.devops.common.bean.erm;

import java.util.List;

import com.wl4g.component.core.bean.BaseBean;

import lombok.Getter;
import lombok.Setter;

/**
 * 对应表：app_cluster
 *
 * @date 2018年9月19日
 */
@Getter
@Setter
public class AppCluster extends BaseBean {
	private static final long serialVersionUID = -3298424126317938674L;

	private String name;
	private Integer type;
	private Integer enable;
	private String remark;
	private String endpoint;
	private Long sshId;
	private Integer deployType;
	private SshBean ssh;

	// --- Temporary. ---

	private Long instanceCount;
	private List<AppInstance> instances;
	private List<AppEnvironment> environments;

}
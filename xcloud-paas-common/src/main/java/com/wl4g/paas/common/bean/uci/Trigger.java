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
package com.wl4g.paas.common.bean.uci;

import java.io.Serializable;

import com.wl4g.component.core.bean.BaseBean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Trigger extends BaseBean implements Serializable {

	private static final long serialVersionUID = 381411777614066880L;

	private String name;
	private Long appClusterId;
	private Long taskId;
	private Integer type;
	private String cron;
	private String sha;

}
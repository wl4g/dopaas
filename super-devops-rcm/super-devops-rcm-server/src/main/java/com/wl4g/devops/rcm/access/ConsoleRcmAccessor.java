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
package com.wl4g.devops.rcm.access;

import org.springframework.beans.factory.annotation.Autowired;

import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.rcm.RcmProvider;
import com.wl4g.devops.rcm.RiskAnalysisEngine;
import com.wl4g.devops.rcm.access.model.GenericRcmParameter;
import com.wl4g.devops.shell.annotation.ShellComponent;

/**
 * Console based RCM accessor
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020年3月4日
 * @since
 */
@ShellComponent
public class ConsoleRcmAccessor implements RcmAccessor {

	final public static String CONSOLE_GROUP = "Risk Control Console";

	/**
	 * {@link RiskAnalysisEngine}
	 */
	@Autowired
	protected GenericOperatorAdapter<RcmProvider, RiskAnalysisEngine> engineAdapter;

	/**
	 * Gets {@link RiskAnalysisEngine}
	 * 
	 * @param param
	 * @return
	 */
	@SuppressWarnings("unused")
	private RiskAnalysisEngine getCossEndpoint(GenericRcmParameter param) {
		return engineAdapter.forOperator(param.getRcmProvider());
	}

}

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
package com.wl4g.devops.umc.rule.inspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wl4g.devops.umc.rule.OperatorType;

/**
 * Abstract rule inspecctor
 * 
 * @author Wangl.sir
 * @author vjay
 * @date 2019-07-05 10:01:00
 */
public abstract class AbstractRuleInspector implements RuleInspector {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Do operation
	 * 
	 * @param operator
	 * @param value1
	 * @param value2
	 * @return
	 */
	protected boolean operate(OperatorType operator, double value1, double value2) {
		return operator.operate(value1, value2);
	}

}
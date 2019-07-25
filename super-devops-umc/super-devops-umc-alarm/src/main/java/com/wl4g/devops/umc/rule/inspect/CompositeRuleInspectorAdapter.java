/**
 * Copyright 2017 ~ 2025 the original author or authors[983708408@qq.com].
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

import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

import com.wl4g.devops.umc.rule.AggregatorType;
import com.wl4g.devops.umc.rule.OperatorType;

/**
 * Composite rule inspector adapter.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年7月25日
 * @since
 */
public class CompositeRuleInspectorAdapter extends AbstractRuleInspector {

	/**
	 * Rule inspectors.
	 */
	final protected Map<AggregatorType, RuleInspector> ruleInspectors = new LinkedHashMap<>();

	public CompositeRuleInspectorAdapter(List<RuleInspector> inspectors) {
		Assert.state(!isEmpty(inspectors), "Rule inspectors has at least one.");
		inspectors.forEach(inspector -> inspectors.put(inspector.inspectType(), inspector));
	}

	@Override
	public boolean verify(Double[] values, OperatorType operatorEnum, double standard) {
		return false;
	}

	@Override
	public AggregatorType inspectType() {
		throw new UnsupportedOperationException();
	}

}

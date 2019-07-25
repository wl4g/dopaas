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

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.wl4g.devops.umc.notification.AlarmType;
import com.wl4g.devops.umc.rule.AggregatorType;

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
		Assert.state(!CollectionUtils.isEmpty(inspectors), "Rule inspectors has at least one.");
		inspectors.forEach(inspector -> ruleInspectors.put(inspector.aggregateType(), inspector));
	}

	@Override
	public boolean verify(InspectWrapper wrap) {
		Optional<RuleInspector> opt = determineRuleInspector("");
		return opt.isPresent() ? opt.get().verify(wrap) : false;
	}

	@Override
	public AggregatorType aggregateType() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Determine rule inspector.
	 * 
	 * @param aggregateType
	 * @return
	 */
	protected Optional<RuleInspector> determineRuleInspector(String aggregateType) {
		if (isBlank(aggregateType)) {
			log.warn("Unsupported this rule aggregate type: {}", aggregateType);
			return Optional.empty();
		}
		for (String part : aggregateType.split(",")) {
			AlarmType type = AlarmType.safeOf(part);
			if (null != type) {
				RuleInspector inspector = ruleInspectors.get(type);
				if (inspector != null) {
					return Optional.of(inspector);
				}
			}
		}
		return Optional.empty();
	}

}

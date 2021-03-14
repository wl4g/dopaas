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
package com.wl4g.paas.umc.rule.inspect;

import com.wl4g.paas.umc.rule.Aggregator;
import com.wl4g.paas.umc.rule.LogicalOperator;
import com.wl4g.paas.umc.rule.RelationOperator;

import static com.wl4g.component.common.collection.CollectionUtils2.safeToList;
import static java.util.stream.Collectors.summarizingDouble;

/**
 * Maximum rule inspector
 * 
 * @author Wangl.sir
 * @author vjay
 * @date 2019-07-05 10:02:00
 */
public class MaxRuleInspector extends AbstractRuleInspector {

	@Override
	public Aggregator aggregateType() {
		return Aggregator.MAX;
	}

	@Override
	public boolean verify(InspectWrapper wrap) {
		// Maximum
		double max = safeToList(Double.class, wrap.getValues()).stream().filter(val -> null != val)
				.collect(summarizingDouble(val -> val)).getMax();
		wrap.setCompareValue(max);
		return super.operate(LogicalOperator.of(wrap.getLogicalOperator()), RelationOperator.of(wrap.getRelateOperator()), max,
				wrap.getBaseline());
	}

}
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
package com.wl4g.devops.umc.rule.inspect;

import static com.wl4g.components.common.collection.Collections2.isEmptyArray;

import com.wl4g.devops.umc.rule.Aggregator;
import com.wl4g.devops.umc.rule.LogicalOperator;
import com.wl4g.devops.umc.rule.RelationOperator;

/**
 * Latest rule inspector
 * 
 * @author Wangl.sir
 * @author vjay
 * @date 2019-07-05 10:02:00
 */
public class LatestRuleInspector extends AbstractRuleInspector {

	@Override
	public Aggregator aggregateType() {
		return Aggregator.LATEST;
	}

	@Override
	public boolean verify(InspectWrapper wrap) {
		if (isEmptyArray(wrap.getValues())) {
			return false;
		}
		// Latest/Last
		double latest = wrap.getValues()[wrap.getValues().length - 1];
		wrap.setCompareValue(latest);
		return super.operate(LogicalOperator.of(wrap.getLogicalOperator()), RelationOperator.of(wrap.getRelateOperator()), latest,
				wrap.getBaseline());
	}

}
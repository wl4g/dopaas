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

import java.util.Arrays;

import org.springframework.util.Assert;

import com.wl4g.devops.umc.rule.AggregatorType;
import com.wl4g.devops.umc.rule.OperatorType;

/**
 * Rule verify inspector.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年7月5日
 * @since
 */
public interface RuleInspector {

	AggregatorType aggregateType();

	/**
	 * Metric aggregate rule inspection
	 * 
	 * @param wrap
	 * @return
	 */
	boolean verify(InspectWrapper wrap);

	/**
	 * Metric inspection wrapper.
	 * 
	 * @author Wangl.sir
	 * @version v1.0 2019年7月24日
	 * @since
	 */
	public static class InspectWrapper {

		final private OperatorType operator;

		final private AggregatorType aggregator;

		final private Double baseline;

		final private Double[] values;

		public InspectWrapper(OperatorType operator, AggregatorType aggregator, Double baseline, Double[] values) {
			Assert.isNull(operator, "Operator type must not be null");
			Assert.isNull(aggregator, "Aggregator type must not be null");
			this.operator = operator;
			this.aggregator = aggregator;
			this.baseline = baseline;
			this.values = values;
		}

		public OperatorType getOperator() {
			return operator;
		}

		public AggregatorType getAggregator() {
			return aggregator;
		}

		public Double[] getValues() {
			return values;
		}

		public Double getBaseline() {
			return baseline;
		}

		@Override
		public String toString() {
			return "MeticInspectWrapper [operator=" + operator + ", aggregator=" + aggregator + ", values="
					+ Arrays.toString(values) + ", baseline=" + baseline + "]";
		}

	}

}
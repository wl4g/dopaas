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

import com.wl4g.devops.umc.rule.Aggregator;
import org.springframework.util.Assert;

import java.util.Arrays;

/**
 * Rule verify inspector.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年7月5日
 * @since
 */
public interface RuleInspector {

	Aggregator aggregateType();

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

		final private Integer relateOperator;

		final private Integer logicalOperator;

		final private String aggregator;

		final private Double baseline;

		final private Double[] values;

		private Double compareValue;

		public InspectWrapper(Integer logicalOperator, Integer relateOperator, String aggregator, Double baseline,
				Double[] values) {
			Assert.notNull(logicalOperator, "Logical operator must not be null");
			Assert.notNull(relateOperator, "Relate operator must not be null");
			Assert.hasText(aggregator, "Aggregator type must not be empty");
			this.logicalOperator = logicalOperator;
			this.relateOperator = relateOperator;
			this.aggregator = aggregator;
			this.baseline = baseline;
			this.values = values;
		}

		public Integer getLogicalOperator() {
			return logicalOperator;
		}

		public Integer getRelateOperator() {
			return relateOperator;
		}

		public String getAggregator() {
			return aggregator;
		}

		public Double[] getValues() {
			return values;
		}

		public Double getBaseline() {
			return baseline;
		}

		public Double getCompareValue() {
			return compareValue;
		}

		public void setCompareValue(Double compareValue) {
			this.compareValue = compareValue;
		}

		@Override
		public String toString() {
			return "MeticInspectWrapper [operator=" + relateOperator + ", aggregator=" + aggregator + ", values="
					+ Arrays.toString(values) + ", baseline=" + baseline + "]";
		}

	}

}
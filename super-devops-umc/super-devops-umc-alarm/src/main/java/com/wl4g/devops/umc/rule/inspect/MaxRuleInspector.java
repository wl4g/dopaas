package com.wl4g.devops.umc.rule.inspect;

import com.wl4g.devops.umc.rule.OperatorType;

/**
 * @author vjay
 * @date 2019-07-05 10:02:00
 */
public class MaxRuleInspector extends AbstractRuleInspector {

	@Override
	public boolean judge(Double[] values, OperatorType operatorEnum, double standard) {
		if (values == null || values.length <= 0) {
			return false;
		}
		double operatorResult = 0;
		for (double value : values) {
			if (value > operatorResult) {
				operatorResult = value;
			}
		}
		return super.operate(operatorEnum, operatorResult, standard);
	}
}

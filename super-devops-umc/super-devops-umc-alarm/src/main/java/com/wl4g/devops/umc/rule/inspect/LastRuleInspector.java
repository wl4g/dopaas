package com.wl4g.devops.umc.rule.inspect;

import com.wl4g.devops.umc.rule.OperatorType;

/**
 * @author vjay
 * @date 2019-07-05 10:02:00
 */
public class LastRuleInspector extends AbstractRuleInspector {

	@Override
	public boolean verify(Double[] values, OperatorType operatorEnum, double standard) {
		if (values == null || values.length <= 0) {
			return false;
		}
		double operatorResult = values[values.length - 1];
		return super.operate(operatorEnum, operatorResult, standard);
	}
}

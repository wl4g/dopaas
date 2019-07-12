package com.wl4g.devops.umc.rule.inspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wl4g.devops.umc.rule.OperatorType;

/**
 * @author vjay
 * @date 2019-07-05 10:01:00
 */
public abstract class AbstractRuleInspector implements RuleInspector {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	protected boolean operate(OperatorType operator, double value1, double value2) {
		if (operator.getValue() == OperatorType.EQ.getValue()) {
			return value1 == value2;
		} else if (operator.getValue() == OperatorType.GT.getValue()) {
			return value1 > value2;
		} else if (operator.getValue() == OperatorType.GTE.getValue()) {
			return value1 >= value2;
		} else if (operator.getValue() == OperatorType.LT.getValue()) {
			return value1 < value2;
		} else if (operator.getValue() == OperatorType.LTE.getValue()) {
			return value1 <= value2;
		} else {
			log.info("not match operatorEnum");
		}
		return false;
	}

}

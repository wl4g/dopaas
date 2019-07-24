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

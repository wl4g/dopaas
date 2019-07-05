package com.wl4g.devops.umc.rule;

import com.wl4g.devops.common.enums.OperatorEnum;

/**
 * @author vjay
 * @date 2019-07-05 10:02:00
 */
public class LastRuleJedge extends AbstractRuleJudge{


    @Override
    public boolean judge(Double[] values, OperatorEnum operatorEnum, double standard) {
        if(values==null||values.length<=0){
            return false;
        }
        double operatorResult = values[values.length-1];
        return super.operator(operatorEnum,operatorResult,standard);
    }
}

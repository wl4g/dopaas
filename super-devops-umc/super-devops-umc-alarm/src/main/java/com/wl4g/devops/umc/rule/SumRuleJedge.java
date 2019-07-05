package com.wl4g.devops.umc.rule;

import com.wl4g.devops.common.enums.Operator;

/**
 * @author vjay
 * @date 2019-07-05 10:02:00
 */
public class SumRuleJedge extends AbstractRuleJudge{


    @Override
    public boolean judge(Double[] values, Operator operator,double standard) {
        if(values==null||values.length<=0){
            return false;
        }
        double operatorResult = 0;
        for(double value : values){
            operatorResult += value;
        }
        return super.operator(operator,operatorResult,standard);
    }
}

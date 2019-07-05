package com.wl4g.devops.umc.rule;

import com.wl4g.devops.common.enums.OperatorEnum;

/**
 * @author vjay
 * @date 2019-07-05 10:02:00
 */
public class MinRuleJedge extends AbstractRuleJudge{


    @Override
    public boolean judge(Double[] values, OperatorEnum operatorEnum, double standard) {
        if(values==null||values.length<=0){
            return false;
        }
        double operatorResult = 0;
        for(double value : values){
            if(value<operatorResult){
                operatorResult = value;
            }
        }
        return super.operator(operatorEnum,operatorResult,standard);
    }
}

package com.wl4g.devops.umc.rule;

import com.wl4g.devops.common.enums.OperatorEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vjay
 * @date 2019-07-05 10:01:00
 */
public abstract class AbstractRuleJudge {

    final protected Logger log = LoggerFactory.getLogger(getClass());


    public abstract boolean judge(Double[] values, OperatorEnum operatorEnum, double standard);


    public boolean operator(OperatorEnum operatorEnum, double value1, double value2){
        if(operatorEnum.getValue()== OperatorEnum.EQ.getValue()){
            return value1==value2;
        }else if(operatorEnum.getValue()== OperatorEnum.GT.getValue()){
            return value1>value2;
        }else if(operatorEnum.getValue()== OperatorEnum.GTE.getValue()){
            return value1>=value2;
        }else if(operatorEnum.getValue()== OperatorEnum.LT.getValue()){
            return value1<value2;
        }else if(operatorEnum.getValue()== OperatorEnum.LTE.getValue()){
            return value1<=value2;
        }else{
            log.info("not match operatorEnum");
        }
        return false;
    }


}

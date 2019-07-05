package com.wl4g.devops.umc.rule;

import com.wl4g.devops.common.enums.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vjay
 * @date 2019-07-05 10:01:00
 */
public abstract class AbstractRuleJudge {

    final protected Logger log = LoggerFactory.getLogger(getClass());


    public abstract boolean judge(Double[] values, Operator operator,double standard);


    public boolean operator(Operator operator,double value1,double value2){
        if(operator.getValue()==Operator.EQ.getValue()){
            return value1==value2;
        }else if(operator.getValue()==Operator.GT.getValue()){
            return value1>value2;
        }else if(operator.getValue()==Operator.GTE.getValue()){
            return value1>=value2;
        }else if(operator.getValue()==Operator.LT.getValue()){
            return value1<value2;
        }else if(operator.getValue()==Operator.LTE.getValue()){
            return value1<=value2;
        }else{
            log.info("not match operator");
        }
        return false;
    }


}

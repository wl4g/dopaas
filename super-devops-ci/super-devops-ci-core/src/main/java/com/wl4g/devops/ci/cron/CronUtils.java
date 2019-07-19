package com.wl4g.devops.ci.cron;

import org.quartz.CronExpression;
import org.quartz.TriggerUtils;
import org.quartz.impl.triggers.CronTriggerImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author vjay
 * @date 2019-07-19 14:33:00
 */
public class CronUtils {

    public static boolean isValidExpression(String expression){
        return CronExpression.isValidExpression(expression);
    }

    public static List<String> getNextExecTime(String expression, Integer numTimes) {
        List<String> list = new ArrayList<>();
        CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
        try {
            cronTriggerImpl.setCronExpression(expression);
        } catch(ParseException e) {
            e.printStackTrace();
        }
        // 这个是重点，一行代码搞定
        List<Date> dates = TriggerUtils.computeFireTimes(cronTriggerImpl, null, numTimes);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Date date : dates) {
            list.add(dateFormat.format(date));
        }
        return list;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(getNextExecTime("0 0/2 * * * ?", 5)+"\n");
    }


}

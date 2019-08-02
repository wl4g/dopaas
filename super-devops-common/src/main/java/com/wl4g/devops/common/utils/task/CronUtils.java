package com.wl4g.devops.common.utils.task;

import static com.wl4g.devops.common.utils.DateUtils.formatDate;
import static com.wl4g.devops.common.utils.lang.Collections2.safeList;
import static java.util.stream.Collectors.toList;
import static org.quartz.TriggerUtils.computeFireTimes;
import org.quartz.CronExpression;
import org.quartz.impl.triggers.CronTriggerImpl;

import java.text.ParseException;
import java.util.List;

/**
 * Quartz task cron expression utility.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年8月2日
 * @since
 */
public abstract class CronUtils {

	/**
	 * Check the expression is Valid
	 */
	public static boolean isValidExpression(String expression) {
		return CronExpression.isValidExpression(expression);
	}

	/**
	 * Get the expression next numTimes -- run time
	 */
	public static List<String> getNextExecTime(String expression, Integer numTimes) {
		CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
		try {
			cronTriggerImpl.setCronExpression(expression);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
		return safeList(computeFireTimes(cronTriggerImpl, null, numTimes)).stream().map(d -> formatDate(d, "yyyy-MM-dd HH:mm:ss"))
				.collect(toList());
	}

}

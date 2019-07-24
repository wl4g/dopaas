package com.wl4g.devops.umc.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Based multiple channel notifier.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public abstract class AbstractAlarmNotifier implements AlarmNotifier {

	final protected Logger log = LoggerFactory.getLogger(getClass());

}

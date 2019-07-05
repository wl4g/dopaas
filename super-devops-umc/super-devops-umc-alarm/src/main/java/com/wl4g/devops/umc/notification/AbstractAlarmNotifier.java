package com.wl4g.devops.umc.notification;

import java.util.List;
import java.util.Map;

/**
 * Based multiple channel notifier.
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public abstract class AbstractAlarmNotifier implements AlarmNotifier {

	@Override
	public void simpleNotify(List<String> targets, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void templateNotify(List<String> targets, String templateCode, Map<String, Object> message) {
		// TODO Auto-generated method stub

	}

}

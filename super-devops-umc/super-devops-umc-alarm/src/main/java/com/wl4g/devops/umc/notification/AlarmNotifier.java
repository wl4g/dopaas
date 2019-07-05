package com.wl4g.devops.umc.notification;

import java.util.List;
import java.util.Map;

/**
 * Alarm notifier
 * 
 * @author wangl.sir
 * @version v1.0 2019年7月5日
 * @since
 */
public interface AlarmNotifier {

	/**
	 * Send simple notify message.
	 * 
	 * @param targets
	 * @param message
	 */
	void simpleNotify(List<String> targets, String message);

	/**
	 * Send template notify message.
	 * 
	 * @param targets
	 * @param templateCode
	 * @param message
	 */
	void templateNotify(List<String> targets, String templateCode, Map<String, Object> message);

}

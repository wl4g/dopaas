package com.wl4g.devops.umc.notification.email;

import com.wl4g.devops.umc.notification.AbstractAlarmNotifier;
import com.wl4g.devops.umc.notification.AlarmType;

/**
 * @author vjay
 * @date 2019-06-10 15:10:00
 */
public class EmailNotifier extends AbstractAlarmNotifier {

	@Override
	public AlarmType alarmType() {
		return AlarmType.EMAIL;
	}

	@Override
	public void simpleNotify(SimpleAlarmMessage message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void templateNotify(TeampleAlarmMessage message) {
		// TODO Auto-generated method stub

	}

}

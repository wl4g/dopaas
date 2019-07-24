package com.wl4g.devops.umc.notification.sms;

import com.wl4g.devops.umc.notification.AbstractAlarmNotifier;
import com.wl4g.devops.umc.notification.AlarmType;

/**
 * @author vjay
 * @date 2019-06-10 15:10:00
 */
public class SmsNotifier extends AbstractAlarmNotifier {

	@Override
	public AlarmType alarmType() {
		return AlarmType.SMS;
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

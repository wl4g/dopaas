package com.wl4g.devops.umc.notification;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

/**
 * Alarm notifier adapter.
 * 
 * @author Wangl.sir
 * @version v1.0 2019年7月24日
 * @since
 */
public class CompositeAlarmNotifierAdapter extends AbstractAlarmNotifier {

	/**
	 * Alarm notifiers.
	 */
	final protected Map<AlarmType, AlarmNotifier> alarmNotifiers = new LinkedHashMap<>();

	public CompositeAlarmNotifierAdapter(List<AlarmNotifier> notifiers) {
		Assert.state(!isEmpty(notifiers), "Alarm Notifier has at least one.");
		notifiers.forEach(notifier -> alarmNotifiers.put(notifier.alarmType(), notifier));
	}

	@Override
	public void simpleNotify(SimpleAlarmMessage message) {
		determineAlarmNotifiers(message.getAlarmType()).forEach(notifier -> notifier.simpleNotify(message));
	}

	@Override
	public void templateNotify(TeampleAlarmMessage message) {
		determineAlarmNotifiers(message.getAlarmType()).forEach(notifier -> notifier.templateNotify(message));
	}

	@Override
	public AlarmType alarmType() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Determine alarm notifiers.
	 * 
	 * @param alarmType
	 * @return
	 */
	protected List<AlarmNotifier> determineAlarmNotifiers(String alarmType) {
		if (isBlank(alarmType)) {
			log.warn("Unsupported this alarm type: {}", alarmType);
			return Collections.emptyList();
		}

		List<AlarmNotifier> notifiers = new ArrayList<>(4);
		for (String part : alarmType.split(",")) {
			AlarmType type = AlarmType.safeOf(part);
			if (null != type) {
				AlarmNotifier notifier = alarmNotifiers.get(type);
				if (notifier != null) {
					notifiers.add(notifier);
				}
			}
		}
		return notifiers;
	}

}

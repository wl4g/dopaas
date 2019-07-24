package com.wl4g.devops.umc.notification.bark;

import com.wl4g.devops.umc.notification.AbstractAlarmNotifier;
import com.wl4g.devops.umc.notification.AlarmType;

import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * A Alert Tool For IOS,Just For Test
 * 
 * @author vjay
 * @date 2019-06-10 15:10:00
 */
public class BarkNotifier extends AbstractAlarmNotifier {

	@Override
	public AlarmType alarmType() {
		return AlarmType.BARK;
	}

	@Override
	public void simpleNotify(SimpleAlarmMessage message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void templateNotify(TeampleAlarmMessage message) {
		// TODO Auto-generated method stub

	}

	/**
	 * Just for Test
	 */
	@SuppressWarnings("unused")
	private void test(String msg) {
		Netty4ClientHttpRequestFactory factory = new Netty4ClientHttpRequestFactory();
		RestTemplate restTemplate = new RestTemplate(factory);
		String url = "http://vjay.pw:8088/xw6iqTPfdY2BuJYzueRSza/" + msg + "-time:" + System.currentTimeMillis();
		String result = restTemplate.getForObject(url, String.class);
		log.info(result);
	}

}

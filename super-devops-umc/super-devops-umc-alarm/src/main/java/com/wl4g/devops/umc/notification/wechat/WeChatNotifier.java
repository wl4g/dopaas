package com.wl4g.devops.umc.notification.wechat;

import com.wl4g.devops.umc.notification.AbstractAlarmNotifier;
import com.wl4g.devops.umc.notification.AlarmType;

import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author vjay
 * @date 2019-06-10 15:10:00
 */
public class WeChatNotifier extends AbstractAlarmNotifier {

	@Override
	public AlarmType alarmType() {
		return AlarmType.WECHAT;
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
		String url = "http://sc.ftqq.com/SCU37589T84d5e38c6aac8d8071f97652967308715c120c0205aec.send?text=主人服务器又挂掉啦:" + msg
				+ " time:" + System.currentTimeMillis();
		String result = restTemplate.getForObject(url, String.class);
		log.info(result);
	}

}

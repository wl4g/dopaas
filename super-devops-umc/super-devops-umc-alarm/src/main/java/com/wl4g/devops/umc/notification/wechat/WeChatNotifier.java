package com.wl4g.devops.umc.notification.wechat;

import com.wl4g.devops.umc.notification.AbstractAlarmNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author vjay
 * @date 2019-06-10 15:10:00
 */
public class WeChatNotifier extends AbstractAlarmNotifier {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void simpleNotify(List<String> targets, String message) {
		// send msg
		log.info("send msg:" + message);
		test(message);

	}

	/**
	 * Just for Test
	 */
	private void test(String msg) {
		Netty4ClientHttpRequestFactory factory = new Netty4ClientHttpRequestFactory();
		RestTemplate restTemplate = new RestTemplate(factory);
		String url = "http://sc.ftqq.com/SCU37589T84d5e38c6aac8d8071f97652967308715c120c0205aec.send?text=主人服务器又挂掉啦:" + msg
				+ " time:" + System.currentTimeMillis();
		String result = restTemplate.getForObject(url, String.class);
		log.info(result);
	}

}

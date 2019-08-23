/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
		Netty4ClientHttpRequestFactory factory = new Netty4ClientHttpRequestFactory();
		//factory.setConnectTimeout(5000);
		RestTemplate restTemplate = new RestTemplate(factory);
		//TemplateContactWrapper templateContactWrapper = JacksonUtils.parseJSON(message.getMessage(), TemplateContactWrapper.class);
		//String msg = templateContactWrapper.getAlarmTemplate().getMetric()+ " is up ";

		String url = "http://api.day.app/JZ7L5LcHcZsPxVrsox5AzG/{msg}" + "-time:" + System.currentTimeMillis();
		String result = restTemplate.getForObject(url, String.class,message.getMessage());

		log.info(result);
		log.info("fasong");

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
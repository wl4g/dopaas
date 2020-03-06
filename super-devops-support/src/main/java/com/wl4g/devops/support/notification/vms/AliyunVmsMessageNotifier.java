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
package com.wl4g.devops.support.notification.vms;

import static com.wl4g.devops.tool.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.devops.tool.common.serialize.JacksonUtils.toJSONString;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Properties;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.wl4g.devops.support.notification.AbstractMessageNotifier;
import com.wl4g.devops.support.notification.NotificationException;

public class AliyunVmsMessageNotifier extends AbstractMessageNotifier<VmsNotifyProperties, AliyunVmsMessage> {

	protected IAcsClient client;

	public AliyunVmsMessageNotifier(VmsNotifyProperties config) {
		super(config);
	}

	@Override
	public NotifierKind kind() {
		return NotifierKind.AliyunVms;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();

		DefaultProfile profile = DefaultProfile.getProfile(config.getAliyun().getRegionId(), config.getAliyun().getAccessKeyId(),
				config.getAliyun().getSecret());
		this.client = new DefaultAcsClient(profile);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void send(AliyunVmsMessage message) {
		CommonRequest request = new CommonRequest();
		// 请求方法分为POST和GET，建议您选择POST方式
		request.setSysMethod(MethodType.POST);
		// Domain参数的默认值为dyvmsapi.aliyuncs.com
		request.setSysDomain("dyvmsapi.aliyuncs.com");
		request.setSysVersion("2017-05-25");
		// SingleCallByTts|SingleCallByVoice
		// https://help.aliyun.com/document_detail/114036.html?spm=a2c4g.11186623.6.583.28265ad58befcz
		request.setSysAction(message.getAction().name());
		request.putQueryParameter("TtsCode", config.getAliyun().getTemplates().getProperty(message.getTemplateKey()));
		// 文本转语音（TTS）模板ID
		request.putQueryParameter("TtsCode", config.getAliyun().getTemplates().getProperty(message.getTemplateKey()));
		request.putQueryParameter("TtsParam", toJSONString(message.getParameters()));
		request.putQueryParameter("CalledShowNumber", message.getCalledShowNumber());
		request.putQueryParameter("CalledNumber", message.getCalledNumber());
		request.putQueryParameter("PlayTimes", valueOf(message.getPlayTimes()));
		request.putQueryParameter("Volume", valueOf(message.getVolume()));
		request.putQueryParameter("Speed", valueOf(message.getSpeed()));
		request.putQueryParameter("OutId", valueOf(message.getOutId()));

		try {
			log.debug("Aliyun vms request: {}", () -> toJSONString(request));
			CommonResponse response = client.getCommonResponse(request);
			if (!isNull(response) && !isBlank(response.getData())) {
				Properties body = parseJSON(response.getData(), Properties.class);
				if (!isNull(body) && "OK".equalsIgnoreCase((String) body.get("Code")))
					log.debug("Successed aliyun vms request: {}", toJSONString(request));
				else
					log.warn("Aliyun vms response: {}", response.getData());
			} else
				throw new NotificationException(kind(), format("Failed to vms request", toJSONString(request)));
		} catch (Exception e) {
			throw new NotificationException(kind(), e.getMessage(), e);
		}

	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public Object sendForReply(AliyunVmsMessage message) {
		throw new UnsupportedOperationException();
	}

}

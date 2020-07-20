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

import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.toJSONString;
import static java.lang.String.format;
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
import com.wl4g.devops.support.notification.GenericNotifyMessage;
import com.wl4g.devops.support.notification.NotificationException;

public class AliyunVmsMessageNotifier extends AbstractMessageNotifier<VmsNotifyProperties> {

	protected IAcsClient acsClient;

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
				config.getAliyun().getAccessKeySecret());
		this.acsClient = new DefaultAcsClient(profile);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void send(GenericNotifyMessage msg) {
		CommonRequest req = new CommonRequest();
		// 请求方法分为POST和GET，建议您选择POST方式
		req.setSysMethod(MethodType.POST);
		// Domain参数的默认值为dyvmsapi.aliyuncs.com
		req.setSysDomain("dyvmsapi.aliyuncs.com");
		req.setSysVersion("2017-05-25");
		// SingleCallByTts|SingleCallByVoice
		req.setSysAction(msg.getParameterAsString(KEY_VMS_ACTION, "SingleCallByTts"));
		// VoiceCode和TtsCode二选一即可，前者用于自定义语音文件通知，后者用于标准语音通知，
		// action都为SingleCallByTts，参考文档：
		// https://help.aliyun.com/document_detail/114036.html?spm=a2c4g.11186623.6.579.7bc95f33wpPjWM
		// https://help.aliyun.com/document_detail/114035.html?spm=a2c4g.11186623.6.581.56295ad5EBbcwv#
		String ttsCodeOrVoiceCode = config.getAliyun().getTemplates().getProperty(msg.getTemplateKey());
		// 自定义语音文件ID
		req.putQueryParameter("VoiceCode", ttsCodeOrVoiceCode);
		// 标准文本转语音模板ID
		req.putQueryParameter("TtsCode", ttsCodeOrVoiceCode);
		req.putQueryParameter("TtsParam", toJSONString(msg.getParameters()));
		req.putQueryParameter("CalledShowNumber", config.getAliyun().getCalledShowNumber());
		String calledNumber = msg.getToObjects().get(0);
		req.putQueryParameter("CalledNumber", calledNumber);
		req.putQueryParameter("PlayTimes", msg.getParameterAsString(KEY_VMS_PLAYTIMES, "2"));
		req.putQueryParameter("Volume", msg.getParameterAsString(KEY_VMS_VOLUME, "100"));
		req.putQueryParameter("Speed", msg.getParameterAsString(KEY_VMS_SPEED, "100"));
		req.putQueryParameter("OutId", msg.getCallbackId());

		try {
			log.debug("Aliyun vms request: {}", () -> toJSONString(req));
			CommonResponse resp = acsClient.getCommonResponse(req);
			if (!isNull(resp) && !isBlank(resp.getData())) {
				Properties body = parseJSON(resp.getData(), Properties.class);
				if (!isNull(body) && "OK".equalsIgnoreCase((String) body.get("Code"))) {
					if (log.isDebugEnabled())
						log.debug("Successed response: {}, request: {}", resp.getData(), toJSONString(req));
					else
						log.info("Successed calledNumer: {}, message: {}", calledNumber, msg.getParameters());
				} else
					log.warn("Failed response: {}, request: {}", resp.getData(), toJSONString(req));
			} else
				throw new NotificationException(kind(), format("Failed to vms request", toJSONString(req)));
		} catch (Exception e) {
			throw new NotificationException(kind(), e.getMessage(), e);
		}

	}

	@Override
	public <R> R sendForReply(GenericNotifyMessage message) {
		throw new UnsupportedOperationException();
	}

	/**
	 * SingleCallByVoice|SingleCallByTts
	 * 
	 * @see https://help.aliyun.com/document_detail/114035.html?spm=a2c4g.11186623.6.581.56295ad5EBbcwv#
	 * @see https://help.aliyun.com/document_detail/114036.html?spm=a2c4g.11186623.6.579.7bc95f33wpPjWM
	 */
	final public static String KEY_VMS_ACTION = "VmsAction";

	/**
	 * 语音文件的播放次数，取值范围为1~3。默认：2
	 */
	final public static String KEY_VMS_PLAYTIMES = "VmsPlayTimes";

	/**
	 * 语音文件播放的音量。取值范围为0~100，默认为100。
	 */
	final public static String KEY_VMS_VOLUME = "VmsVolume";

	/**
	 * 语速控制，取值范围：-500~500。 默认：100
	 */
	final public static String KEY_VMS_SPEED = "VmsSpeed";
}
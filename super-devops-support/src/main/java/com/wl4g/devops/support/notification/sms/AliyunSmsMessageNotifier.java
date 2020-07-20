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
package com.wl4g.devops.support.notification.sms;

import static com.wl4g.devops.components.tools.common.lang.Assert2.isTrue;
import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.toJSONString;
import static java.util.Objects.isNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.wl4g.devops.support.notification.AbstractMessageNotifier;
import com.wl4g.devops.support.notification.GenericNotifyMessage;
import com.wl4g.devops.support.notification.NotificationException;
import com.wl4g.devops.support.notification.sms.SmsNotifyProperties;
import com.wl4g.devops.support.notification.sms.SmsNotifyProperties.AliyunSmsNotifyProperties;

public class AliyunSmsMessageNotifier extends AbstractMessageNotifier<SmsNotifyProperties> implements InitializingBean {

	private IAcsClient acsClient;

	public AliyunSmsMessageNotifier(SmsNotifyProperties config) {
		super(config);
	}

	@Override
	public NotifierKind kind() {
		return NotifierKind.AliyunSms;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();

		AliyunSmsNotifyProperties aliyun = config.getAliyun();
		// 设置超时时间-可自行调整
		System.setProperty("sun.net.client.defaultConnectTimeout", aliyun.getDefaultConnectTimeout());
		System.setProperty("sun.net.client.defaultReadTimeout", aliyun.getDefaultReadTimeout());

		// 初始化ascClient,暂时不支持多region（请勿修改）
		IClientProfile profile = DefaultProfile.getProfile(aliyun.getRegionId(), aliyun.getAccessKeyId(),
				aliyun.getAccessKeySecret());
		acsClient = new DefaultAcsClient(profile);
	}

	@Override
	public void send(GenericNotifyMessage msg) {
		try {
			isTrue(msg.getToObjects().size() < 1000, "Group numbers exceeds the limit (<1000)");

			SendSmsRequest req = new SendSmsRequest();
			req.setSysMethod(MethodType.POST);
			// 必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
			req.setPhoneNumbers(StringUtils.join(msg.getToObjects(), ','));
			// 必填:短信签名-可在短信控制台中找到
			req.setSignName(config.getAliyun().getSignName());
			// 必填:短信模板-可在短信控制台中找到
			req.setTemplateCode(config.getAliyun().getTemplates().getProperty(msg.getTemplateKey()));
			// 可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
			// 友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
			req.setTemplateParam(toJSONString(msg.getParameters()));
			// 可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
			// request.setSmsUpExtendCode("90997");
			// 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
			req.setOutId(msg.getCallbackId());
			// 请求失败这里会抛ClientException异常
			SendSmsResponse resp = acsClient.getAcsResponse(req);
			if (!isNull(resp) && "OK".equalsIgnoreCase(resp.getCode())) {
				if (log.isDebugEnabled())
					log.debug("Successed response: {}, request: {}", toJSONString(resp), toJSONString(req));
				else
					log.info("Successed message: {}, numbers: {}", resp.getMessage(), msg.getToObjects());
			} else
				log.warn("Failed response: {}, request: {}", toJSONString(resp), toJSONString(req));
		} catch (Exception e) {
			throw new NotificationException(kind(), e.getMessage(), e);
		}

	}

	@Override
	public <R> R sendForReply(GenericNotifyMessage message) {
		throw new UnsupportedOperationException();
	}

}
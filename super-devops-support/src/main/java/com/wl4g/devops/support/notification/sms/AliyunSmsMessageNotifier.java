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

import static com.wl4g.devops.tool.common.lang.Exceptions.getRootCausesString;
import static com.wl4g.devops.tool.common.serialize.JacksonUtils.toJSONString;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.wl4g.devops.support.notification.AbstractMessageNotifier;
import com.wl4g.devops.support.notification.sms.SmsNotifyProperties;
import com.wl4g.devops.support.notification.sms.SmsNotifyProperties.AliyunSmsNotifyProperties;

public class AliyunSmsMessageNotifier extends AbstractMessageNotifier<SmsNotifyProperties, AliyunSmsMessage>
		implements InitializingBean {

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
		try {
			AliyunSmsNotifyProperties aliConfig = config.getAliyun();
			// 设置超时时间-可自行调整
			System.setProperty("sun.net.client.defaultConnectTimeout", aliConfig.getDefaultConnectTimeout());
			System.setProperty("sun.net.client.defaultReadTimeout", aliConfig.getDefaultReadTimeout());

			// 初始化ascClient,暂时不支持多region（请勿修改）
			IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", aliConfig.getAccessKeyId(),
					aliConfig.getAccessKeySecret());
			DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", aliConfig.getProduct(), aliConfig.getDomain());

			acsClient = new DefaultAcsClient(profile);
		} catch (ClientException e) {
			log.error("Failed to initialize aliyun-sms.", e);
		}
	}

	@Override
	public void send(AliyunSmsMessage message) {
		SendSmsResponse resp = null;
		try {
			if (message.getNumbers().size() > 999) {
				throw new RuntimeException("SMS发送号码数超限(<1000).");
			}
			AliyunSmsNotifyProperties aliConfig = config.getAliyun();

			// 组装请求对象
			SendSmsRequest req = new SendSmsRequest();
			// 使用post提交
			req.setSysMethod(MethodType.POST);
			// 必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
			req.setPhoneNumbers(StringUtils.join(message.getNumbers(), ','));
			// 必填:短信签名-可在短信控制台中找到
			req.setSignName(aliConfig.getSignName());
			// 必填:短信模板-可在短信控制台中找到
			req.setTemplateCode(aliConfig.getTemplateCode());
			// 可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
			// 友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
			req.setTemplateParam(toJSONString(message.getParameters()));
			// 可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
			// request.setSmsUpExtendCode("90997");
			// 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
			req.setOutId("echoId-default");
			// 请求失败这里会抛ClientException异常
			resp = this.acsClient.getAcsResponse(req);
			if (resp.getCode() != null && resp.getCode().equals("OK")) {
				log.info("Send of aliyun sms message for: {}", toJSONString(req));
			} else {
				log.error("Failed to aliyun sms! req.templateCode: {}, resp.bizId: {}, resp.msg: {}", req.getTemplateCode(),
						resp.getBizId(), resp.getMessage());
			}
		} catch (Exception e) {
			log.error("Failed to sent aliyun-sms message. {}", getRootCausesString(e));
		}

	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public Object sendForReply(AliyunSmsMessage message) {
		throw new UnsupportedOperationException();
	}

}

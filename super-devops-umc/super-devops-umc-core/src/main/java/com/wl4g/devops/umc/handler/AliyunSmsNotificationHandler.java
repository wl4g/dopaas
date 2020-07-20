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
package com.wl4g.devops.umc.handler;
///*
// * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.wl4g.devops.umc.handle;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.exception.ExceptionUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import com.aliyuncs.DefaultAcsClient;
//import com.aliyuncs.IAcsClient;
//import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
//import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
//import com.aliyuncs.exceptions.ClientException;
//import com.aliyuncs.http.MethodType;
//import com.aliyuncs.profile.DefaultProfile;
//import com.aliyuncs.profile.IClientProfile;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
///**
// * Aliyun SMS handle utils.<br/>
// *
// * 模板内容：您好！应用监控中心系统通知：${appName} status changed from ${fStatus} to ${toStatus}
// * details: ${msg}，敬请处理。
// * 申请说明：用于系统健康通知，其中${appName}表示应用名，如：node1，${fStatus}为原状态，如：UP；${toStatus}为变化状态，如：DOWN，${msg}为消息，如：请登陆查看
// *
// * @author Wangl.sir <983708408@qq.com>
// * @version v1.0
// * @date 2018年5月28日
// * @since
// */
//@Component
//public class AliyunSmsNotificationHandle implements SmsNotificationHandle, InitializingBean {
//	final private Logger log = LoggerFactory.getLogger(getClass());
//	private ObjectMapper mapper = new ObjectMapper();
//
//	@Value("${sms.aliyun.product}")
//	private String product;
//	@Value("${sms.aliyun.domain}")
//	private String domain;
//	@Value("${sms.aliyun.accessKeyId}")
//	private String accessKeyId;
//	@Value("${sms.aliyun.accessKeySecret}")
//	private String accessKeySecret;
//	@Value("${sms.aliyun.signName}")
//	private String signName;
//	@Value("${sms.aliyun.templateCode}")
//	private String templateCode;
//	@Value("${sms.aliyun.defaultConnectTimeout}")
//	private String defaultConnectTimeout;
//	@Value("${sms.aliyun.defaultReadTimeout}")
//	private String defaultReadTimeout;
//
//	private IAcsClient acsClient;
//
//	@Override
//	public void afterPropertiesSet() throws Exception {
//		try {
//			// 设置超时时间-可自行调整
//			System.setProperty("sun.net.client.defaultConnectTimeout", defaultConnectTimeout);
//			System.setProperty("sun.net.client.defaultReadTimeout", defaultReadTimeout);
//
//			// 初始化ascClient,暂时不支持多region（请勿修改）
//			IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
//			DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
//
//			this.acsClient = new DefaultAcsClient(profile);
//		} catch (ClientException e) {
//			log.error("Aliyun SMS初始化失败.", e);
//		}
//	}
//
//	@Override
//	public boolean send(List<String> numbers, String appInfo, String fromStatus, String toStatus, String content) {
//		SendSmsResponse resp = null;
//		try {
//			if (numbers.size() > 999)
//				throw new RuntimeException("SMS发送号码数超限(<1000).");
//
//			// 组装请求对象
//			SendSmsRequest req = new SendSmsRequest();
//			// 使用post提交
//			req.setMethod(MethodType.POST);
//			// 必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
//			req.setPhoneNumbers(StringUtils.join(numbers, ','));
//			// 必填:短信签名-可在短信控制台中找到
//			req.setSignName(signName);
//			// 必填:短信模板-可在短信控制台中找到
//			req.setTemplateCode(templateCode);
//
//			// 可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
//			// 友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
//			Map<String, String> param = new HashMap<>();
//			param.put("appInfo", appInfo);
//			param.put("fstatus", fromStatus);
//			param.put("tstatus", toStatus);
//			param.put("msg", content);
//			req.setTemplateParam(this.mapper.writeValueAsString(param));
//			// 可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
//			// request.setSmsUpExtendCode("90997");
//			// 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
//			req.setOutId("echoId-default");
//			// 请求失败这里会抛ClientException异常
//			resp = this.acsClient.getAcsResponse(req);
//			if (resp.getCode() != null && resp.getCode().equals("OK")) {
//				return true;
//			} else {
//				log.error("AliyunSMS发送失败. req.templateCode: {}, req.content: {}, resp.bizId: {}, resp.msg: {}",
//						req.getTemplateCode(), content, resp.getBizId(), resp.getMessage());
//			}
//		} catch (Exception e) {
//			log.error("AliyunSMS发送异常. {}", ExceptionUtils.getRootCauseMessage(e));
//		}
//		return false;
//	}
//
//}
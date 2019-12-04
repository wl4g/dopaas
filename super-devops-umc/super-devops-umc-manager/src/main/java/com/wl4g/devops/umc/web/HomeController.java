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
package com.wl4g.devops.umc.web;

import com.wl4g.devops.common.constants.UMCDevOpsConstants;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.support.notification.mail.MailSenderTemplate;
import com.wl4g.devops.tool.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.umc.handle.DashboardHandle;
import com.wl4g.devops.umc.handle.SmsNotificationHandle;
import com.wl4g.devops.umc.model.StatusMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.Date;

@Controller
@RequestMapping(UMCDevOpsConstants.URI_ADMIN_HOME)
public class HomeController extends BaseController {

	@Autowired
	private DashboardHandle dashboardService;
	@Autowired
	private MailSenderTemplate mailHandle;
	@Autowired
	private SmsNotificationHandle smsHandle;

	@RequestMapping("{msgId}")
	public Object details(@PathVariable("msgId") String msgId, Model model) {
		if (log.isInfoEnabled()) {
			log.info("Get state details. msgId={}", msgId);
		}
		StatusMessage info = this.dashboardService.findStatusInfo(msgId);
		model.addAttribute("info", info);
		if (log.isInfoEnabled()) {
			log.info("State details={}", JacksonUtils.toJSONString(info));
		}
		return "details";
	}

	// @RequestMapping("smsSend")
	public String smsSendTest() {
		this.smsHandle.send(Arrays.asList(new String[] { "18127968606" }), "transport-404e4c6d", "UP", "DOWN",
				"120.79.3.227:64/s/1");
		return "ok";
	}

	// @RequestMapping("mailSend")
	public String mailSendTest() {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setFrom("XXX"); // 设置显示的账号名(最终发送格式为: from显示名<from账号名>)
		msg.setSubject("测试主题");
		msg.setTo("983708408@qq.com");
		msg.setText("http://127.0.0.1:64/s/1245");
		msg.setSentDate(new Date());
		this.mailHandle.send(msg);
		System.out.println("ok..");
		return "ok";
	}

}
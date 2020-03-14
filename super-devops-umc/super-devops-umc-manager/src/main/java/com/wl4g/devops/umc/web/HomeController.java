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
import com.wl4g.devops.common.framework.operator.GenericOperatorAdapter;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.support.notification.GenericNotifyMessage;
import com.wl4g.devops.support.notification.MessageNotifier;
import com.wl4g.devops.support.notification.MessageNotifier.NotifierKind;
import com.wl4g.devops.support.notification.mail.MailMessageNotifier;
import com.wl4g.devops.tool.common.serialize.JacksonUtils;
import com.wl4g.devops.umc.handle.DashboardHandle;
import com.wl4g.devops.umc.model.StatusMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(UMCDevOpsConstants.URI_ADMIN_HOME)
public class HomeController extends BaseController {

	@Autowired
	private DashboardHandle dashboardService;

	@Autowired
	private GenericOperatorAdapter<NotifierKind, MessageNotifier> notifierAdapter;

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
		// this.smsHandle.send(Arrays.asList(new String[] { "18127968606" }),
		// "transport-404e4c6d", "UP", "DOWN",
		// "120.79.3.227:64/s/1");
		return "ok";
	}

	@RequestMapping("mailSend")
	public String mailSendTest() {
		GenericNotifyMessage msg = new GenericNotifyMessage("1154635107@qq.com", "mailTpl1")
				// .addParameter(MailMessageNotifier.KEY_MAIL_MSGTYPE, "simple")
				.addParameter(MailMessageNotifier.KEY_MAIL_SUBJECT, "测试消息").addParameter("appName", "bizService1")
				.addParameter("status", "DOWN").addParameter("cause", "Host.cpu.utilization > 200%");
		notifierAdapter.forOperator(MailMessageNotifier.class).send(msg);
		System.out.println("ok..");
		return "ok";
	}

}
/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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

import com.wl4g.components.common.serialize.JacksonUtils;
import com.wl4g.components.core.constants.UMCDevOpsConstants;
import com.wl4g.components.core.framework.operator.GenericOperatorAdapter;
import com.wl4g.components.core.web.BaseController;
import com.wl4g.components.support.notification.GenericNotifyMessage;
import com.wl4g.components.support.notification.MessageNotifier;
import com.wl4g.components.support.notification.MessageNotifier.NotifierKind;
import com.wl4g.components.support.notification.mail.MailMessageNotifier;
import com.wl4g.devops.umc.handler.DashboardHandler;
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
	private DashboardHandler dashboardService;

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
		// TODO
		GenericNotifyMessage msg = new GenericNotifyMessage("1154635107@qq.com", "notifyTpl1");
		// Common parameters.
		msg.addParameter("appName", "bizService1");
		msg.addParameter("status", "DOWN");
		msg.addParameter("cause", "Host.cpu.utilization > 200%");
		// Mail special parameters.
		msg.addParameter(MailMessageNotifier.KEY_MAILMSG_SUBJECT, "测试消息");
		// msg.addParameter(MailMessageNotifier.KEY_MAILMSG_CC, "");
		// msg.addParameter(MailMessageNotifier.KEY_MAILMSG_BCC, "");
		// msg.addParameter(MailMessageNotifier.KEY_MAILMSG_REPLYTO,
		// "");
		notifierAdapter.forOperator(MailMessageNotifier.class).send(msg);

		System.out.println("ok..");
		return "ok";
	}

}
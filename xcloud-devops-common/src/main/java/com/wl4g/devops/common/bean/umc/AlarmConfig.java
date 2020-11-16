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
package com.wl4g.devops.common.bean.umc;

import com.wl4g.components.core.bean.BaseBean;
import com.wl4g.iam.common.bean.Contact;

import java.io.Serializable;
import java.util.List;

public class AlarmConfig extends BaseBean implements Serializable {

	private static final long serialVersionUID = 381411777614066880L;

	private Long collectId;

	private Long templateId;

	private Long contactGroupId;

	private String callbackUrl;

	/* other */
	private String templateName;

	private String contactGroupName;

	private Long group;
	private String envType;
	private String classify;

	/* template */
	private AlarmTemplate alarmTemplate;

	/* contacts */
	private List<Contact> contacts;

	public Long getCollectId() {
		return collectId;
	}

	public void setCollectId(Long collectId) {
		this.collectId = collectId;
	}

	public Long getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	public Long getContactGroupId() {
		return contactGroupId;
	}

	public void setContactGroupId(Long contactGroupId) {
		this.contactGroupId = contactGroupId;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getContactGroupName() {
		return contactGroupName;
	}

	public void setContactGroupName(String contactGroupName) {
		this.contactGroupName = contactGroupName;
	}

	public Long getGroup() {
		return group;
	}

	public void setGroup(Long group) {
		this.group = group;
	}

	public String getEnvType() {
		return envType;
	}

	public void setEnvType(String envType) {
		this.envType = envType;
	}

	public String getClassify() {
		return classify;
	}

	public void setClassify(String classify) {
		this.classify = classify;
	}

	public AlarmTemplate getAlarmTemplate() {
		return alarmTemplate;
	}

	public void setAlarmTemplate(AlarmTemplate alarmTemplate) {
		this.alarmTemplate = alarmTemplate;
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}
}
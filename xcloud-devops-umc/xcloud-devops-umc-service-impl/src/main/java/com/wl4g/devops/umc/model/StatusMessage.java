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
package com.wl4g.devops.umc.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wl4g.component.common.serialize.JacksonUtils;

import de.codecentric.boot.admin.server.domain.values.StatusInfo;

/**
 * Status message.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年6月7日
 * @since
 */
public class StatusMessage {

	private String appInfo = "";
	private String healthUrl = "";
	private String fromStatus = "";
	private String toStatus = "";
	private long timestamp = 0L;
	private String detailsUrl = "";
	private String msgId = "";
	private List<String> phoneTo = new ArrayList<>();
	private List<String> mailTo = new ArrayList<>();
	private Map<String, Object> details = new HashMap<>();

	public StatusMessage() {
		super();
	}

	public StatusMessage(String appInfo, String healthUrl, String fromStatus, String toStatus, long timestamp,
			List<String> mailTo, List<String> phoneTo, String detailsUrl, String msgId) {
		super();
		this.setAppInfo(appInfo);
		this.setHealthUrl(healthUrl);
		this.setFromStatus(fromStatus);
		this.setToStatus(toStatus);
		this.setTimestamp(timestamp);
		this.setMailTo(mailTo);
		this.setPhoneTo(phoneTo);
		this.setDetailsUrl(detailsUrl);
		this.setMsgId(msgId);
	}

	public String getAppInfo() {
		return appInfo;
	}

	public void setAppInfo(String appInfo) {
		if (appInfo != null) {
			this.appInfo = appInfo;
		}
	}

	public String getHealthUrl() {
		return healthUrl;
	}

	public void setHealthUrl(String healthUrl) {
		if (healthUrl != null) {
			this.healthUrl = healthUrl;
		}
	}

	public String getFromStatus() {
		return fromStatus;
	}

	public void setFromStatus(String fromStatus) {
		if (fromStatus != null) {
			this.fromStatus = fromStatus;
		}
	}

	public String getToStatus() {
		return toStatus;
	}

	public void setToStatus(String toStatus) {
		if (toStatus != null) {
			this.toStatus = toStatus;
		}
	}

	public long getTimestamp() {
		return timestamp;
	}

	@JsonIgnore
	public String getFormatTimestamp() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(this.timestamp);
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getDetailsUrl() {
		return detailsUrl;
	}

	public void setDetailsUrl(String detailsUrl) {
		if (detailsUrl != null) {
			this.detailsUrl = detailsUrl;
		}
	}

	public List<String> getMailTo() {
		return mailTo;
	}

	public void setMailTo(List<String> mailTo) {
		if (mailTo != null) {
			this.mailTo = mailTo;
		}
	}

	public List<String> getPhoneTo() {
		return phoneTo;
	}

	public void setPhoneTo(List<String> phoneTo) {
		this.phoneTo = phoneTo;
	}

	@JsonIgnore
	public String getFormatTos() {
		List<String> tos = new ArrayList<>();
		tos.addAll(this.getMailTo());
		tos.addAll(this.getPhoneTo());
		return StringUtils.join(tos, ",");
	}

	public Map<String, Object> getDetails() {
		return details;
	}

	public void setDetails(Map<String, Object> details) {
		if (details != null) {
			this.details = details;
		}
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	@Override
	public String toString() {
		return JacksonUtils.toJSONString(this);
	}

	public String toHtmlString() throws Exception {
		return this.toHtmlString(this.getDetails(), new StringBuffer());
	}

	@SuppressWarnings("unchecked")
	private String toHtmlString(Map<String, Object> details, StringBuffer html) throws Exception {
		html.append("<ul class='list-group'>");
		for (Entry<String, Object> ent : details.entrySet()) {
			String k = ent.getKey();
			Object v = ent.getValue();
			html.append("<li class='list-group-item'>");
			html.append(k.substring(0, 1).toUpperCase());
			html.append(k.substring(1));
			html.append("：");
			if (v instanceof Map) {
				this.toHtmlString((Map<String, Object>) v, html);
			} else {
				String val = String.valueOf(v);
				//
				// Get the $ref reference to the actual value.
				//
				// k EG: "$ref" val EG: "$.details.advancedMemory.status"
				// if (k.equalsIgnoreCase("$ref")) {
				// JsonNode node = new
				// ObjectMapper().readTree(JacksonUtils.toJSONString(getDetails()));
				// String[] arr = val.replace("$.", "").split("\\.");
				// for (int i = 0; i < arr.length; i++) {
				// node = node.findValue(arr[i]);
				// }
				// if (node.isObject()) {
				// // node.toString() EG: {"code": "UP","description": ""}
				// this.toHtmlString(JacksonUtils.parseJSON(node.toString(),
				// HashMap.class), html);
				// } else {
				// val = node.asText();
				// }
				// }
				// if (!val.startsWith("$.")) {
				html.append(val);
				// }
			}
			html.append("</li>");
		}
		html.append("</ul>");
		return html.toString();
	}

	public static StatusMessage wrap(String appInfo, String healthUrl, String fromStatus, String toStatus, long timestamp,
			List<String> mailTo, List<String> phoneTo, String detailsUrl, String msgId, StatusInfo info) {
		StatusMessage msg = new StatusMessage(appInfo, healthUrl, fromStatus, toStatus, timestamp, mailTo, phoneTo, detailsUrl,
				msgId);
		msg.getDetails().putAll(info.getDetails());
		return msg;
	}

}
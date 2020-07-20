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
package com.wl4g.devops.common.bean.doc;

import com.wl4g.devops.common.bean.BaseBean;

import java.util.List;

public class FileChanges extends BaseBean {
	private static final long serialVersionUID = -3425763977484915010L;

	private String name;

	private String docCode;

	private String type;

	private String action;

	private String lang;

	private String content;

	private String sha;

	private String description;

	private Integer isLatest;

	// --- Temporary ---

	private List<Label> labels;

	private List<Integer> labelIds;

	private String createByStr;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public String getDocCode() {
		return docCode;
	}

	public void setDocCode(String docCode) {
		this.docCode = docCode == null ? null : docCode.trim();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type == null ? null : type.trim();
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action == null ? null : action.trim();
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang == null ? null : lang.trim();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content == null ? null : content.trim();
	}

	public String getSha() {
		return sha;
	}

	public void setSha(String sha) {
		this.sha = sha == null ? null : sha.trim();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description == null ? null : description.trim();
	}

	public Integer getIsLatest() {
		return isLatest;
	}

	public void setIsLatest(Integer isLatest) {
		this.isLatest = isLatest;
	}

	public List<Label> getLabels() {
		return labels;
	}

	public void setLabels(List<Label> labels) {
		this.labels = labels;
	}

	public List<Integer> getLabelIds() {
		return labelIds;
	}

	public void setLabelIds(List<Integer> labelIds) {
		this.labelIds = labelIds;
	}

	public String getCreateByStr() {
		return createByStr;
	}

	public void setCreateByStr(String createByStr) {
		this.createByStr = createByStr;
	}

}
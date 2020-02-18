package com.wl4g.devops.common.bean.doc;

import com.wl4g.devops.common.bean.BaseBean;

import java.util.List;

public class FileChanges extends BaseBean {
	private static final long serialVersionUID = -3425763977484915010L;

	private String name;

	private String fileCode;

	private String type;

	private String action;

	private String lang;

	private String content;

	private String sha;

	private String passwd;

	private String description;

	private Integer isLatest;

	private Integer shareType;

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

	public String getFileCode() {
		return fileCode;
	}

	public void setFileCode(String fileCode) {
		this.fileCode = fileCode == null ? null : fileCode.trim();
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

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
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

	public Integer getShareType() {
		return shareType;
	}

	public void setShareType(Integer shareType) {
		this.shareType = shareType;
	}
}
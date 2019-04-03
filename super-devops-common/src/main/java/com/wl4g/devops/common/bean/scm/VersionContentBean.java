package com.wl4g.devops.common.bean.scm;

public class VersionContentBean extends BaseBean {

	private Integer versionId; // 版本号ID
	private String filename; // 文件名称(不含后缀)
	private Integer type; // 文件类型
	private String content; // 配置文件内容

	public Integer getVersionId() {
		return versionId;
	}

	public void setVersionId(Integer versionId) {
		this.versionId = versionId;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public static enum FileType {

		YAML(1), PROP(2);

		private int value;

		private FileType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public static FileType of(int value) {
			for (FileType t : values()) {
				if (t.getValue() == value) {
					return t;
				}
			}

			throw new IllegalStateException(String.format(" 'value' : %s", String.valueOf(value)));
		}

	}

}

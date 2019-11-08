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
package com.wl4g.devops.common.bean.scm;

import com.wl4g.devops.common.bean.BaseBean;

public class VersionContentBean extends BaseBean {
	private static final long serialVersionUID = 7654969446638218612L;

	private Integer versionId; // 版本号ID
	private String namespaceId; // 命名空间ID（sys_dict表ID）
	private String namespace; // 命名空间（配置文件名）
	private Integer type; // 类型
	private String content; // 配置文件内容

	public Integer getVersionId() {
		return versionId;
	}

	public void setVersionId(Integer versionId) {
		this.versionId = versionId;
	}

	public String getNamespaceId() {
		return namespaceId;
	}

	public void setNamespaceId(String namespaceId) {
		this.namespaceId = namespaceId;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String filename) {
		this.namespace = filename;
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

		YML(1), PROP(2), YAML(3);

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
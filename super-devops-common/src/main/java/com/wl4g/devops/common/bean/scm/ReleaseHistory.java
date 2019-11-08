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

/**
 * 对应表：scm_release_history
 * 
 * @author zzh
 * @date 2018年9月26日
 */
public class ReleaseHistory extends BaseBean {
	private static final long serialVersionUID = 499669446638218612L;

	private int versionid; // 版本号ID
	private String status; // 发布状态（1:成功/2:失败）
	private Integer type; // （1:成功/2:失败）

	public enum type {

		RELEASE(1), ROLLBACK(2);

		private Integer value;

		public Integer getValue() {
			return value;
		}

		type(Integer value) {
			this.value = value;
		}

	}

	public int getVersionid() {
		return versionid;
	}

	public void setVersionid(int versionid) {
		this.versionid = versionid;
	}

	// public String getNamespaceid() {
	// return namespaceid;
	// }
	//
	// public void setNamespaceid(String namespaceid) {
	// this.namespaceid = namespaceid;
	// }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}
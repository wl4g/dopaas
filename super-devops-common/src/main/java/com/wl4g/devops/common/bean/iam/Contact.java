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
package com.wl4g.devops.common.bean.iam;

import com.wl4g.devops.common.bean.BaseBean;

import java.io.Serializable;
import java.util.List;

public class Contact extends BaseBean implements Serializable {
	private static final long serialVersionUID = 381411777614066880L;

	private String name;

	private Integer[] groups;

	private List<ContactChannel> contactChannels;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public Integer[] getGroups() {
		return groups;
	}

	public void setGroups(Integer[] groups) {
		this.groups = groups;
	}

	public List<ContactChannel> getContactChannels() {
		return contactChannels;
	}

	public void setContactChannels(List<ContactChannel> contactChannels) {
		this.contactChannels = contactChannels;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		Contact other = (Contact) obj;
		if (this.getId().equals(other.getId())) {
			return true;
		}
		return false;
	}

}
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
package com.wl4g.devops.common.bean.iam.model;

import com.wl4g.devops.common.bean.BaseBean;

/**
 * @author vjay
 * @date 2019-11-19 11:33:00
 */
public class GroupExt extends BaseBean {
	private static final long serialVersionUID = 6302283757380254809L;

	private Integer id;

	private Integer groupId;

	private String displayName;

	private String contact;

	private String contactPhone;

	private String address;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public static enum GroupType {

		Park(1),

		Company(2),

		Department(3);

		int value;

		GroupType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		/**
		 * Converter string to {@link Action}
		 *
		 * @param action
		 * @return
		 */
		public static GroupType of(Integer type) {
			GroupType wh = safeOf(type);
			if (wh == null) {
				throw new IllegalArgumentException(String.format("Illegal type '%s'", type));
			}
			return wh;
		}

		/**
		 * Safe converter string to {@link Action}
		 *
		 * @param action
		 * @return
		 */
		public static GroupType safeOf(Integer type) {
			if (type == null) {
				return null;
			}
			for (GroupType t : values()) {
				if (type.intValue() == t.getValue()) {
					return t;
				}
			}
			return null;
		}
	}

}
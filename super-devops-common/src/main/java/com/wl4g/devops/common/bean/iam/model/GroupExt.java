package com.wl4g.devops.common.bean.iam.model;

import com.wl4g.devops.common.bean.BaseBean;

import java.io.Serializable;

/**
 * @author vjay
 * @date 2019-11-19 11:33:00
 */
public class GroupExt extends BaseBean implements Serializable {

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

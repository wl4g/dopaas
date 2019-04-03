/*
 * Copyright 2015 the original author or authors.
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
package com.wl4g.devops.common.bean.scm.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;

public class ReportModel extends BaseModel {
	final private static long serialVersionUID = 2523769504519533902L;

	private ReleaseInstance instance = new ReleaseInstance();
	private List<RefreshedBeanDefine> details = new ArrayList<>();
	private ReportStatus status = ReportStatus.ok;
	private String description = "ok";

	public ReportModel() {
		super();
	}

	public ReleaseInstance getInstance() {
		return instance;
	}

	public void setInstance(ReleaseInstance instance) {
		this.instance = instance;
	}

	public List<RefreshedBeanDefine> getDetails() {
		return details;
	}

	public void setDetails(List<RefreshedBeanDefine> details) {
		this.details = details;
	}

	/**
	 * Get details JSONString
	 * 
	 * @return
	 */
	public String getDetailsJSONString() {
		return JacksonUtils.toJSONString(this.getDetails());
	}

	public ReportStatus getStatus() {
		return status;
	}

	public void setStatus(ReportStatus status) {
		if (status != null) {
			this.status = status;
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if (!StringUtils.isEmpty(description) && !"NULL".equalsIgnoreCase(description)) {
			this.description = description;
		}
	}

	@Override
	public void validation(boolean validVersion, boolean validReleaseId) {
		super.validation(validVersion, validReleaseId);
		Assert.notNull(getInstance(), "`instance` is not allowed to be null.");
		getInstance().validation();
	}

	public static class RefreshedBeanDefine {
		private String beanName;
		private String beanType;
		private List<RefreshedMemberDefine> members = new ArrayList<>();

		public RefreshedBeanDefine() {
			super();
		}

		public RefreshedBeanDefine(String beanName, String beanType, List<RefreshedMemberDefine> members) {
			super();
			this.beanName = beanName;
			this.beanType = beanType;
			this.members = members;
		}

		public String getBeanName() {
			return beanName;
		}

		public void setBeanName(String beanName) {
			this.beanName = beanName;
		}

		public String getBeanType() {
			return beanType;
		}

		public void setBeanType(String beanType) {
			this.beanType = beanType;
		}

		public List<RefreshedMemberDefine> getMembers() {
			return members;
		}

		public void setMembers(List<RefreshedMemberDefine> members) {
			this.members = members;
		}

	}

	public static class RefreshedMemberDefine {

		private String propertyName;
		private Object oldValue;
		private Object newValue;
		private Boolean modifyed;

		// Temporary member.
		@JsonIgnore
		private transient Field field;

		public RefreshedMemberDefine() {
			super();
		}

		public RefreshedMemberDefine(String propertyName, Object oldValue, Object newValue, Field field) {
			this(propertyName, oldValue, newValue, null, field);
		}

		public RefreshedMemberDefine(String propertyName, Object oldValue, Object newValue, Boolean modifyed, Field field) {
			super();
			this.setPropertyName(propertyName);
			this.setOldValue(oldValue);
			this.setNewValue(newValue);
			this.setModifyed(modifyed);
			this.setField(field);
		}

		public Field getField() {
			return field;
		}

		public void setField(Field field) {
			if (field != null) {
				this.field = field;
			}
		}

		public String getPropertyName() {
			return propertyName;
		}

		public void setPropertyName(String propertyName) {
			if (propertyName != null) {
				this.propertyName = propertyName;
			}
		}

		public Object getOldValue() {
			return oldValue;
		}

		public void setOldValue(Object oldValue) {
			if (oldValue != null) {
				this.oldValue = oldValue;
			}
		}

		public Object getNewValue() {
			return newValue;
		}

		public void setNewValue(Object newValue) {
			if (newValue != null) {
				this.newValue = newValue;
			}
		}

		public Boolean getModifyed() {
			return modifyed;
		}

		public void setModifyed(Boolean modifyed) {
			if (modifyed != null) {
				this.modifyed = modifyed;
			}
		}

	}

	@Override
	public String toString() {
		return JacksonUtils.toJSONString(this);
	}

	public static enum ReportStatus {
		ok(1), noChanged(0), fail(-1);

		private int value;

		private ReportStatus(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static ReportStatus of(int value) {
			for (ReportStatus t : values()) {
				if (t.getValue() == value) {
					return t;
				}
			}
			throw new IllegalStateException(String.format(" 'value' : %s", String.valueOf(value)));
		}

	}

}
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

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import com.wl4g.devops.common.utils.serialize.JacksonUtils;

public class ReportInfo extends GetRelease {
	final private static long serialVersionUID = 2523769504519533902L;

	@NotNull
	private RefreshStatus status = RefreshStatus.FAIL;

	@NotNull
	private List<RefreshInfo> details = new ArrayList<>();

	@NotBlank
	private String desc = "ok";

	public ReportInfo() {
		super();
	}

	public RefreshStatus getStatus() {
		return status;
	}

	public void setStatus(RefreshStatus status) {
		if (status != null) {
			this.status = status;
		}
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String description) {
		if (!StringUtils.isEmpty(description) && !"NULL".equalsIgnoreCase(description)) {
			this.desc = description;
		}
	}

	@Override
	public void validation(boolean validVersion, boolean validReleaseId) {
		super.validation(validVersion, validReleaseId);
		Assert.notNull(getInstance(), "`instance` is not allowed to be null.");
		getInstance().validation();
	}

	public static class RefreshInfo {

		private String property;
		private Object oldValue;
		private Object newValue;
		private Boolean modifyed;

		public RefreshInfo() {
			super();
		}

		public RefreshInfo(String propertyName, Object oldValue, Object newValue, Boolean modifyed) {
			super();
			this.setProperty(propertyName);
			this.setOldValue(oldValue);
			this.setNewValue(newValue);
			this.setModifyed(modifyed);
		}

		public String getProperty() {
			return property;
		}

		public void setProperty(String propertyName) {
			if (propertyName != null) {
				this.property = propertyName;
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

	public static enum RefreshStatus {
		CHANGED(1), NOCHANGED(0), FAIL(-1);

		private int value;

		private RefreshStatus(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static RefreshStatus of(int value) {
			for (RefreshStatus t : values()) {
				if (t.getValue() == value) {
					return t;
				}
			}
			throw new IllegalStateException(String.format(" 'value' : %s", String.valueOf(value)));
		}

	}

}
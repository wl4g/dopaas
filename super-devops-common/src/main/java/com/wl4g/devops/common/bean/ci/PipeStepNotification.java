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
package com.wl4g.devops.common.bean.ci;

import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.components.tools.common.collection.Collections2;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public class PipeStepNotification extends BaseBean {
	private static final long serialVersionUID = 6815608076300843748L;

	private Integer pipeId;

	private Integer enable;

	private Integer[] contactGroupId;

	private String contactGroupIds;

	public Integer getPipeId() {
		return pipeId;
	}

	public void setPipeId(Integer pipeId) {
		this.pipeId = pipeId;
	}

	public Integer getEnable() {
		return enable;
	}

	public void setEnable(Integer enable) {
		this.enable = enable;
	}

	public Integer[] getContactGroupId() {
		return contactGroupId;
	}

	public void setContactGroupId(Integer[] contactGroupId) {
		this.contactGroupId = contactGroupId;
	}

	public void setContactGroupId2(String[] contactGroupId) {
		if (!Collections2.isEmptyArray(contactGroupId)) {
			List<Integer> list = new ArrayList<>();
			for (int i = 0; i < contactGroupId.length; i++) {
				if (NumberUtils.isCreatable(contactGroupId[i])) {
					list.add(Integer.parseInt(contactGroupId[i]));
				}
			}
			Integer[] result = new Integer[list.size()];
			list.toArray(result);
			this.contactGroupId = result;
		}
	}

	public String getContactGroupIds() {
		return contactGroupIds;
	}

	public void setContactGroupIds(String contactGroupIds) {
		this.contactGroupIds = contactGroupIds;
	}
}
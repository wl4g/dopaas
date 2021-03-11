/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.devops.common.bean.uci;

import com.wl4g.component.core.bean.BaseBean;

import org.apache.commons.lang3.math.NumberUtils;

import static com.wl4g.component.common.collection.CollectionUtils2.isEmptyArray;

import java.util.ArrayList;
import java.util.List;

public class PipeStageNotification extends BaseBean {
	private static final long serialVersionUID = 6815608076300843748L;

	private Long pipeId;
	private Integer enable;
	private Long[] contactGroupId;
	private String contactGroupIds;

	public Long getPipeId() {
		return pipeId;
	}

	public void setPipeId(Long pipeId) {
		this.pipeId = pipeId;
	}

	public Integer getEnable() {
		return enable;
	}

	public void setEnable(Integer enable) {
		this.enable = enable;
	}

	public Long[] getContactGroupId() {
		return contactGroupId;
	}

	public void setContactGroupId(Long[] contactGroupId) {
		this.contactGroupId = contactGroupId;
	}

	public void setContactGroupId2(String[] contactGroupId) {
		if (!isEmptyArray(contactGroupId)) {
			List<Long> list = new ArrayList<>();
			for (int i = 0; i < contactGroupId.length; i++) {
				if (NumberUtils.isCreatable(contactGroupId[i])) {
					list.add(Long.parseLong(contactGroupId[i]));
				}
			}
			Long[] result = new Long[list.size()];
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
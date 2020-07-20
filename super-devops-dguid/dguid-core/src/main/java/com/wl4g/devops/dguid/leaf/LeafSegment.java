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
package com.wl4g.devops.dguid.leaf;

import java.util.Date;

/**
 * 分段ID实体
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年2月10日
 * @since
 */
public class LeafSegment {

	/**
	 * 最小id
	 */
	private Long minId;

	/**
	 * 最大id
	 */
	private Long maxId;

	/**
	 * 步长
	 */
	private Long step;

	/**
	 * 中间值(缓存阈值-用于更新双buffer的阈值。目前阈值比是50%)
	 */
	private Long middleId;

	/**
	 * 上次更新时间
	 */
	private Date lastUpdateTime;

	/**
	 * 本次更新时间
	 */
	private Date currentUpdateTime;

	public LeafSegment() {

	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Date getCurrentUpdateTime() {
		return currentUpdateTime;
	}

	public void setCurrentUpdateTime(Date currentUpdateTime) {
		this.currentUpdateTime = currentUpdateTime;
	}

	public Long getMiddleId() {
		if (this.middleId == null) {
			this.middleId = this.maxId - (step / 2);
		}
		return middleId;
	}

	public Long getMinId() {
		if (this.minId == null) {
			if (this.maxId != null && this.step != null) {
				this.minId = this.maxId - this.step;
			} else {
				throw new RuntimeException("maxid or step is null");
			}
		}
		return minId;
	}

	public Long getMaxId() {
		return maxId;
	}

	public void setMaxId(Long maxId) {
		this.maxId = maxId;
	}

	public Long getStep() {
		return step;
	}

	public void setStep(Long step) {
		this.step = step;
	}
}
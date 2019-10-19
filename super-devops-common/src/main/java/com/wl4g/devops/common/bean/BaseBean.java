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
package com.wl4g.devops.common.bean;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 实体基类
 * 
 * @date 2018年9月25日
 */
public abstract class BaseBean {
	/**
	 * 启用状态:启用
	 */
	public static final int ENABLED = 1;
	/**
	 * 启用状态:禁用
	 */
	public static final int DISABLED = 0;
	/**
	 * 未删除状态
	 */
	public static final int DEL_FLAG_NORMAL = 0;
	/**
	 * 删除状态
	 */
	public static final int DEL_FLAG_DELETE = 1;

	/**
	 * 删除状态
	 */
	public static final int DEFAULT_USER_ID = 1;

	private Integer id;
	private Integer createBy; // 创建人
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createDate; // 创建时间
	private Integer updateBy; // 修改人
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateDate; // 修改日期
	private Integer delFlag; // 删除状态
	private Integer enable; // 启用状态
	private String remark; // 备注

	/**
	 * 插入之前执行方法，需要手动调用
	 */
	public void preInsert() {
		this.updateDate = new Date();
		this.createDate = this.updateDate;
		this.createBy = DEFAULT_USER_ID;
		updateBy = DEFAULT_USER_ID;
	}

	/**
	 * 更新之前执行方法，需要手动调用
	 */
	public void preUpdate() {
		this.updateDate = new Date();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCreateBy() {
		return createBy;
	}

	public void setCreateBy(Integer createBy) {
		this.createBy = createBy;
	}

	public Integer getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(Integer updateBy) {
		this.updateBy = updateBy;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}

	public Integer getEnable() {
		return enable;
	}

	public void setEnable(Integer enable) {
		this.enable = enable;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
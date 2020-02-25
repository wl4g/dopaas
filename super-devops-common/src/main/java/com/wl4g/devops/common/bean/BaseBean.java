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

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.RandomUtils;

/**
 * DB based bean entity.
 * 
 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0.0 2018-09-05
 * @since
 */
public abstract class BaseBean implements Serializable {
	private static final long serialVersionUID = 8940373806493080114L;

	/**
	 * Status: enabled
	 */
	final public static int ENABLED = 1;

	/**
	 * Status: disabled
	 */
	final public static int DISABLED = 0;

	/**
	 * Status: normal (not deleted)
	 */
	final public static int DEL_FLAG_NORMAL = 0;

	/**
	 * Status: deleted
	 */
	final public static int DEL_FLAG_DELETE = 1;

	/**
	 * Default userId.
	 */
	final public static int DEFAULT_USER_ID = 1;

	/*
	 * User: Super administrator account name
	 */
	final public static String DEFAULT_USER_ROOT = "root";

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
	 * Execute method before inserting, need to call manually
	 */
	public void preInsert() {
		// TODO Use random number just for now
		setId(RandomUtils.nextInt(1_0000, 10_0000));

		setCreateDate(new Date());
		setCreateBy(DEFAULT_USER_ID);
		setUpdateDate(getCreateDate());
		setUpdateBy(DEFAULT_USER_ID);
		setDelFlag(DEL_FLAG_NORMAL);
	}

	/**
	 * Execute method before update, need to call manually
	 */
	public void preUpdate() {
		setUpdateDate(new Date());
		setUpdateBy(DEFAULT_USER_ID);
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
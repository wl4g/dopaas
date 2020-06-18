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
import com.wl4g.devops.components.tools.common.id.SnowflakeIdGenerator;
import com.wl4g.devops.components.tools.common.lang.period.PeriodFormatterHolder;

import java.io.Serializable;
import java.util.Date;

import static com.wl4g.devops.components.tools.common.serialize.JacksonUtils.toJSONString;
import static java.lang.Math.abs;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

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
	 * Bean info unqiue ID.
	 */
	private Integer id;

	/**
	 * Bean info create user.
	 */
	private Integer createBy;

	/**
	 * Bean info create date.
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createDate;

	/**
	 * Bean info update user.
	 */
	private Integer updateBy;

	/**
	 * Bean info update date.
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updateDate;

	/**
	 * Is enabled
	 */
	private Integer enable;

	/**
	 * Bean info remark desciprtion.
	 */
	private String remark;

	/**
	 * For data permission, associated Organization (tree) code query
	 */
	private String organizationCode;

	/**
	 * Logistic delete status.
	 */
	private Integer delFlag;

	/**
	 * Execute method before inserting, need to call manually
	 */
	public void preInsert() {
		// TODO
		// This is a temporary ID generation scheme. You can change
		// it to a primary key generation service later.

		setId(abs((int) (SnowflakeIdGenerator.getDefault().nextId() % 10_000_000_000L))); // unsafe-convert!!!

		setCreateDate(new Date());
		setCreateBy(DEFAULT_USER_ID);
		setUpdateDate(getCreateDate());
		setUpdateBy(DEFAULT_USER_ID);
		setDelFlag(DEL_FLAG_NORMAL);
		setEnable(ENABLED);
	}

	/**
	 * Execute method before inserting, need to call manually
	 *
	 * @param organizationCode
	 */
	public void preInsert(String organizationCode) {
		if (isBlank(getOrganizationCode())) {
			setOrganizationCode(organizationCode);
		}
		preInsert();
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
		if (isNull(this.enable)) {
			this.enable = enable;
		}
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getOrganizationCode() {
		return organizationCode;
	}

	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}

	//
	// --- Function's. ---
	//

	public String getHumanCreateDate() {
		return isNull(getCreateDate()) ? null : defaultPeriodFormatter.formatHumanDate(getCreateDate().getTime());
	}

	public String getHumanUpdateDate() {
		return isNull(getUpdateDate()) ? null : defaultPeriodFormatter.formatHumanDate(getUpdateDate().getTime());
	}

	@Override
	public String toString() {
		return getClass().getSimpleName().concat("<").concat(toJSONString(this)).concat(">");
	}

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
	 * User: Super administrator account.
	 */
	final public static String DEFAULT_USER_ROOT = "root";

	/*
	 * Human date formatter instance.
	 */
	final public static PeriodFormatterHolder defaultPeriodFormatter = PeriodFormatterHolder.getDefault();

}
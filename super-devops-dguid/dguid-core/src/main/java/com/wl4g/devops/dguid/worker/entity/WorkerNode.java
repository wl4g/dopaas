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
package com.wl4g.devops.dguid.worker.entity;

import java.util.Date;

import com.wl4g.devops.dguid.worker.WorkerIdAssigner.WorkerNodeType;

import lombok.ToString;

/**
 * {@link WorkerNode}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年2月10日
 * @since
 */
@ToString
public class WorkerNode {

	/**
	 * Entity unique id (table unique)
	 */
	private long id;

	/**
	 * Type of CONTAINER: HostName, ACTUAL : IP.
	 */
	private String hostName;

	/**
	 * Type of CONTAINER: Port, ACTUAL : Timestamp + Random(0-10000)
	 */
	private String port;

	/**
	 * type of {@link WorkerNodeType}
	 */
	private int type;

	/**
	 * Worker launch date, default now
	 */
	private Date launchDate = new Date();

	/**
	 * Created time
	 */
	private Date created;

	/**
	 * Last modified
	 */
	private Date modified;

	/**
	 * Getters & Setters
	 */
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Date getLaunchDate() {
		return launchDate;
	}

	public void setLaunchDateDate(Date launchDate) {
		this.launchDate = launchDate;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

}
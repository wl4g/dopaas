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
package com.wl4g.devops.common.bean.scm;

import com.wl4g.devops.common.bean.BaseBean;

/**
 * 对应：scm_version表
 * 
 * @date 2018年9月25日
 */
public class ConfigVersion extends BaseBean {
	private static final long serialVersionUID = 4996129446638218612L;
	final public static String DEFUALT_SIGN = "MD5";

	private String sign; // 摘要计算字符串
	private String signtype; // 摘要算法名（如：md5/sha1）
	private Integer appClusterId; // 关联组ID
	private String groupName; // 关联组名称
	private String host; // 节点地址
	private String tag; // 版本状态（1:健康/2:缺陷）
	private Integer nodeId; // 节点id
	private String Ip; // 主机ip地址
	private String port;// 端口
	private String envRemark;// 环境备注

	public String getIp() {
		return Ip;
	}

	public void setIp(String ip) {
		Ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getEnvRemark() {
		return envRemark;
	}

	public void setEnvRemark(String envRemark) {
		this.envRemark = envRemark;
	}

	public Integer getNodeId() {
		return nodeId;
	}

	public void setNodeId(Integer nodeId) {
		this.nodeId = nodeId;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getSigntype() {
		return signtype;
	}

	public void setSigntype(String signtype) {
		this.signtype = signtype;
	}

	public Integer getAppClusterId() {
		return appClusterId;
	}

	public void setAppClusterId(Integer appClusterId) {
		this.appClusterId = appClusterId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
}
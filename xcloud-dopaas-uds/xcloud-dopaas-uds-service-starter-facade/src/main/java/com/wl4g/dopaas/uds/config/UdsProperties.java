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
package com.wl4g.dopaas.uds.config;

/**
 * @author vjay
 * @date 2020-04-21 10:32:00
 */
public class UdsProperties {

	private String branchFormat;

	private String tagFormat;

	public String getBranchFormat() {
		return branchFormat;
	}

	public void setBranchFormat(String branchFormat) {
		this.branchFormat = branchFormat;
	}

	public String getTagFormat() {
		return tagFormat;
	}

	public void setTagFormat(String tagFormat) {
		this.tagFormat = tagFormat;
	}
}
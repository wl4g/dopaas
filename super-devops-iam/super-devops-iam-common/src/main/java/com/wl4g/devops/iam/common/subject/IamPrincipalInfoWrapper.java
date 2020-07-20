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
package com.wl4g.devops.iam.common.subject;

/**
 * {@link IamPrincipalInfoWrapper}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年7月7日 v1.0.0
 * @see
 */
public class IamPrincipalInfoWrapper {

	/**
	 * {@link IamPrincipalInfo}
	 */
	private IamPrincipalInfo info;

	public IamPrincipalInfoWrapper() {
		super();
	}

	public IamPrincipalInfoWrapper(IamPrincipalInfo info) {
		super();
		this.info = info;
	}

	public IamPrincipalInfo getInfo() {
		return info;
	}

	public void setInfo(IamPrincipalInfo info) {
		this.info = info;
	}

	@Override
	public String toString() {
		return "IamPrincipalInfoWrapper [info=" + info + "]";
	}

}
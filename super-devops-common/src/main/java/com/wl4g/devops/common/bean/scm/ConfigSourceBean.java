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

import java.util.ArrayList;
import java.util.List;

public class ConfigSourceBean {

	private ConfigReleaseMeta releaseMeta = new ConfigReleaseMeta();

	private List<VersionContentBean> contents = new ArrayList<>();

	public ConfigReleaseMeta getReleaseMeta() {
		return releaseMeta;
	}

	public void setReleaseMeta(ConfigReleaseMeta releaseMeta) {
		if (releaseMeta != null) {
			this.releaseMeta = releaseMeta;
		}
	}

	public List<VersionContentBean> getContents() {
		return contents;
	}

	public void setContents(List<VersionContentBean> versions) {
		if (versions != null) {
			this.contents = versions;
		}
	}

}
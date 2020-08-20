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
package com.wl4g.devops.scm.common.command;

import javax.validation.constraints.NotNull;

import static com.wl4g.components.common.lang.Assert2.notNull;

import java.util.List;

/**
 * {@link FetchConfigRequest}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2018-08-17
 * @since
 */
public class FetchConfigRequest extends GenericConfigInfo {
	final private static long serialVersionUID = -4016863811283064989L;

	@NotNull
	private ConfigNode node;

	public FetchConfigRequest() {
		super();
	}

	public FetchConfigRequest(String cluster, List<String> namespaces, ConfigMeta meta, ConfigNode node) {
		super(cluster, namespaces, meta);
		setNode(node);
	}

	public ConfigNode getNode() {
		return node;
	}

	public void setNode(ConfigNode instance) {
		if (instance != null) {
			this.node = instance;
		}
	}

	@Override
	public void validate(boolean versionValidate, boolean releaseValidate) {
		super.validate(versionValidate, releaseValidate);
		notNull(getNode(), "Invalid empty release instance");
		getNode().validation();
	}

}
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
package com.wl4g.devops.common.bean.ci;

import com.wl4g.components.core.bean.BaseBean;

public class PipeStageInstanceCommand extends BaseBean {
	private static final long serialVersionUID = 6815608076300843748L;

	private Long pipeId;

	private String preCommand;

	private String postCommand;

	public Long getPipeId() {
		return pipeId;
	}

	public void setPipeId(Long pipeId) {
		this.pipeId = pipeId;
	}

	public String getPreCommand() {
		return preCommand;
	}

	public void setPreCommand(String preCommand) {
		this.preCommand = preCommand == null ? null : preCommand.trim();
	}

	public String getPostCommand() {
		return postCommand;
	}

	public void setPostCommand(String postCommand) {
		this.postCommand = postCommand == null ? null : postCommand.trim();
	}
}
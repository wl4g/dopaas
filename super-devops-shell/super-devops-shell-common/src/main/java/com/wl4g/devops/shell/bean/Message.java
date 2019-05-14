/*
 * Copyright 2015 the original author or authors.
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
package com.wl4g.devops.shell.bean;

import java.io.Serializable;

import org.apache.commons.lang3.RandomStringUtils;

import com.wl4g.devops.shell.utils.Assert;

/**
 * Shell transport message
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月4日
 * @since
 */
public abstract class Message implements Serializable {
	private static final long serialVersionUID = 771621623867420464L;

	private Integer processId;

	public Message() {
		super();
	}

	public Message(Integer processId) {
		Assert.notNull(processId, "processId must not be null");
		this.processId = processId;
	}

	public Integer getProcessId() {
		return processId;
	}

	public void setProcessId(Integer msgId) {
		this.processId = msgId;
	}

	public final static Integer nextProcessId() {
		return Integer.parseInt(RandomStringUtils.randomNumeric(8));
	}

}

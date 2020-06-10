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
package com.wl4g.devops.guid.worker;

import com.wl4g.devops.guid.baidu.utils.ValuedEnum;

/**
 * {@link WorkerNodeType}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年2月10日
 * @since
 */
public enum WorkerNodeType implements ValuedEnum<Integer> {

	/**
	 * Docker容器节点
	 */
	CONTAINER(1),

	/**
	 * 普通节点
	 */
	ACTUAL(2);

	/**
	 * Lock type
	 */
	private final Integer type;

	/**
	 * Constructor with field of type
	 */
	private WorkerNodeType(Integer type) {
		this.type = type;
	}

	@Override
	public Integer value() {
		return type;
	}

}
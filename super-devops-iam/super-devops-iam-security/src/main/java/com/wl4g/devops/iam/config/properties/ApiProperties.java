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
package com.wl4g.devops.iam.config.properties;

import static com.wl4g.devops.tool.common.serialize.JacksonUtils.toJSONString;
import static org.springframework.util.Assert.isTrue;

import java.io.Serializable;

/**
 * IAM API configuration properties
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2019年10月31日
 * @since
 */
public class ApiProperties implements Serializable {
	private static final long serialVersionUID = -2694422471852860689L;

	/**
	 * Sessions max iteration batch size.
	 */
	private int maxIteraSize = 200;

	public int getMaxIteraSize() {
		return maxIteraSize;
	}

	public void setMaxIteraSize(int maxIteraSize) {
		isTrue(maxIteraSize > 0, "maxIteraSize must >0");
		this.maxIteraSize = maxIteraSize;
	}

	@Override
	public String toString() {
		return toJSONString(this);
	}

}
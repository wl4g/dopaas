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
package com.wl4g.devops.components.shell.console.args;

import java.io.Serializable;

import com.wl4g.devops.components.tools.common.cli.annotation.PropertyDescription;

public class SumResult implements Serializable {

	private static final long serialVersionUID = -3398687888016885699L;

	@PropertyDescription("Addition sum")
	private int sum;

	public SumResult() {
		super();
	}

	public SumResult(int sum) {
		super();
		this.sum = sum;
	}

	public int getSum() {
		return sum;
	}

	public void setSum(int sum) {
		this.sum = sum;
	}

	@Override
	public String toString() {
		return "AdditionResult [sum=" + sum + "]";
	}

}
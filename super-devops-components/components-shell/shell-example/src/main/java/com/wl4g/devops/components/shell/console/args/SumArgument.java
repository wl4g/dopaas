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

import com.wl4g.devops.components.shell.annotation.ShellOption;

public class SumArgument implements Serializable {

	private static final long serialVersionUID = -90377698662015272L;

	@ShellOption(opt = "a", lopt = "add1", help = "Add number")
	private int add1;

	@ShellOption(opt = "b", lopt = "add2", help = "Added number", defaultValue = "1")
	private int add2;

	public SumArgument() {
		super();
	}

	public SumArgument(int add1, int add2) {
		super();
		this.add1 = add1;
		this.add2 = add2;
	}

	public int getAdd1() {
		return add1;
	}

	public void setAdd1(int add1) {
		this.add1 = add1;
	}

	public int getAdd2() {
		return add2;
	}

	public void setAdd2(int add2) {
		this.add2 = add2;
	}

	@Override
	public String toString() {
		return "AdditionArgument [add1=" + add1 + ", add2=" + add2 + "]";
	}

}
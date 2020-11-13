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
package com.wl4g.devops.ci.console.args;

import java.io.Serializable;

import com.wl4g.shell.common.annotation.ShellOption;

import lombok.Getter;
import lombok.Setter;

/**
 * {@link ManualCleanArgument}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020-11-13
 * @sine v1.0.0
 * @see
 */
@Setter
@Getter
public class ManualCleanArgument implements Serializable {
	private static final long serialVersionUID = -90377698662015272L;

	@ShellOption(opt = "n", lopt = "remainDay", help = "Remain the log history of the last N days", required = false)
	private Integer remainDay;

}
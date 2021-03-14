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
package com.wl4g.paas.uci.console.args;

import java.io.Serializable;

import com.wl4g.shell.common.annotation.ShellOption;

import lombok.Getter;
import lombok.Setter;

/**
 * {@link EvictionIntervalArgument}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2019-05-21
 * @sine v1.0
 * @see
 */
@Setter
@Getter
public class EvictionIntervalArgument implements Serializable {
	private static final long serialVersionUID = -90377698662015272L;

	@ShellOption(opt = "t", lopt = "evictionInternalMs", help = "Global jobs timeout finalizer max-intervalMs", required = true)
	private Long evictionIntervalMs;

}
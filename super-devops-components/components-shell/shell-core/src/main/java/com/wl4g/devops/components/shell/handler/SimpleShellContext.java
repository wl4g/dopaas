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
package com.wl4g.devops.components.shell.handler;

import com.wl4g.devops.components.shell.exception.ChannelShellException;

/**
 * {@link SimpleShellContext}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年2月4日 v1.0.0
 * @see
 */
public class SimpleShellContext extends AbstractShellContext {

	SimpleShellContext(AbstractShellContext context) {
		super(context);
	}

	/**
	 * Print simple message to client console.
	 *
	 * @param message
	 * @return
	 * @throws ChannelShellException
	 */
	public SimpleShellContext printf(String message) throws ChannelShellException {
		return (SimpleShellContext) printf0(message);
	}

}
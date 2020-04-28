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

import static java.lang.System.err;
import static org.apache.commons.lang3.exception.ExceptionUtils.*;
import org.apache.commons.lang3.StringUtils;

import com.wl4g.devops.components.shell.config.Configuration;

/**
 * Script command line(client) shell handler.
 *
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月14日
 * @since
 */
public class ScriptClientShellHandler extends AbstractClientShellHandler {

	public ScriptClientShellHandler(Configuration config) {
		super(config);
	}

	@Override
	public void run(String[] args) {
		String line = StringUtils.join(args, " ");
		try {
			writeStdin(line);
		} catch (Throwable e) {
			err.println(getStackTrace(e));
			shutdown();
		}
	}

}
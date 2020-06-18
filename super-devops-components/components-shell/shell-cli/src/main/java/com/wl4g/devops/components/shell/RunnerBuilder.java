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
package com.wl4g.devops.components.shell;

import java.lang.reflect.Constructor;
import java.net.URL;

import static com.wl4g.devops.components.tools.common.lang.Assert2.*;
import static org.apache.commons.lang3.StringUtils.*;

import com.wl4g.devops.components.shell.config.Configuration;
import com.wl4g.devops.components.shell.handler.CliShellHandler;

/**
 * Runner builder
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月2日
 * @since
 */
public abstract class RunnerBuilder {

	private String conf;

	private Class<? extends CliShellHandler> provider;

	private RunnerBuilder() {
	}

	public final static RunnerBuilder builder() {
		return new RunnerBuilder() {
		};
	}

	public RunnerBuilder config(String conf) {
		hasText(conf, "conf is empty, please check configure");
		this.conf = conf;
		return this;
	}

	public RunnerBuilder provider(Class<? extends CliShellHandler> provider) {
		notNull(provider, "provider is null, please check configure");
		this.provider = provider;
		return this;
	}

	public CliShellHandler build() {
		try {
			Configuration config = Configuration.create();
			if (isNotBlank(conf)) {
				config = Configuration.create(new URL("file://" + conf));
			}
			notNull(provider, "provider is null, please check configure");
			notNull(config, "config is null, please check configure");

			Constructor<? extends CliShellHandler> constr = provider.getConstructor(Configuration.class);
			return constr.newInstance(new Object[] { config });
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}
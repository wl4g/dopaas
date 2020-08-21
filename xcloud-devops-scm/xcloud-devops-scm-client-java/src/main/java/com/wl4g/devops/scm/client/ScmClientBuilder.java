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
package com.wl4g.devops.scm.client;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import org.apache.commons.beanutils.BeanUtilsBean2;

import com.wl4g.devops.scm.client.config.ConfCommType;
import static com.wl4g.devops.scm.client.config.ConfCommType.*;
import com.wl4g.devops.scm.client.config.ScmClientProperties;
import com.wl4g.devops.scm.client.console.RefreshableConfigConsole;
import com.wl4g.devops.scm.client.event.ConfigEventListener;
import com.wl4g.devops.scm.client.repository.InMemoryRefreshConfigRepository;
import com.wl4g.devops.scm.client.repository.RefreshConfigRepository;
import com.wl4g.devops.scm.client.watch.RefreshWatcher;
import com.wl4g.devops.scm.common.exception.ScmException;
import com.wl4g.shell.core.EmbeddedShellServerBuilder;
import com.wl4g.shell.core.handler.EmbeddedShellServer;

/**
 * {@link ScmClientBuilder}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-19
 * @since
 */
@SuppressWarnings("serial")
public class ScmClientBuilder extends ScmClientProperties<ScmClientBuilder> {

	/** Configuration refreshing internal comm protocol type. */
	private ConfCommType commType = ConfCommType.HLP;

	/** Configuration refreshing {@link ConfigEventListener} */
	private ConfigEventListener[] listeners;

	/** {@link RefreshConfigRepository} */
	private RefreshConfigRepository repository = new InMemoryRefreshConfigRepository();

	/** Enable refreshable console for {@link RefreshableConfigConsole} */
	private boolean enableRefreshableConsole = false;

	/** {@link EmbeddedShellServer} */
	private EmbeddedShellServer shellServer;

	/**
	 * New create {@link ScmClientBuilder} instance
	 * 
	 * @return
	 */
	public static ScmClientBuilder newBuilder() {
		return new ScmClientBuilder();
	}

	/**
	 * Sets SCM client configuration of {@link ScmClientProperties#HLP}
	 * 
	 * @return
	 */
	public ScmClientBuilder withConfiguration(ScmClientProperties<?> config) {
		if (nonNull(config)) {
			try {
				BeanUtilsBean2.getInstance().copyProperties(config, this);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		return this;
	}

	/**
	 * Sets SCM internal comm protocol type. The default using
	 * {@link ConfCommType#HLP}
	 * 
	 * @return
	 */
	public ScmClientBuilder withCommType(ConfCommType commType) {
		if (nonNull(commType)) {
			this.commType = commType;
		}
		return this;
	}

	/**
	 * Sets SCM configuration refresh listeners.
	 * 
	 * @return
	 */
	public ScmClientBuilder withListeners(ConfigEventListener... listeners) {
		if (nonNull(listeners)) {
			this.listeners = listeners;
		}
		return this;
	}

	/**
	 * Sets use refresh configuration source of
	 * {@link InMemoryRefreshConfigRepository}
	 * 
	 * @return
	 */
	public ScmClientBuilder useInMemoryConfigStore() {
		this.repository = new InMemoryRefreshConfigRepository();
		return this;
	}

	/**
	 * Enable startup refreshable configuration console.
	 * 
	 * @return
	 */
	public ScmClientBuilder enableRefreshableConsole() {
		this.enableRefreshableConsole = true;
		return this;
	}

	/**
	 * Build {@link ScmClient} with builder configuration.
	 * 
	 * @return
	 */
	public ScmClient build() {
		ScmClient client = null;

		if (commType == HLP) {
			client = new HlpScmClient(this, repository, listeners);
		} else if (commType == RPC) {
			client = new RpcScmClient(this, repository, listeners);
		} else {
			throw new Error("shouldn't be here");
		}

		// Start console
		if (enableRefreshableConsole) {
			startRefreshableConsole(((GenericScmClient) client).getWatcher());
		}

		return client;
	}

	/**
	 * Enable startup refreshable configuration console.
	 * 
	 * @param watcher
	 */
	public void startRefreshableConsole(RefreshWatcher watcher) {
		try {
			String consolePrompt = getConsolePrompt();
			if (isBlank(consolePrompt)) {
				consolePrompt = getClusterName();
			}

			this.shellServer = EmbeddedShellServerBuilder.newBuilder().withAppName(consolePrompt)
					.register(new RefreshableConfigConsole(watcher)).build();
			this.shellServer.start();
		} catch (Exception e) {
			throw new ScmException(e);
		}
	}

}

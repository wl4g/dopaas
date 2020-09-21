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

import com.wl4g.devops.scm.client.config.CommType;
import static com.wl4g.devops.scm.client.config.CommType.*;
import com.wl4g.devops.scm.client.config.ScmClientProperties;
import com.wl4g.devops.scm.client.console.ScmManagementConsole;
import com.wl4g.devops.scm.client.event.ConfigEventListener;
import com.wl4g.devops.scm.client.repository.InMemoryRefreshRecordsRepository;
import com.wl4g.devops.scm.client.repository.RefreshRecordsRepository;
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
	private CommType commType = CommType.HLP;

	/** Configuration refreshing {@link ConfigEventListener} */
	private ConfigEventListener[] listeners;

	/** {@link RefreshRecordsRepository} */
	private RefreshRecordsRepository repository = new InMemoryRefreshRecordsRepository();

	/** Enable managementconsole for {@link ScmManagementConsole} */
	private boolean enableManagementConsole = false;

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
	 * {@link CommType#HLP}
	 * 
	 * @return
	 */
	public ScmClientBuilder withCommType(CommType commType) {
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
	 * {@link InMemoryRefreshRecordsRepository}
	 * 
	 * @return
	 */
	public ScmClientBuilder useInMemoryConfigStore() {
		this.repository = new InMemoryRefreshRecordsRepository();
		return this;
	}

	/**
	 * Enable startup management console.
	 * 
	 * @return
	 */
	public ScmClientBuilder enableManagementConsole() {
		this.enableManagementConsole = true;
		return this;
	}

	/**
	 * Build {@link ScmClient} with builder configuration.
	 * 
	 * @return
	 */
	public ScmClient build() {
		validate();

		ScmClient client = null;
		if (commType == HLP) {
			client = new HlpScmClient(this, repository, listeners);
		} else if (commType == RPC) {
			client = new RpcScmClient(this, repository, listeners);
		} else {
			throw new Error("shouldn't be here");
		}

		// Start console
		if (enableManagementConsole) {
			startManagementConsole(((GenericScmClient) client).getWatcher());
		}

		return client;
	}

	/**
	 * Enable starting management configuration console.
	 * 
	 * @param watcher
	 */
	public void startManagementConsole(RefreshWatcher watcher) {
		try {
			String consolePrompt = getConsolePrompt();
			if (isBlank(consolePrompt)) {
				consolePrompt = getCluster();
			}

			this.shellServer = EmbeddedShellServerBuilder.newBuilder().withAppName(consolePrompt)
					.register(new ScmManagementConsole(watcher)).build();
			this.shellServer.start();
		} catch (Exception e) {
			throw new ScmException(e);
		}
	}

}

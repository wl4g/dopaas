/*
 * Copyright 2015 the original author or authors.
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
package com.wl4g.devops.shell.processor;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.exception.ExceptionUtils.getMessage;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import static com.wl4g.devops.shell.bean.LineResultState.*;

import com.wl4g.devops.shell.bean.LineResultState;
import com.wl4g.devops.shell.bean.ResultMessage;
import com.wl4g.devops.shell.handler.ChannelMessageHandler;
import com.wl4g.devops.shell.processor.EmbeddedServerProcessor.ShellHandler;
import com.wl4g.devops.shell.registry.InternalInjectable;

/**
 * Shell handler context
 * 
 * @author wangl.sir
 * @version v1.0 2019年5月24日
 * @since
 */
public class ShellContext implements InternalInjectable {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/** Shell handler client. */
	final private ShellHandler client;

	/** Line result message state. */
	private LineResultState state;

	public ShellContext(ShellHandler client) {
		this(client, NONCE);
	}

	public ShellContext(ShellHandler client, LineResultState state) {
		Assert.notNull(client, "Client must not be null");
		this.client = client;
		this.state = state;
	}

	public LineResultState getState() {
		return state;
	}

	public ShellHandler getClient() {
		return client;
	}

	/**
	 * Manually open data flow message transaction output.
	 */
	public final void begin() {
		this.state = RESP_WAIT;

		// Output start mark
		write(EMPTY);
	}

	/**
	 * Manually end data flow message transaction output.
	 */
	public final void end() {
		this.state = FINISHED;

		// Output end mark
		write(EMPTY);
	}

	/**
	 * Manually output simple string message to the client console.
	 * 
	 * @param message
	 */
	public void write(String message) {
		ChannelMessageHandler client = getClient();
		if (client != null && client.isActive()) {
			try {
				client.writeAndFlush(new ResultMessage(getState(), message));
			} catch (IOException e) {
				String errmsg = getRootCauseMessage(e);
				errmsg = isBlank(errmsg) ? getMessage(e) : errmsg;
				log.error("=> {}", errmsg);
			}
		}
	}

}

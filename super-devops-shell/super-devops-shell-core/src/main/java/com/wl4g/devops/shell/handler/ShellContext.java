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
package com.wl4g.devops.shell.handler;

import com.wl4g.devops.shell.handler.EmbeddedServerShellHandler.ServerShellMessageChannel;
import com.wl4g.devops.shell.message.ChannelState;
import com.wl4g.devops.shell.message.StderrMessage;
import com.wl4g.devops.shell.message.Message;
import com.wl4g.devops.shell.message.StdoutMessage;
import com.wl4g.devops.shell.message.ProgressMessage;
import com.wl4g.devops.shell.registry.InternalInjectable;
import org.slf4j.Logger;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import static com.wl4g.devops.shell.handler.ShellMessageChannel.*;
import static com.wl4g.devops.shell.message.ChannelState.*;
import static com.wl4g.devops.tool.common.lang.Assert2.notNull;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.exception.ExceptionUtils.getMessage;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

/**
 * Shell handler context
 *
 * @author wangl.sir
 * @version v1.0 2019年5月24日
 * @since
 */
public final class ShellContext implements InternalInjectable {
	final public static int DEFAULT_WHOLE = 100;

	final protected Logger log = getLogger(getClass());

	/**
	 * Event listeners
	 */
	final private List<ShellEventListener> eventListeners = new Vector<>(4);

	/**
	 * Shell message channel.
	 */
	final private ServerShellMessageChannel channel;

	/**
	 * Line result message state.
	 */
	private ChannelState state;

	ShellContext(ServerShellMessageChannel client) {
		this(client, NEW);
	}

	ShellContext(ServerShellMessageChannel channel, ChannelState state) {
		notNull(channel, "Shell channel must not be null");
		notNull(channel, "State must not be null");
		this.channel = channel;
		this.state = state;

		// Register default listener.
		addEventListener(new ShellEventListener() {
			// Ignore
		});
	}

	ShellContext setState(ChannelState state) {
		notNull(channel, "State must not be null");
		this.state = state;
		return this;
	}

	public ChannelState getState() {
		return state;
	}

	/**
	 * Open the channel of the current command line, effect: at this time, the
	 * client console will wait for execution to complete (until the
	 * {@link #completed()} method is called).
	 */
	synchronized ShellContext begin() {
		state = RUNNING;
		// Print begin mark
		printf(BOF);
		return this;
	}

	/**
	 * Complete processing the current command line channel, effect: the client
	 * will reopen the console prompt.
	 */
	public synchronized void completed() {
		state = COMPLETED;
		printf(EOF); // Ouput end mark
	}

	/**
	 * Are you currently in an interrupt state? (if the current thread does not
	 * open the shell channel, it will return false, that is, uninterrupted)
	 * 
	 * @return
	 */
	public final boolean isInterrupted() {
		return nonNull(state) ? (state == INTERRUPTED) : false;
	}

	/**
	 * Get target event listeners.
	 * 
	 * @return
	 */
	public List<ShellEventListener> getEventListeners() {
		return unmodifiableList(eventListeners);
	}

	/**
	 * Add event listener
	 * 
	 * @param eventListener
	 */
	public void addEventListener(ShellEventListener eventListener) {
		Assert.notNull(eventListener, "eventListener must not be null");
		eventListeners.add(eventListener);
	}

	/**
	 * Print message to the client console.
	 *
	 * @param message
	 * @throws IllegalStateException
	 */
	public ShellContext printf(Object message) throws IllegalStateException {
		Assert.notNull(message, "Printf message must not be null.");
		Assert.isTrue((message instanceof Message || message instanceof CharSequence || message instanceof Throwable),
				String.format("Unsupported print message types: %s", message.getClass()));
		// Check channel state.
		// To solve: com.wl4g.devops.shell.console.ExampleConsole#log3()#MARK1
		//
		// if (getState() != WAITING && !equalsAny(message.toString(), BOF,
		// EOF)) {
		// throw new IllegalStateException("Shell channel is not writable, has
		// it not opened or interrupted/closed?");
		// }

		if (nonNull(channel) && channel.isActive()) {
			try {
				if (message instanceof CharSequence) {
					channel.writeFlush(new StdoutMessage(getState(), message.toString()));
				} else if (message instanceof Throwable) {
					channel.writeFlush(new StderrMessage((Throwable) message));
				} else {
					channel.writeFlush(message);
				}
			} catch (IOException e) {
				String errmsg = getRootCauseMessage(e);
				errmsg = isBlank(errmsg) ? getMessage(e) : errmsg;
				log.error("=> {}", errmsg);
			}
		} else {
			throw new IllegalStateException("The current console channel may be closed!");
		}
		return this;
	}

	/**
	 * Print progress message to client console.
	 *
	 * @param title
	 * @param progressPercent
	 * @return
	 */
	public ShellContext printf(String title, float progressPercent) {
		return printf(new ProgressMessage(title, DEFAULT_WHOLE, (int) (DEFAULT_WHOLE * progressPercent)));
	}

	/**
	 * Print progress message to client console.
	 *
	 * @param title
	 * @param whole
	 * @param progress
	 * @return
	 */
	public ShellContext printf(String title, int whole, int progress) {
		return printf(new ProgressMessage(title, whole, progress));
	}

}
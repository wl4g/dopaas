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
package com.wl4g.devops.shell.processor;

import com.wl4g.devops.shell.message.ChannelState;
import com.wl4g.devops.shell.message.ExceptionMessage;
import com.wl4g.devops.shell.message.Message;
import com.wl4g.devops.shell.message.OutputMessage;
import com.wl4g.devops.shell.message.ProgressMessage;
import com.wl4g.devops.shell.processor.EmbeddedServerShellHandler.ShellHandler;
import com.wl4g.devops.shell.processor.event.EventListener;
import com.wl4g.devops.shell.processor.event.InterruptedEventListener;
import com.wl4g.devops.shell.registry.InternalInjectable;
import org.slf4j.Logger;
import org.springframework.util.Assert;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.wl4g.devops.shell.handler.InternalChannelMessageHandler.*;
import static com.wl4g.devops.shell.message.ChannelState.*;
import static com.wl4g.devops.tool.common.lang.Assert2.notNull;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
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
public final class ShellContext implements InternalInjectable, Closeable {
	final public static int DEFAULT_WHOLE = 100;

	final protected Logger log = getLogger(getClass());

	/**
	 * Event listeners
	 */
	final private Set<EventListener> eventListeners = new LinkedHashSet<>(4);

	/**
	 * Shell handler client.
	 */
	final private ShellHandler client;

	/**
	 * Line result message state.
	 */
	private ChannelState state;

	public ShellContext(ShellHandler client) {
		this(client, NONCE);
	}

	public ShellContext(ShellHandler client, ChannelState state) {
		notNull(client, "Client must not be null");
		notNull(client, "State must not be null");
		this.client = client;
		this.state = state;

		// Register default listener.
		addEventListener(new InterruptedEventListener(this));
	}

	public ChannelState getState() {
		return state;
	}

	public ShellContext setState(ChannelState state) {
		notNull(client, "State must not be null");
		this.state = state;
		return this;
	}

	/**
	 * Open console stream channel.
	 */
	public synchronized ShellContext open() {
		this.state = RUNNING_WAIT;
		// Print start mark
		printf(BOF);
		return this;
	}

	/**
	 * End console stream channel.
	 */
	@Override
	public synchronized void close() {
		this.state = COMPLATED;
		printf(EOF); // Ouput end mark
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
		if (getState() != RUNNING_WAIT && !equalsAny(message.toString(), BOF, EOF)) {
			throw new IllegalStateException("The shell is not printable in the afternoon, has it not been opened or closed?");
		}

		if (client != null && client.isActive()) {
			try {
				if (message instanceof CharSequence) {
					client.writeAndFlush(new OutputMessage(getState(), message.toString()));
				} else if (message instanceof Throwable) {
					client.writeAndFlush(new ExceptionMessage((Throwable) message));
				} else {
					client.writeAndFlush(message);
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

	/**
	 * Are you currently in an interrupt state? (if the current thread does not
	 * open the shell channel, it will return false, that is, uninterrupted)
	 * 
	 * @return
	 */
	public boolean isInterrupted() {
		return nonNull(state) ? (state == INTERRUPTED) : false;
	}

	/**
	 * Invoke event listeners
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	<T extends EventListener> List<T> publishEvent(Class<T> clazz) {
		return (List<T>) eventListeners.stream().filter(l -> l.getClass() == clazz).collect(toList());
	}

	/**
	 * Add event listener
	 * 
	 * @param eventListener
	 */
	public void addEventListener(EventListener eventListener) {
		Assert.notNull(eventListener, "eventListener must not be null");
		this.eventListeners.add(eventListener);
	}

}
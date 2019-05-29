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

import com.wl4g.devops.shell.bean.ExceptionMessage;
import com.wl4g.devops.shell.bean.RunState;
import com.wl4g.devops.shell.handler.ChannelMessageHandler;
import com.wl4g.devops.shell.bean.Message;
import com.wl4g.devops.shell.bean.ResultMessage;
import com.wl4g.devops.shell.processor.EmbeddedServerProcessor.ShellHandler;
import com.wl4g.devops.shell.registry.InternalInjectable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.Closeable;
import java.io.IOException;
import java.util.LinkedList;

import static com.wl4g.devops.shell.handler.ChannelMessageHandler.*;
import static com.wl4g.devops.shell.bean.RunState.*;
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

	final protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Shell handler client.
	 */
	final private ShellHandler client;

	/**
	 * Line result message state.
	 */
	private RunState state;

	/**
	 * Event listeners
	 */
	final private LinkedList<EventListener> eventListeners = new LinkedList<>();

	public ShellContext(ShellHandler client) {
		this(client, NONCE);
	}

	public ShellContext(ShellHandler client, RunState state) {
		Assert.notNull(client, "Client must not be null");
		this.client = client;
		this.state = state;

		// Register default listener.
		addEventListener(() -> this.state = INTERRUPTED);
	}

	public RunState getState() {
		return state;
	}

	public ShellHandler getClient() {
		return client;
	}

	/**
	 * Open console stream channel.
	 */
	public synchronized void open() {
		this.state = RUNNING_WAIT;

		// Print start mark
		printf(BOF);
	}

	/**
	 * End console stream channel.
	 */
	@Override
	public synchronized void close() {
		this.state = COMPLATED;

		// Print end mark
		printf(EOF);
	}

	/**
	 * Print message to the client console.
	 *
	 * @param message
	 * @throws IllegalStateException
	 */
	public void printf(Object message) throws IllegalStateException {
		Assert.notNull(message, "Printf message must not be null.");
		Assert.isTrue((message instanceof Message || message instanceof CharSequence || message instanceof Throwable),
				String.format("Unsupported print message types: %s", message.getClass()));

		// Channel state check
		if (getState() != RUNNING_WAIT && !equalsAny(message.toString(), BOF, EOF)) {
			throw new IllegalStateException("The shell is not printable in the afternoon, has it not been opened or closed?");
		}

		ChannelMessageHandler client = getClient();
		if (client != null && client.isActive()) {
			try {
				if (message instanceof CharSequence) {
					client.writeAndFlush(new ResultMessage(getState(), message.toString()));
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

	}

	/**
	 * Print quietly message to client console
	 *
	 * @param message
	 */
	public void printfQuietly(String message) {
		try {
			printf(message);
		} catch (Exception e) {
			log.warn("Printf error. cause: {}", getRootCauseMessage(e));
		}
	}

	/**
	 * Print quietly throwable message to client console
	 *
	 * @param message
	 */
	public void printfQuietly(Throwable th) {
		try {
			printf(th);
		} catch (Exception e) {
			log.warn("Printf error. cause: {}", getRootCauseMessage(e));
		}
	}

	/**
	 * Get event listeners
	 * 
	 * @return
	 */
	public LinkedList<EventListener> getEventListeners() {
		return eventListeners;
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

	/**
	 * Are you currently in an interrupt state? (if the current thread does not
	 * open the shell channel, it will return false, that is, uninterrupted)
	 * 
	 * @return
	 */
	public boolean isInterruptIfNecessary() {
		return state != null ? (state == INTERRUPTED) : false;
	}

	/**
	 * Event listener
	 * 
	 * @author Wangl.sir <983708408@qq.com>
	 * @version v1.0 2019年5月25日
	 * @since
	 */
	public static interface EventListener {
		void onInterrupt();
	}

}

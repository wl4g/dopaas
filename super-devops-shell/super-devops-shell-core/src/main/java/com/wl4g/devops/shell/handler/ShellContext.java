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

import com.wl4g.devops.shell.exception.ShellException;
import com.wl4g.devops.shell.handler.EmbeddedServerShellHandler.ServerShellMessageChannel;
import com.wl4g.devops.shell.registry.InternalInjectable;
import com.wl4g.devops.shell.signal.BOFStdoutSignal;
import com.wl4g.devops.shell.signal.ChannelState;
import com.wl4g.devops.shell.signal.EOFStdoutSignal;
import com.wl4g.devops.shell.signal.Signal;
import com.wl4g.devops.shell.signal.ProgressSignal;
import com.wl4g.devops.shell.signal.StderrSignal;
import com.wl4g.devops.shell.signal.StdoutSignal;

import org.slf4j.Logger;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.wl4g.devops.shell.signal.ChannelState.*;
import static com.wl4g.devops.tool.common.lang.Assert2.notNull;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.lang.String.format;
import static java.util.Collections.synchronizedMap;
import static java.util.Collections.unmodifiableList;
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
public final class ShellContext implements InternalInjectable {
	final public static int DEFAULT_WHOLE = 100;
	final public static String DEFAULT_INTERRUPT_LISTENER = "defaultInterruptListener";

	final protected Logger log = getLogger(getClass());

	/**
	 * Event listeners
	 */
	final private Map<String, ShellEventListener> eventListeners = synchronizedMap(new LinkedHashMap<>(4));

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
		addEventListener(DEFAULT_INTERRUPT_LISTENER, new ShellEventListener() {
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
		printf(new BOFStdoutSignal());
		return this;
	}

	/**
	 * Complete processing the current command line channel, effect: the client
	 * will reopen the console prompt.</br>
	 * </br>
	 * <b><font color=red>Note: Don't forget to execute it, or the client
	 * console will pause until it timesout.</font><b>
	 */
	public synchronized void completed() {
		state = COMPLETED;
		printf(new EOFStdoutSignal()); // Ouput end mark
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
		return unmodifiableList(eventListeners.values().stream().collect(toList()));
	}

	/**
	 * Add event listener
	 * 
	 * @param name
	 * @param eventListener
	 */
	public void addEventListener(String name, ShellEventListener eventListener) {
		Assert.notNull(eventListener, "eventListener must not be null");
		eventListeners.put(name, eventListener);
	}

	/**
	 * Print message to the client console.
	 *
	 * @param output
	 * @throws IllegalStateException
	 */
	public ShellContext printf(Object output) throws IllegalStateException {
		Assert.notNull(output, "Printf message must not be null.");
		Assert.isTrue((output instanceof Signal || output instanceof CharSequence || output instanceof Throwable),
				format("Unsupported print message types: %s", output.getClass()));

		// Check channel state.
		// To solve: com.wl4g.devops.shell.console.ExampleConsole#log3()#MARK1
		// if (getState() != WAITING && !equalsAny(output.toString(), BOF,
		// EOF)) {
		// throw new IllegalStateException("Shell channel is not writable, has
		// it not opened or interrupted/closed?");
		// }

		if (nonNull(channel) && channel.isActive()) {
			try {
				if (output instanceof CharSequence) {
					channel.writeFlush(new StdoutSignal(output.toString()));
				} else if (output instanceof Throwable) {
					channel.writeFlush(new StderrSignal((Throwable) output));
				} else if (output instanceof Signal) {
					channel.writeFlush(output);
				} else {
					throw new ShellException(format("Unsupported printf message type of '%s'", output));
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
		return printf(new ProgressSignal(title, DEFAULT_WHOLE, (int) (DEFAULT_WHOLE * progressPercent)));
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
		return printf(new ProgressSignal(title, whole, progress));
	}

}
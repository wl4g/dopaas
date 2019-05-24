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
package com.wl4g.devops.shell.handler;

import com.wl4g.devops.shell.registry.ShellBeanRegistry;
import com.wl4g.devops.shell.utils.Assert;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static java.lang.System.err;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

/**
 * Shell message handler
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月2日
 * @since
 */
public abstract class ChannelMessageHandler implements Runnable, Closeable {

	/**
	 * Currently running?
	 */
	final protected AtomicBoolean running = new AtomicBoolean(false);

	/**
	 * Local shell component registry.
	 */
	final protected ShellBeanRegistry registry;

	/**
	 * Client socket
	 */
	final protected Socket client;

	/**
	 * Callback function
	 */
	final protected Function<String, Object> function;

	/**
	 * Input stream
	 */
	protected InputStream _in;

	/**
	 * Out stream
	 */
	protected OutputStream _out;

	public ChannelMessageHandler(ShellBeanRegistry registry, Socket client, Function<String, Object> function) {
		Assert.notNull(client, "Socket client is null, please check configure");
		Assert.notNull(function, "Function is null, please check configure");
		Assert.notNull(registry, "Registry must not be null");
		this.registry = registry;
		this.client = client;
		this.function = function;
		if (running.compareAndSet(false, true)) {
			try {
				this._in = client.getInputStream();
				this._out = client.getOutputStream();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	/**
	 * Write and flush echo to client
	 * 
	 * @param message
	 * @throws IOException
	 */
	public synchronized void writeAndFlush(Object message) throws IOException {
		Assert.notNull(message, "message is null, please check configure");
		ObjectOutputStream out = new ObjectOutputStream(_out);
		out.writeObject(message);
		out.flush();
		_out.flush();
	}

	/**
	 * On starting
	 * 
	 * @return
	 */
	public ChannelMessageHandler starting() {
		return this;
	}

	/**
	 * Is connect active
	 * 
	 * @return
	 */
	public boolean isActive() {
		return client.isConnected() && !client.isClosed() && !client.isInputShutdown() && !client.isOutputShutdown();
	}

	/**
	 * Disconnect client socket
	 */
	@Override
	public void close() throws IOException {
		if (running.compareAndSet(true, false)) {
			if (client != null && !client.isClosed()) {
				try {
					client.close();
				} catch (IOException e) {
					err.println(String.format("Closing client failure", getStackTrace(e)));
				}
			}

			if (_in != null) {
				try {
					_in.close();
				} catch (IOException e) {
					err.println(String.format("Closing data input failure", getStackTrace(e)));
				}
			}

			if (_out != null) {
				try {
					_out.close();
				} catch (IOException e) {
					err.println(String.format("Closing data output failure", getStackTrace(e)));
				}
			}
		}
	}

}
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

import com.wl4g.devops.shell.registry.ShellBeanRegistry;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static java.lang.System.err;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static com.wl4g.devops.tool.common.lang.Assert.*;

/**
 * Shell message handler
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月2日
 * @since
 */
public abstract class ChannelMessageHandler implements Runnable, Closeable {

	/** Begin of file */
	final public static String BOF = "<<EOF>";

	/** End of file */
	final public static String EOF = "EOF";

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
		notNull(client, "Socket client is null, please check configure");
		notNull(function, "Function is null, please check configure");
		notNull(registry, "Registry must not be null");
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
		notNull(message, "Message is null, please check configure");
		if (!isActive()) {
			throw new SocketException("No socket active!");
		}
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
		return client.isConnected() && !client.isClosed();
	}

	/**
	 * Disconnect client socket
	 */
	@Override
	public synchronized void close() throws IOException {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((client == null) ? 0 : client.getRemoteSocketAddress().toString().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChannelMessageHandler other = (ChannelMessageHandler) obj;
		if (client == null) {
			if (other.client != null)
				return false;
		} else if (!client.getRemoteSocketAddress().toString().equals(other.client.getRemoteSocketAddress().toString()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ChannelMessageHandler [client=" + client.getRemoteSocketAddress().toString() + "]";
	}

}
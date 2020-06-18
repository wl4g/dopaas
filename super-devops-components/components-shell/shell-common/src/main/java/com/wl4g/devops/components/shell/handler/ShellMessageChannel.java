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
package com.wl4g.devops.components.shell.handler;

import com.wl4g.devops.components.shell.exception.ChannelShellException;
import com.wl4g.devops.components.shell.registry.ShellHandlerRegistrar;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static com.wl4g.devops.components.tools.common.lang.Assert2.*;
import static java.lang.String.format;
import static java.lang.System.err;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

/**
 * Built-in shell channel message handler
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月2日
 * @since
 */
public abstract class ShellMessageChannel implements Runnable, Closeable {

	/**
	 * Currently running?
	 */
	final protected AtomicBoolean running = new AtomicBoolean(false);

	/**
	 * Local shell component registry.
	 */
	final protected ShellHandlerRegistrar registrar;

	/**
	 * Client socket
	 */
	final protected Socket socket;

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

	public ShellMessageChannel(ShellHandlerRegistrar registrar, Socket socket, Function<String, Object> function) {
		notNull(socket, "Socket client is null, please check configure");
		notNull(function, "Function is null, please check configure");
		notNull(registrar, "Registry must not be null");
		this.registrar = registrar;
		this.socket = socket;
		this.function = function;
		if (running.compareAndSet(false, true)) {
			try {
				this._in = socket.getInputStream();
				this._out = socket.getOutputStream();
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
	public void writeFlush(Object message) throws IOException {
		notNull(message, "Message is null, please check configure");
		if (!isActive()) {
			throw new ChannelShellException("No socket active!");
		}
		ObjectOutputStream out = new ObjectOutputStream(_out);
		out.writeObject(message);
		out.flush();
		_out.flush();
	}

	/**
	 * Is connect active
	 * 
	 * @return
	 */
	public boolean isActive() {
		return socket.isConnected() && !socket.isClosed();
	}

	/**
	 * Disconnect client socket
	 */
	@Override
	public void close() throws IOException {
		if (running.compareAndSet(true, false)) {
			if (socket != null && !socket.isClosed()) {
				try {
					socket.close();
				} catch (IOException e) {
					err.println(format("Closing client failure", getStackTrace(e)));
				}
			}

			if (_in != null) {
				try {
					_in.close();
				} catch (IOException e) {
					err.println(format("Closing data input failure", getStackTrace(e)));
				}
			}

			if (_out != null) {
				try {
					_out.close();
				} catch (IOException e) {
					err.println(format("Closing data output failure", getStackTrace(e)));
				}
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((socket == null) ? 0 : socket.getRemoteSocketAddress().toString().hashCode());
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
		ShellMessageChannel other = (ShellMessageChannel) obj;
		if (socket == null) {
			if (other.socket != null)
				return false;
		} else if (!socket.getRemoteSocketAddress().toString().equals(other.socket.getRemoteSocketAddress().toString()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [client=" + socket.getRemoteSocketAddress().toString() + "]";
	}

}
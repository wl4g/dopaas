package com.wl4g.devops.shell.handler;

import static java.lang.System.err;
import static org.apache.commons.lang3.exception.ExceptionUtils.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import com.wl4g.devops.shell.registry.ShellBeanRegistry;
import com.wl4g.devops.shell.utils.Assert;

/**
 * Shell handler
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月2日
 * @since
 */
public abstract class ChannelMessageHandler implements Runnable {

	/**
	 * Currently running?
	 */
	final protected AtomicBoolean running = new AtomicBoolean(false);

	/**
	 * Local shell componment registry.
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
	 * Inputstream
	 */
	protected InputStream _in;

	/**
	 * Outstream
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
	public void writeAndFlush(Object message) throws IOException {
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
	public void close() {
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
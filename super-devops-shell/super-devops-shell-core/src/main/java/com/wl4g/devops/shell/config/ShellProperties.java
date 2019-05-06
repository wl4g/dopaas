package com.wl4g.devops.shell.config;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.util.Assert;

/**
 * Shell properties configuration
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月1日
 * @since
 */
public class ShellProperties implements Serializable {

	private static final long serialVersionUID = -24798955162679115L;

	/**
	 * Listening server socket port
	 */
	private int port = 14002;

	/**
	 * listening TCP backlog
	 */
	private int backlog = 16;

	/**
	 * Listening server socket bind address
	 */
	private String bindAddr = "127.0.0.1";

	/**
	 * Submission executors
	 */
	private int concurrently = 2;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		Assert.isTrue(port > 1024, String.format("listening port must greater than 1024, actual is %s", port));
		this.port = port;
	}

	public int getBacklog() {
		return backlog;
	}

	public void setBacklog(int backlog) {
		Assert.isTrue(backlog > 0, String.format("backlog must greater than 0, actual is %s", backlog));
		this.backlog = backlog;
	}

	public String getBindAddr() {
		return bindAddr;
	}

	public InetAddress getInetBindAddr() {
		try {
			return InetAddress.getByName(getBindAddr());
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	public void setBindAddr(String bindAddr) {
		Assert.hasText(bindAddr, "binAddr is emtpy, please check configure");
		this.bindAddr = bindAddr;
	}

	public int getConcurrently() {
		return concurrently;
	}

	public void setConcurrently(int executors) {
		Assert.isTrue(executors > 0, String.format("executors must greater than 0, actual is %s", backlog));
		this.concurrently = executors;
	}

}

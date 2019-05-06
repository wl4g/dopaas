package com.wl4g.devops.shell.config;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

import com.wl4g.devops.shell.utils.Assert;

/**
 * Shell properties configuration
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月1日
 * @since
 */
@XmlRootElement(name = "configuration")
public class Configuration implements Serializable {

	final private static long serialVersionUID = -24798955162679115L;

	final public static String DEFAULT_CONFIG = "default-config.xml";

	/**
	 * Listening server socket port
	 */
	private int port = 14002;

	/**
	 * Listening server socket bind address
	 */
	private String server = "127.0.0.1";

	public int getPort() {
		Assert.isTrue(port > 1024, String.format("listening port must greater than 1024, actual is %s", port));
		return port;
	}

	public void setPort(int port) {
		Assert.isTrue(port > 1024, String.format("listening port must greater than 1024, actual is %s", port));
		this.port = port;
	}

	public String getServer() {
		Assert.hasText(server, "server is emtpy, please check configure");
		return server;
	}

	public void setServer(String server) {
		Assert.hasText(server, "server is emtpy, please check configure");
		this.server = server;
	}

	public InetAddress getInetBindAddr() {
		try {
			return InetAddress.getByName(getServer());
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	public static Configuration create() {
		return create(Configuration.class.getClassLoader().getResource(DEFAULT_CONFIG));
	}

	public static Configuration create(URL url) {
		try {
			return Util.read(url, Configuration.class);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	static abstract class Util {

		@SuppressWarnings("unchecked")
		public static <T> T read(URL url, Class<?> clazz) throws Exception {
			try {
				url.openStream();
			} catch (Exception e) {
				throw new IllegalArgumentException(String.format("File path: %s does not exist", url));
			}

			JAXBContext jaxb = JAXBContext.newInstance(clazz);
			Unmarshaller unmarshaller = jaxb.createUnmarshaller();
			return (T) unmarshaller.unmarshal(url);
		}

	}

}

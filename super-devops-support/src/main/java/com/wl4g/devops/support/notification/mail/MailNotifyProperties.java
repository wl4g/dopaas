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
package com.wl4g.devops.support.notification.mail;

import static com.wl4g.devops.components.tools.common.lang.Assert2.hasTextOf;
import static com.wl4g.devops.components.tools.common.lang.Assert2.notNullOf;
import static com.wl4g.devops.components.tools.common.lang.Assert2.stateOf;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.wl4g.devops.support.notification.AbstractNotifyProperties;

/**
 * {@link MailNotifyProperties}, Full compatibility with native spring mail!
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年1月9日 v1.0.0
 * @see {@link org.springframework.boot.autoconfigure.mail.MailProperties}
 */
public class MailNotifyProperties extends AbstractNotifyProperties {
	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	/**
	 * SMTP server host.
	 */
	private String host;

	/**
	 * SMTP server port.
	 */
	private Integer port;

	/**
	 * Login user of the SMTP server.
	 */
	private String username;

	/**
	 * Login password of the SMTP server.
	 */
	private String password;

	/**
	 * Protocol used by the SMTP server.
	 */
	private String protocol = "smtp";

	/**
	 * Default MimeMessage encoding.
	 */
	private Charset defaultEncoding = DEFAULT_CHARSET;

	/**
	 * Additional JavaMail session properties.
	 */
	private Map<String, String> properties = new HashMap<String, String>();

	/**
	 * Session JNDI name. When set, takes precedence to others mail settings.
	 */
	private String jndiName;

	/**
	 * Test that the mail server is available on startup.
	 */
	private boolean testConnection;

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return this.port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getProtocol() {
		return this.protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public Charset getDefaultEncoding() {
		return this.defaultEncoding;
	}

	public void setDefaultEncoding(Charset defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	public Map<String, String> getProperties() {
		return this.properties;
	}

	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	public String getJndiName() {
		return this.jndiName;
	}

	public boolean isTestConnection() {
		return this.testConnection;
	}

	public void setTestConnection(boolean testConnection) {
		this.testConnection = testConnection;
	}

	@Override
	public void validate() {
		hasTextOf(getHost(), "host");
		notNullOf(getPort(), "port");
		stateOf(getPort() > 0, "port must >0");
		hasTextOf(getUsername(), "username");
		hasTextOf(getPassword(), "password");
		hasTextOf(getProtocol(), "protocol");
		notNullOf(getDefaultEncoding(), "defaultEncoding");
	}

}
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
package com.wl4g.devops.scm.client.config;

import static org.apache.commons.lang3.StringUtils.isAnyBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static com.wl4g.devops.tool.common.utils.lang.Exceptions.getRootCausesString;
import static java.net.NetworkInterface.getNetworkInterfaces;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import com.google.common.net.HostAndPort;
import com.wl4g.devops.common.bean.scm.model.GenericInfo.ReleaseInstance;

/**
 * Instance information.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年4月1日
 * @since
 */
public class InstanceHolder {
	final protected Logger log = LoggerFactory.getLogger(getClass());

	/** Application name. */
	final private String appName;

	/** Local application instance. */
	final private ReleaseInstance instance;

	public InstanceHolder(Environment environment, ScmClientProperties config) {
		this.appName = environment.getProperty("spring.application.name");
		String servPort = environment.getProperty("server.port");
		Assert.isTrue(!isAnyBlank(appName, servPort),
				"Failed to obtain application name and server port. Check if bootstrap.yml is configured correctly");

		// Default local host-name.
		String hostname = getLocalHostname();
		/*
		 * Use the specified network card name to correspond to IP.
		 */
		if (!isEmpty(config.getNetcard())) {
			try {
				// First ipv4 network card
				Enumeration<NetworkInterface> nis = getNetworkInterfaces();
				while (nis.hasMoreElements()) {
					NetworkInterface ni = nis.nextElement();
					Enumeration<InetAddress> addresses = ni.getInetAddresses();
					while (addresses.hasMoreElements()) {
						InetAddress addr = addresses.nextElement();
						if (!addr.isLoopbackAddress() && addr.getHostAddress().indexOf(':') == -1) { // Ignore-ipv6
							if (StringUtils.equals(ni.getName(), config.getNetcard())) {
								hostname = addr.getHostName();
								break;
							}
						}
					}
				}
			} catch (Exception e) {
				log.warn("Unable to get hostname of network card '{}', used default for '{}' causes by: {}", config.getNetcard(),
						hostname, getRootCausesString(e));
			}
		}

		// Check & build ReleaseInstance.
		HostAndPort hap = HostAndPort.fromString(hostname + ":" + servPort);
		this.instance = new ReleaseInstance(hap.getHostText(), String.valueOf(hap.getPort()));
	}

	public String getAppName() {
		return appName;
	}

	public ReleaseInstance getInstance() {
		return instance;
	}

	/**
	 * Get default local host-name
	 * 
	 * @return
	 */
	private String getLocalHostname() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			throw new IllegalStateException(e);
		}
	}

}
/*
 * Copyright 2017 ~ 2025 the original author or authors.
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

import static com.wl4g.devops.common.utils.Exceptions.getRootCausesString;
import static org.apache.commons.lang3.StringUtils.isAnyBlank;
import static org.apache.commons.lang3.StringUtils.isEmpty;

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
				"Environment['server.port','spring.application.name'] config is null, Because spring cloud loads bootstrap.yml preferentially, which means that other config files are not loaded at initialization, so configurations other than bootstrap.yml cannot be used at initialization, Therefore, these 3 items must be allocated to bootstrap.yml.");

		// Default host-name (Maybe it's not really needed.)
		String host = getDefaultLocalHost();
		if (!isEmpty(config.getNetcard())) {
			// First ipv4 network card
			Enumeration<NetworkInterface> netInterfaces;
			try {
				netInterfaces = NetworkInterface.getNetworkInterfaces();
				InetAddress addr;
				while (netInterfaces.hasMoreElements()) {
					NetworkInterface ni = netInterfaces.nextElement();
					Enumeration<InetAddress> addresses = ni.getInetAddresses();
					while (addresses.hasMoreElements()) {
						addr = addresses.nextElement();
						if (!addr.isLoopbackAddress() && addr.getHostAddress().indexOf(':') == -1) { // Ignore-ipv6
							if (StringUtils.equals(ni.getName(), config.getNetcard())) {
								host = addr.getHostName();
								break;
							}
						}
					}
				}
			} catch (Exception e) {
				log.warn("Unable to get hostname of network card '{}', used default for '{}' causes by: {}", config.getNetcard(),
						host, getRootCausesString(e));
			}
		}

		// Check
		HostAndPort hap = HostAndPort.fromString(host + ":" + servPort);
		this.instance = new ReleaseInstance(hap.getHostText(), hap.getPort());
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
	private String getDefaultLocalHost() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			throw new IllegalStateException(e);
		}
	}

}
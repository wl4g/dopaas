package com.wl4g.devops.common.utils;

import static com.wl4g.components.common.lang.Assert2.hasTextOf;
import static com.wl4g.components.common.lang.Assert2.isTrueOf;
import static com.wl4g.components.common.lang.Assert2.notNullOf;
import static com.wl4g.components.common.log.SmartLoggerFactory.getLogger;
import static com.wl4g.components.common.serialize.JacksonUtils.toJSONString;
import static java.util.Objects.nonNull;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

import com.wl4g.components.common.log.SmartLogger;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Wither;

/**
 * {@link InetHolder}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-17
 * @since
 */
public class InetHolder {

	protected final SmartLogger log = getLogger(InetHolder.class);

	/** {@link InetProperties} */
	private final InetProperties config;

	public InetHolder(final InetProperties config) {
		notNullOf(config, "inetConfiguration");
		this.config = config;
	}

	/**
	 * Find first non loopback {@link HostInfo}
	 * 
	 * @return
	 */
	public HostInfo getFirstNonLoopbackHostInfo() {
		InetAddress address = lookupFirstNonLoopbackAddress();
		if (nonNull(address)) {
			return convertAddress(address);
		}

		HostInfo hinfo = new HostInfo();
		hinfo.setHostname(config.getDefaultHostname());
		hinfo.setIpAddress(config.getDefaultIpAddress());
		return hinfo;
	}

	/**
	 * Find first non loopback address
	 * 
	 * @return
	 */
	public InetAddress lookupFirstNonLoopbackAddress() {
		InetAddress result = null;
		try {
			int lowest = Integer.MAX_VALUE;
			for (Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces(); nis.hasMoreElements();) {
				NetworkInterface ni = nis.nextElement();
				if (ni.isUp()) {
					log.trace("Testing interface: {}", ni.getDisplayName());
					if (ni.getIndex() < lowest || result == null) {
						lowest = ni.getIndex();
					} else if (result != null) {
						continue;
					}
					if (!isIgnoreInterface(ni.getDisplayName())) {
						for (Enumeration<InetAddress> addrs = ni.getInetAddresses(); addrs.hasMoreElements();) {
							InetAddress address = addrs.nextElement();
							if (address instanceof Inet4Address && !address.isLoopbackAddress() && isPreferredAddress(address)) {
								log.trace("Found non-loopback interface: {}", ni.getDisplayName());
								result = address;
							}
						}
					}
				}
			}
		} catch (IOException ex) {
			log.error("Cannot get first non-loopback address", ex);
		}
		if (nonNull(result)) {
			return result;
		}

		try {
			return InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			log.warn("Unable to retrieve localhost");
		}

		return null;
	}

	/**
	 * Check is preferred address
	 * 
	 * @param address
	 * @return
	 */
	private boolean isPreferredAddress(InetAddress address) {

		if (config.isUseOnlySiteLocalInterfaces()) {
			final boolean siteLocalAddress = address.isSiteLocalAddress();
			if (!siteLocalAddress) {
				log.trace("Ignoring address: {}", address.getHostAddress());
			}
			return siteLocalAddress;
		}
		final List<String> preferredNetworks = config.getPreferredNetworks();
		if (preferredNetworks.isEmpty()) {
			return true;
		}
		for (String regex : preferredNetworks) {
			final String hostAddress = address.getHostAddress();
			if (hostAddress.matches(regex) || hostAddress.startsWith(regex)) {
				return true;
			}
		}
		log.trace("Ignoring address: {}", address.getHostAddress());
		return false;
	}

	/**
	 * Check is ignore inteface
	 * 
	 * @param interfaceName
	 * @return
	 */
	private boolean isIgnoreInterface(String interfaceName) {
		for (String regex : config.getIgnoredInterfaces()) {
			if (interfaceName.matches(regex)) {
				log.trace("Ignoring interface: " + interfaceName);
				return true;
			}
		}
		return false;
	}

	/**
	 * Convert address
	 * 
	 * @param address
	 * @return
	 */
	private HostInfo convertAddress(final InetAddress address) {
		HostInfo hostInfo = new HostInfo();

		ExecutorService executor = newExecutor();
		Future<String> res = executor.submit(address::getHostName);
		String hostname;
		try {
			hostname = res.get(config.getTimeoutMs(), TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			log.info("Cannot determine local hostname");
			hostname = "localhost";
		} finally {
			if (nonNull(executor)) {
				executor.shutdown();
			}
		}
		hostInfo.setHostname(hostname);
		hostInfo.setIpAddress(address.getHostAddress());
		return hostInfo;
	}

	/**
	 * New create executor
	 * 
	 * @return
	 */
	private ExecutorService newExecutor() {
		return Executors.newSingleThreadExecutor(r -> {
			Thread t = new Thread(r);
			t.setName(InetHolder.class.getSimpleName());
			t.setDaemon(true);
			return t;
		});
	}

	/**
	 * Host information pojo.
	 */
	public static class HostInfo {

		/**
		 * Should override the host info.
		 */
		public boolean override;

		private String ipAddress;

		private String hostname;

		public HostInfo(String hostname) {
			this.hostname = hostname;
		}

		public HostInfo() {
		}

		public int getIpAddressAsInt() {
			InetAddress inetAddress = null;
			String host = this.ipAddress;
			if (host == null) {
				host = this.hostname;
			}
			try {
				inetAddress = InetAddress.getByName(host);
			} catch (final UnknownHostException e) {
				throw new IllegalArgumentException(e);
			}
			return ByteBuffer.wrap(inetAddress.getAddress()).getInt();
		}

		public boolean isOverride() {
			return this.override;
		}

		public void setOverride(boolean override) {
			this.override = override;
		}

		public String getIpAddress() {
			return this.ipAddress;
		}

		public void setIpAddress(String ipAddress) {
			this.ipAddress = ipAddress;
		}

		public String getHostname() {
			return this.hostname;
		}

		public void setHostname(String hostname) {
			this.hostname = hostname;
		}

	}

	/**
	 * Properties for {@link InetHolder}.
	 *
	 * @since
	 */
	@Getter
	@Setter
	@Wither
	public static class InetProperties {

		/**
		 * The default hostname. Used in case of errors.
		 */
		private String defaultHostname = "localhost";

		/**
		 * The default IP address. Used in case of errors.
		 */
		private String defaultIpAddress = "127.0.0.1";

		/**
		 * Timeout, in seconds, for calculating hostname.
		 */
		private int timeoutMs = 1_000;

		/**
		 * List of Java regular expressions for network interfaces that will be
		 * ignored.
		 */
		private List<String> ignoredInterfaces = new ArrayList<>();

		/**
		 * Whether to use only interfaces with site local addresses. See
		 * {@link InetAddress#isSiteLocalAddress()} for more details.
		 */
		private boolean useOnlySiteLocalInterfaces = false;

		/**
		 * List of Java regular expressions for network addresses that will be
		 * preferred.
		 */
		private List<String> preferredNetworks = new ArrayList<>();

		public InetProperties() {
			super();
		}

		public InetProperties(String defaultHostname, String defaultIpAddress, int timeoutSeconds, List<String> ignoredInterfaces,
				boolean useOnlySiteLocalInterfaces, List<String> preferredNetworks) {
			hasTextOf(defaultHostname, "defaultHostname");
			hasTextOf(defaultIpAddress, "defaultIpAddress");
			isTrueOf(timeoutSeconds > 0, "timeoutSeconds>0");
			notNullOf(ignoredInterfaces, "ignoredInterfaces");
			notNullOf(preferredNetworks, "preferredNetworks");
			this.defaultHostname = defaultHostname;
			this.defaultIpAddress = defaultIpAddress;
			this.timeoutMs = timeoutSeconds;
			this.ignoredInterfaces = ignoredInterfaces;
			this.useOnlySiteLocalInterfaces = useOnlySiteLocalInterfaces;
			this.preferredNetworks = preferredNetworks;
		}

		@Override
		public String toString() {
			return getClass().getSimpleName().concat(" - ").concat(toJSONString(this));
		}

	}

}

package com.wl4g.devops.gateway.server.config;

/**
 * {@link GatewayRefreshProperties}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-07-23
 * @since
 */
public class GatewayRefreshProperties {

	private Long refreshDelayMs = 5_000L;

	public Long getRefreshDelayMs() {
		return refreshDelayMs;
	}

	public void setRefreshDelayMs(Long refreshDelayMs) {
		this.refreshDelayMs = refreshDelayMs;
	}

}

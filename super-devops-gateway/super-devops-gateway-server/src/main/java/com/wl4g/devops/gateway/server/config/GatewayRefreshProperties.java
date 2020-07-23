package com.wl4g.devops.gateway.server.config;

/**
 * @author vjay
 * @date 2020-07-23 10:53:00
 */
public class GatewayRefreshProperties {

    private Long refreshTimeMs = 10_000l;

    public Long getRefreshTimeMs() {
        return refreshTimeMs;
    }

    public void setRefreshTimeMs(Long refreshTimeMs) {
        this.refreshTimeMs = refreshTimeMs;
    }
}

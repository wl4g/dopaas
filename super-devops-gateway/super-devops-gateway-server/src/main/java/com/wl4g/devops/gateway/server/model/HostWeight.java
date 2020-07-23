package com.wl4g.devops.gateway.server.model;

import java.io.Serializable;

/**
 * @author vjay
 * @date 2020-07-22 19:08:00
 */
public class HostWeight implements Serializable {

    private static final long serialVersionUID = 8940373806493080114L;

    private String uri;

    private int weight = 0;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}

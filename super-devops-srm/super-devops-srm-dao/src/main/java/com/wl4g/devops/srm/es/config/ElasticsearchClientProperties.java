package com.wl4g.devops.srm.es.config;

public class ElasticsearchClientProperties {
	public static final String PREFIX = "spring.es";

	private String[] hosts;
	private int port = 9200;
	private String schema = "http";
	private int connectTimeOut;
	private int socketTimeOut;
	private int connectionRequestTimeOut;
	private int maxConnectNum;
	private int maxConnectPerRoute;

	public String[] getHosts() {
		return hosts;
	}

	public void setHosts(String[] hosts) {
		this.hosts = hosts;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public int getConnectTimeOut() {
		return connectTimeOut;
	}

	public void setConnectTimeOut(int connectTimeOut) {
		this.connectTimeOut = connectTimeOut;
	}

	public int getSocketTimeOut() {
		return socketTimeOut;
	}

	public void setSocketTimeOut(int socketTimeOut) {
		this.socketTimeOut = socketTimeOut;
	}

	public int getConnectionRequestTimeOut() {
		return connectionRequestTimeOut;
	}

	public void setConnectionRequestTimeOut(int connectionRequestTimeOut) {
		this.connectionRequestTimeOut = connectionRequestTimeOut;
	}

	public int getMaxConnectNum() {
		return maxConnectNum;
	}

	public void setMaxConnectNum(int maxConnectNum) {
		this.maxConnectNum = maxConnectNum;
	}

	public int getMaxConnectPerRoute() {
		return maxConnectPerRoute;
	}

	public void setMaxConnectPerRoute(int maxConnectPerRoute) {
		this.maxConnectPerRoute = maxConnectPerRoute;
	}

}

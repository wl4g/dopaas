package com.wl4g.devops.umc.config;

import java.io.Serializable;

public class StoreProperties implements Serializable {

	private static final long serialVersionUID = -8652479436330234847L;

	private OpentsdbProproties opentsdb = new OpentsdbProproties();

	public OpentsdbProproties getOpentsdb() {
		return opentsdb;
	}

	public void setOpentsdb(OpentsdbProproties opentsdb) {
		this.opentsdb = opentsdb;
	}

	public static class OpentsdbProproties {

		private String host = "127.0.0.1";

		private int port = 14242;

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

	}

}

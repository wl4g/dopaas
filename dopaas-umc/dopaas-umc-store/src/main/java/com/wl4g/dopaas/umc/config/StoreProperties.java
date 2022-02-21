/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.umc.config;

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
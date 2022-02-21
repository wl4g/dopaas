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
package com.wl4g.dopaas.umc.opentsdb.client.http;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wl4g.dopaas.umc.opentsdb.client.OpenTSDBConfig;

/**
 * @Description:
 * @Author: jinyao
 * @CreateDate: 2019/2/22 下午1:29
 * @Version: 1.0
 */

public class HttpClientFactory {

	final private static Logger log = LoggerFactory.getLogger(HttpClientFactory.class);

	private static final AtomicInteger NUM = new AtomicInteger();

	/***
	 * 创建httpclient
	 * 
	 * @param config
	 *            配置文件
	 * @return
	 * @throws IOReactorException
	 */
	public static HttpClient createHttpClient(OpenTSDBConfig config) throws IOReactorException {
		Objects.requireNonNull(config);

		ConnectingIOReactor ioReactor = initIOReactorConfig();
		PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(ioReactor);

		RequestConfig requestConfig = initRequestConfig(config);
		CloseableHttpAsyncClient httpAsyncClient = createPoolingHttpClient(requestConfig, connManager, config);

		return new HttpClient(config, httpAsyncClient, initFixedCycleCloseConnection(connManager));
	}

	/***
	 * 创建CPU核数的IO线程
	 * 
	 * @return
	 * @throws IOReactorException
	 */
	private static ConnectingIOReactor initIOReactorConfig() throws IOReactorException {
		IOReactorConfig ioReactorConfig = IOReactorConfig.custom().setIoThreadCount(Runtime.getRuntime().availableProcessors())
				.build();
		ConnectingIOReactor ioReactor;
		ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
		return ioReactor;
	}

	/***
	 * 设置超时时间
	 * 
	 * @return
	 */
	private static RequestConfig initRequestConfig(OpenTSDBConfig config) {
		return RequestConfig.custom()
				// ConnectTimeout:连接超时.连接建立时间，三次握手完成时间.
				.setConnectTimeout(config.getHttpConnectTimeout() * 1000)
				// SocketTimeout:Socket请求超时.数据传输过程中数据包之间间隔的最大时间.
				.setSocketTimeout(config.getHttpConnectTimeout() * 1000)
				// ConnectionRequestTimeout:httpclient使用连接池来管理连接，这个时间就是从连接池获取连接的超时时间
				.setConnectionRequestTimeout(config.getHttpConnectTimeout() * 1000).build();
	}

	private static ConnectionKeepAliveStrategy myStrategy() {
		ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
			@Override
			public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
				HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
				while (it.hasNext()) {
					HeaderElement he = it.nextElement();
					String param = he.getName();
					String value = he.getValue();
					if (value != null && param.equalsIgnoreCase("timeout")) {
						return Long.parseLong(value) * 1000;
					}
				}
				return 60 * 1000;// 如果没有约定，则默认定义时长为60s
			}
		};
		return myStrategy;
	}

	/***
	 * 创建client
	 * 
	 * @param config
	 *            查询对象
	 * @param cm
	 *            连接池管理
	 * @param openTSDBConfig
	 * @return
	 */
	private static CloseableHttpAsyncClient createPoolingHttpClient(RequestConfig config, PoolingNHttpClientConnectionManager cm,
			OpenTSDBConfig openTSDBConfig) {
		cm.setMaxTotal(100);
		cm.setDefaultMaxPerRoute(100);

		HttpAsyncClientBuilder httpAsyncClientBuilder = HttpAsyncClients.custom().setConnectionManager(cm)
				.setDefaultRequestConfig(config);
		// 如果不是只读，则设置为长连接
		if (!openTSDBConfig.isReadonly()) {
			httpAsyncClientBuilder.setKeepAliveStrategy(myStrategy());
		}
		CloseableHttpAsyncClient client = httpAsyncClientBuilder.build();
		return client;
	}

	/***
	 * 创建定时任务线程池
	 * 
	 * @param cm
	 *            连接池管理
	 * @return
	 */
	private static ScheduledExecutorService initFixedCycleCloseConnection(final PoolingNHttpClientConnectionManager cm) {
		// 通过工厂方法创建线程
		ScheduledExecutorService connectionGcService = Executors.newSingleThreadScheduledExecutor((r) -> {
			Thread t = new Thread(r, "Fixed-Cycle-Close-Connection-" + NUM.incrementAndGet());
			t.setDaemon(true);
			return t;
		});

		// 定时关闭所有空闲链接
		connectionGcService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					log.debug("Close idle connections, fixed cycle operation");
					// 关闭30秒内不活动的链接
					cm.closeExpiredConnections();
					cm.closeIdleConnections(30, TimeUnit.SECONDS);
				} catch (Exception ex) {
					log.error("", ex);
				}
			}
		}, 30, 30, TimeUnit.SECONDS);
		return connectionGcService;
	}

	public static class OpenTSDBConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy {

		private long time;

		public OpenTSDBConnectionKeepAliveStrategy(long time) {
			super();
			this.time = time;
		}

		@Override
		public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
			return 1000 * time;
		}

	}

	public static class OpenTSDBConnectionReuseStrategy implements ConnectionReuseStrategy {

		@Override
		public boolean keepAlive(HttpResponse response, HttpContext context) {
			return false;
		}

	}

}
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
package com.wl4g.devops.umc.config;

import com.wl4g.devops.umc.opentsdb.client.OpenTSDBClient;
import com.wl4g.devops.umc.opentsdb.client.bean.request.Point;
import com.wl4g.devops.umc.opentsdb.client.bean.response.DetailResult;
import com.wl4g.devops.umc.opentsdb.client.http.callback.BatchPutHttpResponseCallback.BatchPutCallBack;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.List;

import static com.wl4g.devops.umc.opentsdb.client.OpenTSDBClientFactory.connect;
import static com.wl4g.devops.umc.opentsdb.client.OpenTSDBConfig.address;

/**
 * OpenTSDB client factory bean
 * 
 * @author wangl.sir
 * @version v1.0 2019年6月17日
 * @since
 */
public class OpenTsdbFactoryBean implements InitializingBean, DisposableBean, FactoryBean<OpenTSDBClient> {

	private StoreProperties config;

	private OpenTSDBClient client;

	public OpenTsdbFactoryBean(StoreProperties config) {
		Assert.notNull(config, "StoreProperties must not be null");
		this.config = config;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		client = connect(address(config.getOpentsdb().getHost(), config.getOpentsdb().getPort())
				// http连接池大小，默认100
				.httpConnectionPool(100)
				// http请求超时时间，默认100s
				.httpConnectTimeout(100)
				// 异步写入数据时，每次http提交的数据条数，默认50
				.batchPutSize(500)
				// 异步写入数据中，内部有一个队列，默认队列大小20000
				.batchPutBufferSize(20000)
				// 异步写入等待时间，如果距离上一次请求超多300ms，且有数据，则直接提交
				.batchPutTimeLimit(3000)
				// 当确认这个client只用于查询时设置，可不创建内部队列从而提高效率
				// .readonly()
				// 每批数据提交完成后回调
				.batchPutCallBack(new BatchPutCallBack() {
					@Override
					public void response(List<Point> points, DetailResult result) {
						// 在请求完成并且response code成功时回调
					}

					@Override
					public void responseError(List<Point> points, DetailResult result) {
						// 在response code失败时回调
					}

					@Override
					public void failed(List<Point> points, Exception e) {
						// 在发生错误是回调
					}
				}).config());
	}

	@Override
	public void destroy() throws Exception {
		try {
			// 优雅关闭连接，会等待所有异步操作完成
			client.gracefulClose();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public OpenTSDBClient getObject() throws Exception {
		Assert.notNull(client, "Can not init the opentsdb client");
		return client;
	}

	@Override
	public Class<?> getObjectType() {
		return OpenTSDBClient.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public static void main(String[] args) throws Exception {
		StoreProperties config = new StoreProperties();
		config.getOpentsdb().setHost("127.0.0.1");
		config.getOpentsdb().setPort(14242);
		OpenTsdbFactoryBean cfr = new OpenTsdbFactoryBean(config);
		cfr.afterPropertiesSet();
		long timestamp = System.currentTimeMillis();
		Point point = Point.metric("test1").tag("testTag", "test").value(timestamp, 1.0).build();
		cfr.getObject().put(point);
		cfr.destroy();
	}

}
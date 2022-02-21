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
package com.wl4g.dopaas.umc.opentsdb.client.sender.producer;

import com.wl4g.dopaas.umc.opentsdb.client.bean.request.Point;

/**
 * 生产者接口
 *
 * @Author: jinyao
 * @Description:
 * @CreateDate: 2019/2/23 下午4:07
 * @Version: 1.0
 */
public interface Producer {

	/***
	 * 写入队列
	 * 
	 * @param point
	 *            数据点
	 */
	void send(Point point);

	/***
	 * 关闭写入
	 */
	void forbiddenSend();

}
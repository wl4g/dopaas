/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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
package com.wl4g.devops.umc.opentsdb.client.sender.consumer;

/**
 * 消费者接口
 *
 * @Author: jinyao
 * @Description:
 * @CreateDate: 2019/2/23 下午4:07
 * @Version: 1.0
 */
public interface Consumer {

	/***
	 * 开始消费，启动线程池中的消费线程
	 */
	void start();

	/***
	 * 停止消费，会等待线程池中的任务完成
	 */
	void gracefulStop();

	/***
	 * 强制停止
	 */
	void forceStop();

}
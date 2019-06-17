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

package com.wl4g.devops.umc.opentsdb.client.sender.producer;

import com.wl4g.devops.umc.opentsdb.client.bean.request.Point;

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

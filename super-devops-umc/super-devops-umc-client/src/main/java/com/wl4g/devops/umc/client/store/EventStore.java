package com.wl4g.devops.umc.client.store;

/**
 * Event store
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年6月7日
 * @since
 */
public interface EventStore<T> {

	void save(T t);

	T largest();

	T least();

	T latest();

	long average();

}

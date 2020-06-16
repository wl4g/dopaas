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
package com.wl4g.devops.dguid.worker;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Redis编号分配器
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年2月10日
 * @since
 */
public class RedisWorkerIdAssigner extends AbstractIntervalWorkerId {

	/**
	 * redis上uid 机器节点的key前缀
	 */
	public static final String UID_ROOT = "dguid:";

	/**
	 * uid 机器节点列表
	 */
	public static final String UID_FOREVER = UID_ROOT.concat("forever");

	/**
	 * uid 活跃节点心跳列表(用于保存活跃节点及活跃心跳)
	 */
	public static final String UID_TEMPORARY = UID_ROOT.concat("temporary:");

	@Autowired
	private RedisTemplate<Object, Object> redisTemplate;

	@Override
	public long action() {
		/**
		 * 1、文件不存在，检查redis上是否存在ip:port的机器节点
		 */
		Set<Object> uidWork = redisTemplate.opsForZSet().range(UID_FOREVER, 0, -1);
		if (null == workerId) {
			// a、 检查redis上是否存在ip:port的节点,存在，获取节点的顺序编号
			Long i = 0L;
			for (Object item : uidWork) {
				i++;
				if (item.toString().equals(pidName)) {
					workerId = i;
					break;
				}
			}
			// b、 不存在，创建ip:port节点
			if (null == workerId) {
				workerId = (long) uidWork.size();
				// 使用zset 时间排序，保证有序性
				redisTemplate.opsForZSet().add(UID_FOREVER, pidName, System.currentTimeMillis());
				uidWork.add(pidName);
			}
		}
		/**
		 * 2、创建临时机器节点的时间
		 */
		redisTemplate.opsForValue().set(UID_TEMPORARY + pidName, System.currentTimeMillis(), interval * 3, TimeUnit.MILLISECONDS);
		active.set(true);

		/**
		 * 3、获取本地时间，跟uid 活跃节点心跳列表的时间平均值做比较(uid 活跃节点心跳列表
		 * 用于存储活跃节点的上报时间，每隔一段时间上报一次临时节点时间)
		 */
		Long sumTime = 0L;
		if (null != uidWork && uidWork.size() > 0) {
			for (Object itemName : uidWork) {
				Object itemTime = redisTemplate.opsForValue().get(UID_TEMPORARY + itemName);
				sumTime += null == itemTime ? 0 : Long.valueOf(itemTime.toString());
			}
			return sumTime / uidWork.size();
		}
		return 0;
	}

	@Override
	public boolean where() {
		return null != workerId;
	}

	@Override
	public void report() {
		redisTemplate.opsForValue().set(UID_TEMPORARY + pidName, System.currentTimeMillis(), interval * 3, TimeUnit.MILLISECONDS);
	}
}
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
package com.wl4g.devops.dguid.strategy;

/**
 * uid策略接口
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年2月10日
 * @since
 */
public interface IUidStrategy {

	/**
	 * @方法名称 getUID
	 * @功能描述
	 * 
	 *       <pre>
	 *       获取ID
	 *       </pre>
	 * 
	 * @param group
	 *            分组
	 * @return id
	 */
	public long getUID(String group);

	/**
	 * @方法名称 parseUID
	 * @功能描述
	 * 
	 *       <pre>
	 *       解析ID
	 *       </pre>
	 * 
	 * @param uid
	 * @param group
	 *            分组
	 * @return 输出json字符串：{\"UID\":\"\",\"timestamp\":\"\",\"workerId\":\"\",\"dataCenterId\":\"\",\"sequence\":\"\"}
	 */
	String parseUID(long uid, String group);
}
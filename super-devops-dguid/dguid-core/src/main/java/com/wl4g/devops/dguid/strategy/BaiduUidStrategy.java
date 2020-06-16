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

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.util.StringUtils;

import com.wl4g.devops.dguid.baidu.UidGenerator;

/**
 * Baidu uid生成策略
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年2月10日
 * @since
 */
public class BaiduUidStrategy implements IUidStrategy {

	private static Map<String, UidGenerator> generatorMap = new HashMap<>();

	@Autowired
	private UidGenerator uidGenerator;

	/**
	 * 获取uid生成器
	 * 
	 * @方法名称 getUidGenerator
	 * @功能描述
	 * 
	 *       <pre>
	 *       获取uid生成器
	 *       </pre>
	 * 
	 * @param prefix
	 *            前缀
	 * @return uid生成器
	 */
	public UidGenerator getUidGenerator(String prefix) {
		if (StringUtils.isEmpty(prefix)) {
			return uidGenerator;
		}
		UidGenerator generator = generatorMap.get(prefix);
		if (null == generator) {
			synchronized (generatorMap) {
				if (null == generator) {
					generator = getGenerator();
				}
				generatorMap.put(prefix, generator);
			}
		}
		return generator;
	}

	@Override
	public long getUID(String group) {
		return getUidGenerator(group).getUID();
	}

	@Override
	public String parseUID(long uid, String group) {
		return getUidGenerator(group).parseUID(uid);
	}

	/**
	 * @方法名称 getGenerator
	 * @功能描述
	 * 
	 *       <pre>
	 * 多实例返回uidGenerator(返回值不重要，动态注入)
	 *       </pre>
	 * 
	 * @return
	 */
	@Lookup
	public UidGenerator getGenerator() {
		return null;
	}

	public UidGenerator getUidGenerator() {
		return uidGenerator;
	}

	public void setUidGenerator(UidGenerator uidGenerator) {
		this.uidGenerator = uidGenerator;
	}
}
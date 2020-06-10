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
package com.wl4g.devops.guid.extend.strategy;

import com.wl4g.devops.guid.extend.annotation.UidModel;
import com.wl4g.devops.guid.leaf.ISegmentService;
import com.wl4g.devops.guid.spring.ColumnMaxValueIncrementer;

/**
 * @类名称 SpringStrategy.java
 * @类描述
 * 
 *      <pre>
 * spring 分段批量Id策略(可配置asynLoadingSegment-异步标识)
 *      </pre>
 * 
 * @作者 庄梦蝶殇 linhuaichuan1989@126.com
 * @创建时间 2019年3月15日 下午7:48:58
 * @版本 1.0.0
 *
 * @修改记录
 * 
 *       <pre>
 *     版本                       修改人 		修改日期 		 修改内容描述
 *     ----------------------------------------------
 *     1.0.0 	       庄梦蝶殇 	2019年3月15日             
 *     ----------------------------------------------
 *       </pre>
 */
public class SpringStrategy extends LeafSegmentStrategy {
	@Override
	public UidModel getName() {
		return UidModel.step;
	}

	@Override
	public ISegmentService getSegmentService(String prefix) {
		ISegmentService segmentService = generatorMap.get(prefix);
		if (null == segmentService) {
			synchronized (generatorMap) {
				if (null == segmentService) {
					segmentService = new ColumnMaxValueIncrementer(jdbcTemplate, prefix);
				}
				generatorMap.put(prefix, segmentService);
			}
		}
		return segmentService;
	}

}
/*
 * Copyright (c) 2019 Dianping, Inc. All Rights Reserve.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.guid.leaf;

/**
 * @类名称 IdLeafService.java
 * @类描述
 * 
 *      <pre>
 *      Leaf理论的分段批量id生成服务
 * 		</pre>
 * 
 * @作者 庄梦蝶殇 linhuaichuan1989@126.com
 * @创建时间 2018年9月5日 上午11:46:37
 * @版本 1.00
 *
 * @修改记录
 * 
 *       <pre>
 *     版本                       修改人 		修改日期 		 修改内容描述
 *     ----------------------------------------------
 *     1.00 	庄梦蝶殇 	2018年9月5日             
 *     ----------------------------------------------
 *       </pre>
 */
public interface ISegmentService {
	/**
	 * 获取id
	 * 
	 * @return id
	 */
	public Long getId();

	/**
	 * 设置业务标识
	 * 
	 * @param bizTag
	 *            业务标识
	 */
	public void setBizTag(String bizTag);
}

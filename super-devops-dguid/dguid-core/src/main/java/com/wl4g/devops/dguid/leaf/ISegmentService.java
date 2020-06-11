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
package com.wl4g.devops.dguid.leaf;

/**
 * Leaf理论的分段批量id生成服务
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年2月10日
 * @since
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

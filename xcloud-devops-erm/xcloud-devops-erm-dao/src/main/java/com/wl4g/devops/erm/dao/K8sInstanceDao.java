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
package com.wl4g.devops.erm.dao;

import org.apache.ibatis.annotations.Param;

import com.wl4g.devops.common.bean.erm.K8sInstance;

import java.util.List;

public interface K8sInstanceDao {
	int deleteByPrimaryKey(Long id);

	int deleteByK8sId(Long k8sId);

	int insert(K8sInstance record);

	int insertSelective(K8sInstance record);

	K8sInstance selectByPrimaryKey(Long id);

	List<Long> selectHostIdByK8sId(Long k8sId);

	int updateByPrimaryKeySelective(K8sInstance record);

	int updateByPrimaryKey(K8sInstance record);

	int insertBatch(@Param("k8sInstances") List<K8sInstance> k8sInstances);
}
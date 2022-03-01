/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.uci.data;

import org.apache.ibatis.annotations.Param;

import com.wl4g.dopaas.common.bean.uci.PipeStageBuildingProject;

import java.util.List;

public interface PipeStageBuildingProjectDao {
	int deleteByPrimaryKey(Long id);

	int deleteByBuildingId(Long buildingId);

	int deleteByPipeId(Long pipeId);

	int insert(PipeStageBuildingProject record);

	int insertSelective(PipeStageBuildingProject record);

	PipeStageBuildingProject selectByPrimaryKey(Long id);

	List<PipeStageBuildingProject> selectByBuildingId(Long buildingId);

	List<PipeStageBuildingProject> selectByPipeId(Long pipeId);

	int updateByPrimaryKeySelective(PipeStageBuildingProject record);

	int updateByPrimaryKey(PipeStageBuildingProject record);

	int insertBatch(@Param("pipeStepBuildingProjects") List<PipeStageBuildingProject> pipeStepBuildingProjects);

}
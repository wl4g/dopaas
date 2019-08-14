/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.TaskHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskHistoryDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TaskHistory record);

    int insertSelective(TaskHistory record);

    TaskHistory selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TaskHistory record);

    int updateByPrimaryKeyWithBLOBs(TaskHistory record);

    int updateByPrimaryKey(TaskHistory record);

    List<TaskHistory> list(@Param("groupName") String groupName, @Param("projectName") String projectName, @Param("branchName") String branchName);

    int updateStatus(Integer time);
}
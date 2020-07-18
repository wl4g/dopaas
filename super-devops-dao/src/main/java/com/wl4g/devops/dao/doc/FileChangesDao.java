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
package com.wl4g.devops.dao.doc;

import com.wl4g.devops.common.bean.doc.FileChanges;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FileChangesDao {
    int deleteByPrimaryKey(Integer id);

    int insert(FileChanges record);

    int insertSelective(FileChanges record);

    FileChanges selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FileChanges record);

    int updateByPrimaryKey(FileChanges record);

    List<FileChanges> list(@Param("name")String name, @Param("lang")String lang,@Param("labelId")Integer labelId);

    List<FileChanges> selectByDocCode(String fileCode);

    FileChanges selectLastByDocCode(String fileCode);

    void updateIsLatest(String fileCode);
}
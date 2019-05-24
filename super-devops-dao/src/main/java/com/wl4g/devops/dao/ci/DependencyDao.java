package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.Dependency;

import java.util.List;

public interface DependencyDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Dependency record);

    int insertSelective(Dependency record);

    Dependency selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Dependency record);

    int updateByPrimaryKey(Dependency record);

    List<Dependency> getParentsByProjectId(Integer projectId);


}
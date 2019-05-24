package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.scm.CustomPage;

import java.util.List;

public interface ProjectDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Project record);

    int insertSelective(Project record);

    Project selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Project record);

    int updateByPrimaryKey(Project record);

    List<Project> list(CustomPage customPage);

    Project getByProjectName(String projectName);

    Project getByAppGroupId(Integer appGrouPId);

}
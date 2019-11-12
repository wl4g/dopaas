package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.Vcs;

import java.util.List;

public interface VcsDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Vcs record);

    int insertSelective(Vcs record);

    Vcs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Vcs record);

    int updateByPrimaryKey(Vcs record);

    List<Vcs> list();
}
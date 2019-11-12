package com.wl4g.devops.dao.ci;

import com.github.pagehelper.Page;
import com.wl4g.devops.common.bean.ci.Vcs;

public interface VcsDao {
	int deleteByPrimaryKey(Integer id);

	int insert(Vcs record);

	int insertSelective(Vcs record);

	Vcs selectByPrimaryKey(Integer id);

	int updateByPrimaryKeySelective(Vcs record);

	int updateByPrimaryKey(Vcs record);

	Page<Vcs> list();

}
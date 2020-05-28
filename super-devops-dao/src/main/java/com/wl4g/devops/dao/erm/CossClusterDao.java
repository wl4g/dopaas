package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.CossCluster;

public interface CossClusterDao {
    int deleteByPrimaryKey(Integer id);

    int insert(CossCluster record);

    int insertSelective(CossCluster record);

    CossCluster selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CossCluster record);

    int updateByPrimaryKey(CossCluster record);
}
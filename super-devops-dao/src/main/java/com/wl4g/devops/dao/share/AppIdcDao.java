package com.wl4g.devops.dao.share;

import com.wl4g.devops.common.bean.share.AppIdc;

public interface AppIdcDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AppIdc record);

    int insertSelective(AppIdc record);

    AppIdc selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AppIdc record);

    int updateByPrimaryKey(AppIdc record);
}
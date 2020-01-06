package com.wl4g.devops.dao.ci;

import com.github.pagehelper.Page;
import com.wl4g.devops.common.bean.ci.Pcm;
import org.apache.ibatis.annotations.Param;

public interface PcmDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Pcm record);

    int insertSelective(Pcm record);

    Pcm selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Pcm record);

    int updateByPrimaryKey(Pcm record);

    Page<Pcm> list(@Param("name")String name, @Param("providerKind")String providerKind, @Param("authType")Integer authType);
}
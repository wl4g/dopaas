package com.wl4g.devops.dao.share;

import com.wl4g.devops.common.bean.share.Dict;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DictDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Dict record);

    int insertSelective(Dict record);

    Dict selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Dict record);

    int updateByPrimaryKey(Dict record);

    List<Dict> selectByType(@Param("type") String type);

}
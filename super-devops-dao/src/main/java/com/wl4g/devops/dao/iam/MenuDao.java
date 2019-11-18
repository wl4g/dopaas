package com.wl4g.devops.dao.iam;

import com.wl4g.devops.common.bean.iam.Menu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MenuDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Menu record);

    int insertSelective(Menu record);

    Menu selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Menu record);

    int updateByPrimaryKey(Menu record);

    List<Menu> selectByParentId(Integer parentId);

    List<Menu> selectByUserId(@Param("userId") Integer userId);

    List<Menu> selectByUserIdAccessGroup(Integer userId);

    List<Menu> selectByRoot();

}
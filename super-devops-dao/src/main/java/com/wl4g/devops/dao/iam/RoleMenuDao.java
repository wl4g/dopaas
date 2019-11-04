package com.wl4g.devops.dao.iam;

import com.wl4g.devops.common.bean.iam.RoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMenuDao {
    int deleteByPrimaryKey(Integer id);

    int deleteByRoleId(Integer id);

    int insert(RoleMenu record);

    int insertSelective(RoleMenu record);

    RoleMenu selectByPrimaryKey(Integer id);

    List<Integer> selectMenuIdByRoleId(Integer id);

    int updateByPrimaryKeySelective(RoleMenu record);

    int updateByPrimaryKey(RoleMenu record);

    int insertBatch(@Param("roleMenus") List<RoleMenu> roleMenus);

}
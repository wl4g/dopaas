package com.wl4g.devops.dao.iam;

import com.wl4g.devops.common.bean.iam.GroupMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GroupMenuDao {
    int deleteByPrimaryKey(Integer id);

    int deleteByGroupId(Integer groupId);

    int insert(GroupMenu record);

    int insertBatch(@Param("groupMenus") List<GroupMenu> groupMenus);

    int insertSelective(GroupMenu record);

    GroupMenu selectByPrimaryKey(Integer id);

    List<Integer> selectMenuIdsByGroupId(Integer groupId);

    int updateByPrimaryKeySelective(GroupMenu record);

    int updateByPrimaryKey(GroupMenu record);
}
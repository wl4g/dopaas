package com.wl4g.devops.dao.iam;

import com.wl4g.devops.common.bean.iam.GroupRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GroupRoleDao {
    int deleteByPrimaryKey(Integer id);

    int deleteByRoleId(Integer roleId);

    int deleteByGroupId(Integer groupId);

    int insert(GroupRole record);

    int insertSelective(GroupRole record);

    int insertBatch(@Param("groupRoles") List<GroupRole> groupRoles);

    GroupRole selectByPrimaryKey(Integer id);

    List<Integer> selectGroupIdByRoleId(Integer roleId);

    List<Integer> selectRoleIdsByGroupId(Integer groupId);

    int updateByPrimaryKeySelective(GroupRole record);

    int updateByPrimaryKey(GroupRole record);
}
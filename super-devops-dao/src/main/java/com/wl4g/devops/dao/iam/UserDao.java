package com.wl4g.devops.dao.iam;

import com.wl4g.devops.common.bean.iam.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDao {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    List<User> list(@Param("userId") Integer userId,@Param("userName") String userName,@Param("displayName") String displayName);

    User selectByUserName(String userName);

    User selectByUnionIdOrOpenId(@Param("unionId")String unionId,@Param("openId")String openId);

}
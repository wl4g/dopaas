package com.wl4g.devops.dao.umc;

import com.wl4g.devops.common.bean.umc.AlarmContact;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AlarmContactDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AlarmContact record);

    int insertSelective(AlarmContact record);

    AlarmContact selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AlarmContact record);

    int updateByPrimaryKey(AlarmContact record);

    List<AlarmContact> list(@Param("name") String name,@Param("email") String email, @Param("phone") String phone);

    List<AlarmContact> getContactByGroupIds(@Param("groupIds") List<Integer> groupIds);


}
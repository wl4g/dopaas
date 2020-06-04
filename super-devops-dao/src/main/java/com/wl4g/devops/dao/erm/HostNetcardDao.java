package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.Host;
import com.wl4g.devops.common.bean.erm.HostNetcard;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HostNetcardDao {
    int deleteByPrimaryKey(Integer id);

    int insert(HostNetcard record);

    int insertSelective(HostNetcard record);

    HostNetcard selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(HostNetcard record);

    int updateByPrimaryKey(HostNetcard record);

    List<Host> list(@Param("organizationCodes")List<String> organizationCodes, @Param("hostId") Integer hostId, @Param("name") String name);
}
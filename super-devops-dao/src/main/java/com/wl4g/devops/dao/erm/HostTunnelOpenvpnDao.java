package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.HostTunnelOpenvpn;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HostTunnelOpenvpnDao {
    int deleteByPrimaryKey(Integer id);

    int insert(HostTunnelOpenvpn record);

    int insertSelective(HostTunnelOpenvpn record);

    HostTunnelOpenvpn selectByPrimaryKey(Integer id);

    List<HostTunnelOpenvpn> selectAll(@Param("organizationCodes")List<String> organizationCodes);

    int updateByPrimaryKeySelective(HostTunnelOpenvpn record);

    int updateByPrimaryKey(HostTunnelOpenvpn record);
}
package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.HostTunnelOpenvpn;

public interface HostTunnelOpenvpnMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(HostTunnelOpenvpn record);

    int insertSelective(HostTunnelOpenvpn record);

    HostTunnelOpenvpn selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(HostTunnelOpenvpn record);

    int updateByPrimaryKey(HostTunnelOpenvpn record);
}
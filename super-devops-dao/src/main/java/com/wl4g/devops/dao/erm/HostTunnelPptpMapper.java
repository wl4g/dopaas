package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.HostTunnelPptp;

public interface HostTunnelPptpMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(HostTunnelPptp record);

    int insertSelective(HostTunnelPptp record);

    HostTunnelPptp selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(HostTunnelPptp record);

    int updateByPrimaryKey(HostTunnelPptp record);
}
package com.wl4g.devops.dao.erm;

import com.wl4g.devops.common.bean.erm.HostTunnelPptp;

import java.util.List;

public interface HostTunnelPptpDao {
    int deleteByPrimaryKey(Integer id);

    int insert(HostTunnelPptp record);

    int insertSelective(HostTunnelPptp record);

    HostTunnelPptp selectByPrimaryKey(Integer id);

    List<HostTunnelPptp> selectAll();

    int updateByPrimaryKeySelective(HostTunnelPptp record);

    int updateByPrimaryKey(HostTunnelPptp record);
}
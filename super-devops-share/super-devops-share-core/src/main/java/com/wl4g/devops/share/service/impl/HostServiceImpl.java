package com.wl4g.devops.share.service.impl;

import com.wl4g.devops.common.bean.share.AppHost;
import com.wl4g.devops.dao.share.AppHostDao;
import com.wl4g.devops.share.service.HostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author vjay
 * @date 2019-11-14 14:10:00
 */
@Service
public class HostServiceImpl implements HostService {

    @Autowired
    private AppHostDao appHostDao;

    @Override
    public List<AppHost> list(String name, String hostname, Integer idcId) {
        List<AppHost> list = appHostDao.list(name, hostname, idcId);
        return list;
    }
}

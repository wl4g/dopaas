package com.wl4g.devops.iam.service.impl;

import com.wl4g.devops.common.bean.share.ClusterConfig;
import com.wl4g.devops.dao.share.ClusterConfigDao;
import com.wl4g.devops.iam.service.ClusterConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vjay
 * @date 2019-11-14 11:47:00
 */
@Service
public class ClusterConfigServiceImpl implements ClusterConfigService {

    @Value("${spring.profiles.active}")
    private String profile;

    @Autowired
    private ClusterConfigDao clusterConfigDao;

    @Override
    public Map<String, Object> info() {
        List<ClusterConfig> list = clusterConfigDao.getByAppNames(null, profile, null);
        Assert.notEmpty(list,"not found cluster config info , Please Check your db , table = 'app_cluster_config'");
        Map<String, Object> map = new HashMap<>();
        for (ClusterConfig entryAddress : list) {
            map.put(entryAddress.getName(), entryAddress);
        }
        return map;
    }
}

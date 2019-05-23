package com.wl4g.devops.ci.service;

import com.wl4g.devops.common.bean.ci.Dependency;

/**
 * @author vjay
 * @date 2019-05-22 11:33:00
 */
public interface DependencyService {

    public void build(Dependency dependency, String branch) throws Exception;

}

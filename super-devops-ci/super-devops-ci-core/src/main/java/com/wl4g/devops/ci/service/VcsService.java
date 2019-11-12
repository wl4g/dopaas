package com.wl4g.devops.ci.service;

import com.wl4g.devops.common.bean.PageModel;
import com.wl4g.devops.common.bean.ci.Vcs;

import java.util.Map;

/**
 * @author vjay
 * @date 2019-11-12 11:05:00
 */
public interface VcsService {

    Map<String,Object> list(PageModel pm);

    void save(Vcs vcs);

    void del(Integer id);

    Vcs detail(Integer id);


}

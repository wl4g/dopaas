package com.wl4g.devops.doc.service;

import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.page.PageModel;

/**
 * @author vjay
 * @date 2020-02-19 16:22:00
 */
public interface ShareService {

    PageModel list(PageModel pm);

    void cancelShare(Integer id);

    RespBase<?> rendering(String code, String passwd);

}

package com.wl4g.devops.ci.web;

import com.wl4g.devops.ci.service.AnalysisHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.page.PageModel;

/**
 * @author vjay
 * @date 2019-12-16 16:13:00
 */
@RestController
@RequestMapping("/analysis")
public class AnalysisHistoryController extends BaseController {

    @Autowired
    private AnalysisHistoryService analysisHistoryService;

    @RequestMapping(value = "/list")
    public RespBase<?> list(String groupName, String projectName, PageModel pm) {
        if (log.isInfoEnabled()) {
            log.info("Query projects for groupName: {}, projectName: {}, {} ", groupName, projectName, pm);
        }
        RespBase<Object> resp = RespBase.create();
        analysisHistoryService.list(pm);
        resp.setData(pm);
        return resp;
    }

}

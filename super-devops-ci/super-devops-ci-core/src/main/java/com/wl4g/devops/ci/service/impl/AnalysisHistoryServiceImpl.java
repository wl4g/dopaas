package com.wl4g.devops.ci.service.impl;

import com.wl4g.devops.ci.service.AnalysisHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.wl4g.devops.dao.ci.AnalysisHistoryDao;
import com.wl4g.devops.page.PageModel;

/**
 * @author vjay
 * @date 2019-12-16 16:13:00
 */
@Service
public class AnalysisHistoryServiceImpl implements AnalysisHistoryService {

    @Autowired
    private AnalysisHistoryDao analysisHistoryDao;

    @Override
    public PageModel list(PageModel pm) {
        pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
        pm.setRecords(analysisHistoryDao.list());
        return pm;
    }
}

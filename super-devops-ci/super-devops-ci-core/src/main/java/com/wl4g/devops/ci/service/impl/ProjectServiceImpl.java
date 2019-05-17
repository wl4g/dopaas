package com.wl4g.devops.ci.service.impl;

import com.wl4g.devops.ci.service.ProjectService;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.dao.ci.ProjectDao;

/**
 * @author vjay
 * @date 2019-05-17 10:24:00
 */
public class ProjectServiceImpl implements ProjectService {

    private ProjectDao projectDao;

    @Override
    public int insert(Project project) {
        return projectDao.insert(project);
    }

    @Override
    public int update(Project project) {
        return projectDao.updateByPrimaryKeySelective(project);
    }

    @Override
    public int deleteById(Integer id) {
        return 0;
    }
}

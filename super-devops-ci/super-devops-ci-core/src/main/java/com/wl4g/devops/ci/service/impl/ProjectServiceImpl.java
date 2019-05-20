package com.wl4g.devops.ci.service.impl;

import com.wl4g.devops.ci.service.ProjectService;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.scm.BaseBean;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.dao.ci.ProjectDao;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author vjay
 * @date 2019-05-17 10:24:00
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    private ProjectDao projectDao;

    @Override
    public int insert(Project project) {
        project.preInsert();
        return projectDao.insert(project);
    }

    @Override
    public int update(Project project) {
        project.preUpdate();
        return projectDao.updateByPrimaryKeySelective(project);
    }

    @Override
    public int deleteById(Integer id) {
        Project project = new Project();
        project.preUpdate();
        project.setDelFlag(BaseBean.DEL_FLAG_DELETE);
        return projectDao.updateByPrimaryKeySelective(project);
    }

    @Override
    public int removeById(Integer id) {
        return projectDao.deleteByPrimaryKey(id);
    }

    @Override
    public List<Project> list(CustomPage customPage) {
        return projectDao.list(customPage);
    }


}

package com.wl4g.devops.dts.codegen.service.impl;

import com.github.pagehelper.PageHelper;
import com.wl4g.components.core.bean.BaseBean;
import com.wl4g.components.data.page.PageModel;
import com.wl4g.devops.dts.codegen.bean.GenProject;
import com.wl4g.devops.dts.codegen.dao.GenProjectDao;
import com.wl4g.devops.dts.codegen.engine.GeneratorProvider;
import com.wl4g.devops.dts.codegen.service.GenProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static java.util.Objects.isNull;

/**
* GenProjectServiceImpl
*
* @author heweijie
* @Date 2020-09-11
*/
@Service
public class GenProjectServiceImpl implements GenProjectService {

    @Autowired
    private GenProjectDao genProjectDao;

    @Override
    public PageModel page(PageModel pm, String projectName) {
        pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
        pm.setRecords(genProjectDao.list(projectName));
        return pm;
    }

    public void save(GenProject genProject) {
        if (isNull(genProject.getId())) {
        genProject.preInsert();
        insert(genProject);
        } else {
        genProject.preUpdate();
        update(genProject);
        }
    }

    private void insert(GenProject genProject) {
        genProjectDao.insertSelective(genProject);
    }

    private void update(GenProject genProject) {
        genProjectDao.updateByPrimaryKeySelective(genProject);
    }

    public GenProject detail(Integer id) {
        Assert.notNull(id, "id is null");
        return genProjectDao.selectByPrimaryKey(id);
    }

    public void del(Integer id) {
        Assert.notNull(id, "id is null");
        GenProject genProject = new GenProject();
        genProject.setId(id);
        genProject.setDelFlag(BaseBean.DEL_FLAG_DELETE);
        genProjectDao.updateByPrimaryKeySelective(genProject);
    }

    @Override
    public List<GeneratorProvider.ExtraOptionSupport> getProviderConfig(String genProviderGroup) {
        List<String> providers = GeneratorProvider.GenProviderGroup.getProviders(genProviderGroup);
        //TODO
        return CollectionUtils.arrayToList(GeneratorProvider.ExtraOptionSupport.values());
    }
}

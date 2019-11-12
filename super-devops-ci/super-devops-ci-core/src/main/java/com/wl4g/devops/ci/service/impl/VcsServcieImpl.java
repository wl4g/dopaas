package com.wl4g.devops.ci.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wl4g.devops.ci.service.VcsService;
import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.PageModel;
import com.wl4g.devops.common.bean.ci.Vcs;
import com.wl4g.devops.dao.ci.VcsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vjay
 * @date 2019-11-12 11:05:00
 */
@Service
public class VcsServcieImpl implements VcsService {

    @Autowired
    private VcsDao vcsDao;

    @Override
    public Map<String, Object> list(PageModel pm) {
        Map<String,Object> result = new HashMap<>();
        Page page = PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true);
        List<Vcs> list = vcsDao.list();
        result.put("data",list);
        result.put("page", pm);
        return result;
    }

    @Override
    public void save(Vcs vcs) {
        if(vcs.getId()==null){
            insert(vcs);
        }else{
            update(vcs);
        }
    }

    private void insert(Vcs vcs){
        vcsDao.insertSelective(vcs);
    }

    private void update(Vcs vcs){
        vcsDao.updateByPrimaryKeySelective(vcs);
    }

    @Override
    public void del(Integer id) {
        Vcs vcs = new Vcs();
        vcs.setId(id);
        vcs.setDelFlag(BaseBean.DEL_FLAG_DELETE);
        vcsDao.updateByPrimaryKeySelective(vcs);
    }

    @Override
    public Vcs detail(Integer id) {
        return vcsDao.selectByPrimaryKey(id);
    }
}

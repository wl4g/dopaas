package com.wl4g.devops.iam.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.iam.Role;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.dao.iam.RoleDao;
import com.wl4g.devops.iam.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vjay
 * @date 2019-10-29 16:02:00
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Override
    public List getRoles() {
        //TODO get current logined userId
        return roleDao.selectByUserId(4);
    }


    @Override
    public Map<String,Object> list(CustomPage customPage, String name, String displayName) {
        Map<String,Object> resp = new HashMap<>();
        Integer pageNum = null != customPage.getPageNum() ? customPage.getPageNum() : 1;
        Integer pageSize = null != customPage.getPageSize() ? customPage.getPageSize() : 10;
        Page<Project> page = PageHelper.startPage(pageNum, pageSize, true);
        List<Role> list = roleDao.list(name,displayName);
        for(Role role : list){
            //TODO menu

        }
        customPage.setPageNum(pageNum);
        customPage.setPageSize(pageSize);
        customPage.setTotal(page.getTotal());
        resp.put("page", customPage);
        resp.put("list", list);
        return resp;
    }

    @Override
    public void save(Role role) {
        if (role.getId() != null) {
            update(role);
        } else {
            insert(role);
        }
    }

    private void insert(Role role) {
        role.preInsert();
        roleDao.insertSelective(role);
    }

    private void update(Role role) {
        role.preUpdate();
        roleDao.updateByPrimaryKeySelective(role);
    }

    @Override
    public void del(Integer id) {
        Assert.notNull(id, "id is null");
        Role role = new Role();
        role.setId(id);
        role.setDelFlag(BaseBean.DEL_FLAG_DELETE);
        roleDao.updateByPrimaryKeySelective(role);
    }

    @Override
    public Role detail(Integer id){
        return roleDao.selectByPrimaryKey(id);
    }
}

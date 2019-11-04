package com.wl4g.devops.iam.service.impl;

import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.iam.Menu;
import com.wl4g.devops.common.bean.iam.User;
import com.wl4g.devops.dao.iam.MenuDao;
import com.wl4g.devops.dao.iam.UserDao;
import com.wl4g.devops.iam.service.MenuService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vjay
 * @date 2019-10-30 15:48:00
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private UserDao userDao;

    @Override
    public Map<String,Object> getMenuTree() {
        Map<String,Object> result = new HashMap<>();
        //get top-level menu
        List<Menu> menus = menuDao.selectByParentId(0);
        List<Menu> menus2 = new ArrayList<>();
        menus2.addAll(menus);
        //get childrens
        getChildren(menus,menus2);
        result.put("data",menus);
        result.put("data2",menus2);
        return result;
    }

    private void getChildren(List<Menu> menus,List<Menu> menus2) {
        for (Menu menu : menus) {
            List<Menu> childrens = menuDao.selectByParentId(menu.getId());
            menus2.addAll(childrens);
            if (!CollectionUtils.isEmpty(childrens)) {
                menu.setChildren(childrens);
                getChildren(childrens,menus2);
            }
        }
    }

    public List<Menu> getMenuList(){
        String principal = (String) SecurityUtils.getSubject().getPrincipal();
        Assert.hasText(principal,"principal is null");
        User user = userDao.selectByUserName(principal);
        Assert.notNull(user,"user is null");
        return menuDao.selectByUserId(user.getId());
    }

    @Override
    public void save(Menu menu) {
        if (menu.getId() != null) {
            update(menu);
        } else {
            insert(menu);
        }
    }

    private void insert(Menu menu) {
        menu.preInsert();
        menuDao.insertSelective(menu);
    }

    private void update(Menu menu) {
        menu.preUpdate();
        menuDao.updateByPrimaryKeySelective(menu);
    }

    @Override
    public void del(Integer id) {
        Assert.notNull(id, "id is null");
        Menu menu = new Menu();
        menu.setId(id);
        menu.setDelFlag(BaseBean.DEL_FLAG_DELETE);
        menuDao.updateByPrimaryKeySelective(menu);
    }

    @Override
    public Menu detail(Integer id){
        return menuDao.selectByPrimaryKey(id);
    }


}

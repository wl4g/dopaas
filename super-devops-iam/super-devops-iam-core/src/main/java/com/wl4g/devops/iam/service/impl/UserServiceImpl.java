package com.wl4g.devops.iam.service.impl;

import com.wl4g.devops.common.bean.iam.Menu;
import com.wl4g.devops.common.bean.iam.User;
import com.wl4g.devops.dao.iam.MenuDao;
import com.wl4g.devops.dao.iam.UserDao;
import com.wl4g.devops.iam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author vjay
 * @date 2019-10-28 16:38:00
 */
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private MenuDao menuDao;


    @Override
    public User getUserById(Integer id) {
        return userDao.selectByPrimaryKey(id);
    }


    @Override
    public Set<Menu> getMenusByUserId(Integer userId) {
        Set set = new HashSet();
        List<Menu> menus = menuDao.selectByUserId(userId);
        set.addAll(menus);
        for(Menu menu : menus){
            getMenusByParentId(menu.getId(),set);
        }
        return set;
    }


    private Set<Menu> getMenusByParentId(Integer parentId,Set<Menu> menuSet){
        //TODO chche best
        List<Menu> menus = menuDao.selectByParentId(parentId);
        if(!CollectionUtils.isEmpty(menus)){
            if(menuSet!=null){
                menuSet.addAll(menus);
            }
            for(Menu menu : menus){
                getMenusByParentId(menu.getId(),menuSet);
            }
        }
        return menuSet;
    }


}

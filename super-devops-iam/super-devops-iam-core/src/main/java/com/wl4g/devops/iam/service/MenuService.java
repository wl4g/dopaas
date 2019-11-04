package com.wl4g.devops.iam.service;

import com.wl4g.devops.common.bean.iam.Menu;

import java.util.List;
import java.util.Map;

/**
 * @author vjay
 * @date 2019-10-30 15:48:00
 */
public interface MenuService {

    Map<String,Object> getMenuTree();

    List<Menu> getMenuList();

    void save(Menu menu);

    void del(Integer id);

    Menu detail(Integer id);


}

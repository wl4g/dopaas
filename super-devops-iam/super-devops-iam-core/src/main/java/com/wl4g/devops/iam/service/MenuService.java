package com.wl4g.devops.iam.service;

import com.wl4g.devops.common.bean.iam.Menu;

import java.util.List;

/**
 * @author vjay
 * @date 2019-10-30 15:48:00
 */
public interface MenuService {

    List<Menu> getMenuTree();

    void save(Menu menu);

    void del(Integer id);

    Menu detail(Integer id);


}

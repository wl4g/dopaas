package com.wl4g.devops.iam.service.impl;

import com.wl4g.devops.common.bean.iam.Menu;
import com.wl4g.devops.dao.iam.MenuDao;
import com.wl4g.devops.iam.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author vjay
 * @date 2019-10-30 15:48:00
 */
@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuDao menuDao;

    public List<Menu> getMenuTree(){

        return null;
    }






}

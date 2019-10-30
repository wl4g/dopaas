package com.wl4g.devops.iam.service.impl;

import com.wl4g.devops.dao.iam.RoleDao;
import com.wl4g.devops.iam.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}

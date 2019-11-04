package com.wl4g.devops.iam.service;

import com.wl4g.devops.common.bean.iam.Role;
import com.wl4g.devops.common.bean.scm.CustomPage;

import java.util.List;
import java.util.Map;

/**
 * @author vjay
 * @date 2019-10-29 16:01:00
 */
public interface RoleService {

    List getRolesByUserGroups();

    Map<String,Object> list(CustomPage customPage, String name, String displayName);

    void save(Role group);

    void del(Integer id);

    Role detail(Integer id);

}

package com.wl4g.devops.iam.service;

import com.wl4g.devops.common.bean.iam.Menu;
import com.wl4g.devops.common.bean.iam.User;
import com.wl4g.devops.common.bean.scm.CustomPage;

import java.util.Map;
import java.util.Set;

/**
 * @author vjay
 * @date 2019-10-28 16:38:00
 */
public interface UserService {

    User getUserById(Integer id);

    Set<Menu> getMenusByUserId(Integer userId);

    Map<String,Object> list(CustomPage customPage,String userName,String displayName);

    void save(User user);

    void del(Integer userId);

    User detail(Integer userId);


}

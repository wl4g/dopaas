/*
 * Copyright 2017 ~ 2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.devops.rest.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wl4g.devops.ci.service.DependencyService;
import com.wl4g.devops.ci.service.ProjectService;
import com.wl4g.devops.common.bean.ci.Dependency;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.scm.ConfigVersionList;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.ci.DependencyDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.wl4g.devops.common.bean.scm.BaseBean.DEL_FLAG_NORMAL;
import static com.wl4g.devops.common.bean.scm.BaseBean.ENABLED;

/**
 * CI/CD controller
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-16 15:05:00
 */
@RestController
@RequestMapping("/dependency")
public class DependencyController {

    @Autowired
    private DependencyDao dependencyDao;

    @RequestMapping(value = "/list")
    public RespBase<?> list(String projectName, CustomPage customPage) {
        RespBase<Object> resp = RespBase.create();
        Integer pageNum = null != customPage.getPageNum() ? customPage.getPageNum() : 1;
        Integer pageSize = null != customPage.getPageSize() ? customPage.getPageSize() : 5;
        Page<ConfigVersionList> page = PageHelper.startPage(pageNum, pageSize, true);
        List<Dependency> list = dependencyDao.list(projectName);
        customPage.setPageNum(pageNum);
        customPage.setPageSize(pageSize);
        customPage.setTotal(page.getTotal());
        resp.getData().put("page", customPage);
        resp.getData().put("list", list);
        return resp;
    }

    @RequestMapping(value = "/save")
    public RespBase<?> save(Dependency dependency) {
        RespBase<Object> resp = RespBase.create();

        if (null != dependency.getId() && dependency.getId() > 0) {
            dependency.preUpdate();
            dependencyDao.updateByPrimaryKeySelective(dependency);
        } else {
            dependency.preInsert();
            dependency.setDelFlag(DEL_FLAG_NORMAL);
            dependency.setEnable(ENABLED);
            dependencyDao.insertSelective(dependency);
        }
        return resp;
    }

    @RequestMapping(value = "/detail")
    public RespBase<?> detail(Integer id) {
        RespBase<Object> resp = RespBase.create();
        Assert.notNull(id, "id can not be null");
        Dependency dependency = dependencyDao.selectByPrimaryKey(id);
        resp.getData().put("dependency", dependency);
        return resp;
    }

    @RequestMapping(value = "/del")
    public RespBase<?> del(Integer id) {
        RespBase<Object> resp = RespBase.create();
        Assert.notNull(id, "id can not be null");
        dependencyDao.deleteByPrimaryKey(id);
        return resp;
    }


}
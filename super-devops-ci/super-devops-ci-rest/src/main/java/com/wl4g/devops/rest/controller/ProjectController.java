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
import com.wl4g.devops.ci.config.DeployProperties;
import com.wl4g.devops.ci.service.ProjectService;
import com.wl4g.devops.common.bean.ci.Project;
import com.wl4g.devops.common.bean.scm.ConfigVersionList;
import com.wl4g.devops.common.bean.scm.CustomPage;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.ci.ProjectDao;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.wl4g.devops.common.bean.BaseBean.DEL_FLAG_NORMAL;
import static com.wl4g.devops.common.bean.BaseBean.ENABLED;
import static com.wl4g.devops.common.constants.CiDevOpsConstants.TASK_LOCK_STATUS__UNLOCK;

/**
 * Porject controller
 *
 * @author Wangl.sir <983708408@qq.com>
 * @author vjay
 * @date 2019-05-16 15:05:00
 */
@RestController
@RequestMapping("/project")
public class ProjectController {

    final protected Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private DeployProperties config;

    /**
     * list
     * @param groupName
     * @param projectName
     * @param customPage
     * @return
     */
    @RequestMapping(value = "/list")
    public RespBase<?> list(String groupName, String projectName, CustomPage customPage) {
        log.info("into ProjectController.list prarms::"+ "groupName = {} , projectName = {} , customPage = {} ", groupName, projectName, customPage );
        RespBase<Object> resp = RespBase.create();
        Integer pageNum = null != customPage.getPageNum() ? customPage.getPageNum() : 1;
        Integer pageSize = null != customPage.getPageSize() ? customPage.getPageSize() : 5;
        Page<ConfigVersionList> page = PageHelper.startPage(pageNum, pageSize, true);
        List<Project> list = projectService.list(groupName, projectName);
        customPage.setPageNum(pageNum);
        customPage.setPageSize(pageSize);
        customPage.setTotal(page.getTotal());
        resp.getData().put("page", customPage);
        resp.getData().put("list", list);
        return resp;
    }

    /**
     * save
     * @param project
     * @return
     */
    @RequestMapping(value = "/save")
    public RespBase<?> save(@RequestBody Project project) {
        log.info("into ProjectController.save prarms::"+ "project = {} ", project );
        RespBase<Object> resp = RespBase.create();
        if (null != project.getId() && project.getId() > 0) {
            project.preUpdate();
            projectService.update(project);
        } else {
            project.preInsert();
            project.setDelFlag(DEL_FLAG_NORMAL);
            project.setEnable(ENABLED);
            project.setLockStatus(TASK_LOCK_STATUS__UNLOCK);
            projectService.insert(project);
        }
        return resp;
    }

    /**
     * Get detail by id
     * @param id
     * @return
     */
    @RequestMapping(value = "/detail")
    public RespBase<?> detail(Integer id) {
        log.info("into ProjectController.detail prarms::"+ "id = {} ", id );
        RespBase<Object> resp = RespBase.create();
        Assert.notNull(id, "id can not be null");
        Project project = projectService.selectByPrimaryKey(id);
        resp.getData().put("project", project);
        return resp;
    }

    /**
     * delete by id
     * @param id
     * @return
     */
    @RequestMapping(value = "/del")
    public RespBase<?> del(Integer id) {
        log.info("into ProjectController.del prarms::"+ "id = {} ", id );
        RespBase<Object> resp = RespBase.create();
        Assert.notNull(id, "id can not be null");
        projectService.deleteById(id);
        return resp;
    }

    /**
     * 用于下拉框
     * @return
     */
    @RequestMapping(value = "/all")
    public RespBase<?> all() {
        RespBase<Object> resp = RespBase.create();
        List<Project> list = projectService.list(null, null);
        resp.getData().put("list", list);
        return resp;
    }

    /**
     * unlock  - 在执行中的build任务会让project锁定，正常情况会自动解锁，异常情况可以用该接口解锁
     * @param id
     * @return
     */
    @RequestMapping(value = "/unlock")
    public RespBase<?> unlock(Integer id) {
        log.info("into ProjectController.unlock prarms::"+ "id = {} ", id );
        RespBase<Object> resp = RespBase.create();
        Assert.notNull(id, "id can not be null");
        projectService.updateLockStatus(id, TASK_LOCK_STATUS__UNLOCK);
        return resp;
    }

    /**
     * 从gitlab获取分支列表，让前端可以下拉显示
     * @param appGroupId
     * @param tarOrBranch
     * @return
     */
    @RequestMapping(value = "/getBranchs")
    public RespBase<?> getBranchs(Integer appGroupId,Integer tarOrBranch) {
        log.debug("into ProjectController.getBranchs prarms::"+ "appGroupId = {} , tarOrBranch = {} ", appGroupId, tarOrBranch );
        RespBase<Object> resp = RespBase.create();
        Assert.notNull(appGroupId, "id can not be null");

        Project project = projectDao.getByAppGroupId(appGroupId);
        Assert.notNull(project,"not found project ,please check you project config");
        String url = project.getGitUrl();


        Integer gitlabProjectId = getGitlabProjectId(getGitProjectNameByUrl(url));

        if(tarOrBranch!=null&&tarOrBranch==2){//tag
            List<String> branchNames = getTagsByProjectId(gitlabProjectId);
            resp.getData().put("branchNames",branchNames);
        }else{//branch
            List<String> branchNames = getBranchByProjectId(gitlabProjectId);
            resp.getData().put("branchNames",branchNames);
        }
        return resp;
    }

    /**
     * Get GitLab Branch by gitlab projectId
     * @param projectId
     * @return
     */
    private List<String> getBranchByProjectId(Integer projectId){
        Assert.notNull(projectId,"not found git projectId,please check you gitlab name is match your project config name");
        Netty4ClientHttpRequestFactory factory = new Netty4ClientHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate(factory);
        HttpHeaders headers = new HttpHeaders();
        headers.add("PRIVATE-TOKEN", config.getGitToken());
        HttpEntity<String> formEntity = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange = restTemplate.exchange(config.getGitUrl()+"/api/v4/projects/"+projectId+"/repository/branches",
                HttpMethod.GET, formEntity, String.class);
        String result = exchange.getBody();
        List<Map> list = JacksonUtils.parseJSON(result,List.class);
        List<String> re = new ArrayList<>();
        for(Map map : list){
            String name = map.get("name").toString();
            re.add(name);
        }
        log.info("resutl = {}",re);
        return re;
    }

    /**
     * Get GitLab Tag by gitlab projectId
     * @param projectId
     * @return
     */
    private List<String> getTagsByProjectId(Integer projectId){
        Assert.notNull(projectId,"not found git projectId,please check you gitlab name is match your project config name");
        Netty4ClientHttpRequestFactory factory = new Netty4ClientHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate(factory);
        HttpHeaders headers = new HttpHeaders();
        headers.add("PRIVATE-TOKEN", config.getGitToken());
        HttpEntity<String> formEntity = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange = restTemplate.exchange(config.getGitUrl()+"/api/v4/projects/"+projectId+"/repository/tags",
                HttpMethod.GET, formEntity, String.class);
        String result = exchange.getBody();
        List<Map> list = JacksonUtils.parseJSON(result,List.class);
        List<String> re = new ArrayList<>();
        for(Map map : list){
            String name = map.get("name").toString();
            re.add(name);
        }
        log.info("resutl = {}",re);
        return re;
    }


    /**
     * Get GitLab ProjectId by projectName
     * @param projectName
     * @return
     */
    private Integer getGitlabProjectId(String projectName){
        Assert.hasText(projectName,"projectName can not be null");
        Netty4ClientHttpRequestFactory factory = new Netty4ClientHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate(factory);
        HttpHeaders headers = new HttpHeaders();
        headers.add("PRIVATE-TOKEN", config.getGitToken());
        HttpEntity<String> formEntity = new HttpEntity<>(null, headers);
        ResponseEntity<String> exchange = restTemplate.exchange(config.getGitUrl()+"/api/v4/projects?simple=true&search="+projectName,
                HttpMethod.GET, formEntity, String.class);
        String result = exchange.getBody();
        List<Map> list = JacksonUtils.parseJSON(result,List.class);
        for(Map map : list){
            String name = map.get("name").toString();
            if(StringUtils.equals(name,projectName)){
                return (int) map.get("id");
            }
        }
        log.info("resutl = {}",result);
        return null;
    }

    /**
     * Tool for this class : get Git Project Name from Url
     * @param url
     * @return
     */
    private static String getGitProjectNameByUrl(String url){
        int index = url.lastIndexOf("/");
        url = url.substring(index+1);
        index = url.lastIndexOf(".");
        url = url.substring(0,index);
        return url;
    }







}
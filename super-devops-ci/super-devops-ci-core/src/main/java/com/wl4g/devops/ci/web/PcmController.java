package com.wl4g.devops.ci.web;

import com.wl4g.devops.ci.pcm.CompositePcmOperatorAdapter;
import com.wl4g.devops.ci.service.PcmService;
import com.wl4g.devops.common.bean.ci.Pcm;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.model.SelectionModel;
import com.wl4g.devops.page.PageModel;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.apache.shiro.authz.annotation.Logical.AND;

/**
 * Porject manager
 *
 * @author vjay
 * @date 2020-01-03 16:28:00
 */
@RestController
@RequestMapping("/pcm")
public class PcmController {

    @Autowired
    private CompositePcmOperatorAdapter pcmPlatformHandle;
    
    @Autowired
    private PcmService pcmService;

    @RequestMapping(value = "/getUsers")
    public RespBase<?> getUsers(Integer taskId){
        RespBase<Object> resp = RespBase.create();
        List<SelectionModel> selectInfos = pcmPlatformHandle.getUsers(taskId);
        resp.setData(selectInfos);
        return resp;
    }

    @RequestMapping(value = "/getProjects")
    public RespBase<?> getProjects(Integer taskId){
        RespBase<Object> resp = RespBase.create();
        List<SelectionModel>  selectInfos = pcmPlatformHandle.getProjects(taskId);
        resp.setData(selectInfos);
        return resp;
    }

    @RequestMapping(value = "/getIssues")
    public RespBase<?> getIssues(Integer taskId,String userId, String projectId,String search){
        RespBase<Object> resp = RespBase.create();
        List<SelectionModel>  selectInfos = pcmPlatformHandle.getIssues(taskId,userId,projectId,search);
        resp.setData(selectInfos);
        return resp;
    }

    @RequestMapping("/list")
    @RequiresPermissions(value = {"ci","ci:pcm"},logical = AND)
    public RespBase<?> list(PageModel pm, String name, String providerKind, Integer authType) {
        RespBase<Object> resp = RespBase.create();
        resp.setData(pcmService.list(pm, name, providerKind, authType));
        return resp;
    }

    @RequestMapping("/save")
    @RequiresPermissions(value = {"ci","ci:pcm"},logical = AND)
    public RespBase<?> save(Pcm pcm) {
        RespBase<Object> resp = RespBase.create();
        pcmService.save(pcm);
        return resp;
    }

    @RequestMapping("/del")
    @RequiresPermissions(value = {"ci","ci:pcm"},logical = AND)
    public RespBase<?> del(Integer id) {
        RespBase<Object> resp = RespBase.create();
        pcmService.del(id);
        return resp;
    }

    @RequestMapping("/detail")
    @RequiresPermissions(value = {"ci","ci:pcm"},logical = AND)
    public RespBase<?> detail(Integer id) {
        RespBase<Object> resp = RespBase.create();
        Pcm pcm = pcmService.detail(id);
        resp.setData(pcm);
        return resp;
    }

    @RequestMapping("/all")
    public RespBase<?> all() {
        RespBase<Object> resp = RespBase.create();
        resp.setData(pcmService.all());
        return resp;
    }
}

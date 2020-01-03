package com.wl4g.devops.ci.web;

import com.wl4g.devops.ci.pmplatform.PmPlatformHandle;
import com.wl4g.devops.ci.pmplatform.model.dto.SelectInfo;
import com.wl4g.devops.common.web.RespBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Porject manager
 *
 * @author vjay
 * @date 2020-01-03 16:28:00
 */
@RestController
@RequestMapping("/pm")
public class PmController {

    @Autowired
    private PmPlatformHandle pmPlatformHandle;

    @RequestMapping(value = "/getUsers")
    public RespBase<?> getUsers(Integer taskId){
        RespBase<Object> resp = RespBase.create();
        List<SelectInfo> selectInfos = pmPlatformHandle.getUsers(taskId);
        resp.setData(selectInfos);
        return resp;
    }

    @RequestMapping(value = "/getProjects")
    public RespBase<?> getProjects(Integer taskId){
        RespBase<Object> resp = RespBase.create();
        List<SelectInfo>  selectInfos = pmPlatformHandle.getProjects(taskId);
        resp.setData(selectInfos);
        return resp;
    }

    @RequestMapping(value = "/getIssues")
    public RespBase<?> getIssues(Integer taskId,String userId, String projectId,String search){
        RespBase<Object> resp = RespBase.create();
        List<SelectInfo>  selectInfos = pmPlatformHandle.getIssues(taskId,userId,projectId,search);
        resp.setData(selectInfos);
        return resp;
    }
}

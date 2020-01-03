package com.wl4g.devops.ci.pmplatform.handle;

import com.wl4g.devops.ci.pmplatform.model.dto.SelectInfo;

import java.util.List;

/**
 * @author vjay
 * @date 2020-01-03 14:10:00
 */
public interface PmPlatformInterface {

    List<SelectInfo> getProjects();

    List<SelectInfo> getUsers();

    List<SelectInfo> getIssues(String userId,String projectId,String search);


}

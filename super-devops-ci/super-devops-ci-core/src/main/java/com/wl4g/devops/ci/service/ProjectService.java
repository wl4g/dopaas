package com.wl4g.devops.ci.service;

import com.wl4g.devops.common.bean.ci.Project;

/**
 * @author vjay
 * @date 2019-05-17 10:23:00
 */
public interface ProjectService {


	int insert(Project project);

	int update(Project project);

	int deleteById(Integer id);


}

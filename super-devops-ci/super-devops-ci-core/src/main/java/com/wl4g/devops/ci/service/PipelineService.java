package com.wl4g.devops.ci.service;

import com.wl4g.devops.common.bean.ci.PipeStepBuilding;
import com.wl4g.devops.common.bean.ci.Pipeline;
import com.wl4g.devops.page.PageModel;

import java.util.List;

/**
 * @author vjay
 * @date 2020-04-27 15:06:00
 */
public interface PipelineService {

    PageModel list(PageModel pm, String pipeName, String providerKind, String environment);

    void save(Pipeline pipeline);

    Pipeline detail(Integer id);

    void del(Integer id);

    List<Pipeline> getByClusterId(Integer clusterId);

    PipeStepBuilding getPipeStepBuilding(Integer clusterId, Integer pipeId, Integer refType);

    List<Pipeline> getForSelect();


}

package com.wl4g.devops.ci.service;

import com.wl4g.devops.common.bean.ci.Pipeline;

import java.util.List;

/**
 * @author vjay
 * @date 2020-04-27 15:06:00
 */
public interface PipelineService {

    List<Pipeline> list(String pipeName,String providerKind,String environment);

    void save(Pipeline pipeline);

    Pipeline detail(Integer id);

    void del(Integer id);







}

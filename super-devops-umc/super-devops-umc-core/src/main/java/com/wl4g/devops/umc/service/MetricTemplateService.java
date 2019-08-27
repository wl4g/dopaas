package com.wl4g.devops.umc.service;

import com.wl4g.devops.common.bean.umc.MetricTemplate;

import java.util.List;

/**
 * @author vjay
 * @date 2019-08-05 16:01:00
 */
public interface MetricTemplateService {

	void save(MetricTemplate metricTemplate);

	void del(Integer id);

	List<MetricTemplate> getByClassify(String classify);

}

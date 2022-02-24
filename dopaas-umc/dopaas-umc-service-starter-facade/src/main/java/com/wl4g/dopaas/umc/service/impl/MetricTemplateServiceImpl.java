/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.dopaas.umc.service.impl;


import com.wl4g.infra.core.bean.BaseBean;
import com.wl4g.infra.core.page.PageHolder;
import com.wl4g.dopaas.common.bean.umc.MetricTemplate;
import com.wl4g.dopaas.umc.data.MetricTemplateDao;
import com.wl4g.dopaas.umc.service.MetricTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author vjay
 * @date 2019-08-09 14:06:00
 */
@Service
public class MetricTemplateServiceImpl implements MetricTemplateService {

private @Autowired  MetricTemplateDao metricTemplateDao;

	@Override
	public PageHolder<MetricTemplate> list(PageHolder<MetricTemplate> pm, String metric, String classify) {
		pm.bind();
		pm.setRecords(metricTemplateDao.list(metric, classify));
		return pm;
	}

	@Override
	public MetricTemplate detail(Long id) {
		return metricTemplateDao.selectByPrimaryKey(id);
	}

	@Override
	public void save(MetricTemplate metricTemplate) {
		if (metricTemplate.getId() != null) {
			metricTemplate.preUpdate();
			metricTemplateDao.updateByPrimaryKeySelective(metricTemplate);
		} else {
			metricTemplate.preInsert();
			metricTemplateDao.insertSelective(metricTemplate);
		}
	}

	@Override
	public void del(Long id) {
		MetricTemplate metricTemplate = new MetricTemplate();
		metricTemplate.setId(id);
		metricTemplate.setDelFlag(BaseBean.DEL_FLAG_DELETE);
		metricTemplate.preUpdate();
		metricTemplateDao.updateByPrimaryKeySelective(metricTemplate);
	}

	@Override
	public List<MetricTemplate> getByClassify(String classify) {
		return metricTemplateDao.list(null, classify);
	}
}
package com.wl4g.devops.umc.service.impl;

import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.umc.MetricTemplate;
import com.wl4g.devops.dao.umc.MetricTemplateDao;
import com.wl4g.devops.umc.service.MetricTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author vjay
 * @date 2019-08-09 14:06:00
 */
@Service
public class MetricTemplateServiceImpl implements MetricTemplateService {

    @Autowired
    private MetricTemplateDao metricTemplateDao;

    @Override
    public void save(MetricTemplate metricTemplate) {
        if(metricTemplate.getId()!=null){
            metricTemplate.preUpdate();
            metricTemplateDao.updateByPrimaryKeySelective(metricTemplate);
        }else{
            metricTemplate.preInsert();
            metricTemplateDao.insertSelective(metricTemplate);
        }
    }

    @Override
    public void del(Integer id) {
        MetricTemplate metricTemplate = new MetricTemplate();
        metricTemplate.setId(id);
        metricTemplate.setDelFlag(BaseBean.DEL_FLAG_DELETE);
        metricTemplate.preUpdate();
        metricTemplateDao.updateByPrimaryKeySelective(metricTemplate);
    }

    @Override
    public List<MetricTemplate> getByClassify(String classify) {
        return metricTemplateDao.list(null,classify);
    }
}

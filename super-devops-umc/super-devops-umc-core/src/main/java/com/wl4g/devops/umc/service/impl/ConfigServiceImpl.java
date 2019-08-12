package com.wl4g.devops.umc.service.impl;

import com.wl4g.devops.common.bean.BaseBean;
import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.dao.scm.AppClusterDao;
import com.wl4g.devops.dao.umc.AlarmConfigDao;
import com.wl4g.devops.dao.umc.AlarmTemplateDao;
import com.wl4g.devops.umc.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @author vjay
 * @date 2019-08-09 14:06:00
 */
@Service
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    private AlarmConfigDao alarmConfigDao;

    @Autowired
    private AlarmTemplateDao alarmTemplateDao;

    @Autowired
    private AppClusterDao appClusterDao;

    @Override
    public void save(AlarmConfig alarmConfig) {
        if(alarmConfig.getId()!=null){
            alarmConfig.preUpdate();
            alarmConfigDao.updateByPrimaryKeySelective(alarmConfig);
        }else{
            alarmConfig.preInsert();
            alarmConfigDao.insertSelective(alarmConfig);
        }
    }

    @Override
    public void del(Integer id) {
        AlarmConfig alarmConfig = new AlarmConfig();
        alarmConfig.setId(id);
        alarmConfig.setDelFlag(BaseBean.DEL_FLAG_DELETE);
        alarmConfig.preUpdate();
        alarmConfigDao.updateByPrimaryKeySelective(alarmConfig);
    }

    @Override
    public AlarmConfig detail(Integer id) {
        Assert.notNull(id,"id is null");
        AlarmConfig alarmConfig = alarmConfigDao.selectByPrimaryKey(id);
        Assert.notNull(alarmConfig,"not found alarmConfig");
        AlarmTemplate alarmTemplate = alarmTemplateDao.selectByPrimaryKey(alarmConfig.getTemplateId());
        Assert.notNull(alarmTemplate,"not found alarmTemplate");
        alarmConfig.setClassify(alarmTemplate.getClassify());
        AppInstance appInstance = appClusterDao.getAppInstance(alarmConfig.getCollectId().toString());
        alarmConfig.setGroup(appInstance.getClusterId().intValue());
        alarmConfig.setEnvironment(Integer.valueOf(appInstance.getEnvId()));
        return alarmConfig;
    }
}

package com.wl4g.devops.umc.alarm;

import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.dao.scm.AppGroupDao;
import com.wl4g.devops.dao.umc.AlarmConfigDao;
import com.wl4g.devops.dao.umc.AlarmTemplateDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author vjay
 * @date 2019-07-05 17:38:00
 */
@Component
public class AlarmDaoInterfaceImpl implements AlarmDaoInterface {

    @Autowired
    private AppGroupDao appGroupDao;
    @Autowired
    private AlarmTemplateDao alarmTemplateDao;
    @Autowired
    private AlarmConfigDao alarmConfigDao;


    @Override
    public List<AlarmConfig> selectByTemplateId(Integer templateId) {
        return alarmConfigDao.selectByTemplateId(templateId);
    }

    @Override
    public List<AppInstance> instancelist(AppInstance appInstance) {
        return appGroupDao.instancelist(appInstance);
    }

    @Override
    public List<AlarmConfig> selectAll() {
        return alarmConfigDao.selectAll();
    }

    @Override
    public List<AlarmTemplate> selectAllWithRule() {
        return alarmTemplateDao.selectAllWithRule();
    }
}

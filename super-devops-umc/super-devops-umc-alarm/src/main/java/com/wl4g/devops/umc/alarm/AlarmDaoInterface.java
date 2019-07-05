package com.wl4g.devops.umc.alarm;

import com.wl4g.devops.common.bean.scm.AppInstance;
import com.wl4g.devops.common.bean.umc.AlarmConfig;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author vjay
 * @date 2019-07-05 17:26:00
 */
@Component
public interface AlarmDaoInterface {

    List<AlarmConfig> selectByTemplateId(Integer templateId);

    List<AppInstance> instancelist(AppInstance appInstance);

    List<AlarmConfig> selectAll();

    List<AlarmTemplate> selectAllWithRule();


}

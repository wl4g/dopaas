package com.wl4g.devops.umc.service.impl;

import com.wl4g.devops.common.bean.umc.AlarmRule;
import com.wl4g.devops.common.bean.umc.AlarmTemplate;
import com.wl4g.devops.common.utils.serialize.JacksonUtils;
import com.wl4g.devops.dao.umc.AlarmRuleDao;
import com.wl4g.devops.dao.umc.AlarmTemplateDao;
import com.wl4g.devops.umc.service.TemplateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.wl4g.devops.common.bean.BaseBean.DEL_FLAG_DELETE;
import static com.wl4g.devops.common.bean.BaseBean.DEL_FLAG_NORMAL;
import static com.wl4g.devops.common.bean.BaseBean.ENABLED;

/**
 * @author vjay
 * @date 2019-08-06 18:30:00
 */
@Service
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    private AlarmTemplateDao alarmTemplateDao;

    @Autowired
    private AlarmRuleDao alarmRuleDao;

    @Override
    @Transactional
    public void save(AlarmTemplate alarmTemplate) {
        //template
        Assert.notNull(alarmTemplate,"alarmTemplate is null");

        List<Map<String, String>> tagMap = alarmTemplate.getTagMap();
        if(!CollectionUtils.isEmpty(tagMap)){
            String tags = JacksonUtils.toJSONString(tagMap);
            alarmTemplate.setTags(tags);
        }

        if(alarmTemplate.getId()!=null){//update
            alarmTemplate.preUpdate();
            alarmTemplateDao.updateByPrimaryKeySelective(alarmTemplate);
        }else{
            alarmTemplate.preInsert();
            alarmTemplate.setDelFlag(DEL_FLAG_NORMAL);
            alarmTemplate.setEnable(ENABLED);
            alarmTemplateDao.insertSelective(alarmTemplate);
        }


        //找到旧的规则，如果发现新规则列表中没有旧的规则，则删掉缺少的旧规则
        List<AlarmRule> oldAlarmRules = alarmRuleDao.selectByTemplateId(alarmTemplate.getId());
        List<AlarmRule> temp = new ArrayList<>();
        if(!CollectionUtils.isEmpty(oldAlarmRules)){
            for(AlarmRule oldAlarmRule : oldAlarmRules){
                for(AlarmRule newAlarmRule : alarmTemplate.getRules()){
                    if(newAlarmRule.getId()!=null&& oldAlarmRule.getId().equals(newAlarmRule.getId())){
                        temp.add(oldAlarmRule);
                    }
                }
            }
        }
        oldAlarmRules.removeAll(temp);
        for(AlarmRule alarmRule : oldAlarmRules){
            alarmRule.setDelFlag(DEL_FLAG_DELETE);
            alarmRuleDao.updateByPrimaryKeySelective(alarmRule);
        }


        //rules
        List<AlarmRule> rules = alarmTemplate.getRules();
        Assert.notEmpty(rules,"rules is empty");
        for(AlarmRule alarmRule : rules){
            if(alarmRule.getId()!=null){
                alarmRule.preUpdate();
                alarmRule.setTemplateId(alarmTemplate.getId());
                alarmRuleDao.updateByPrimaryKeySelective(alarmRule);
            }else{
                alarmRule.preInsert();
                alarmRule.setTemplateId(alarmTemplate.getId());
                alarmRuleDao.insertSelective(alarmRule);
            }
        }

    }

    @Override
    public AlarmTemplate detail(Integer id) {
        Assert.notNull(id,"id is null");
        AlarmTemplate alarmTemplate = alarmTemplateDao.selectByPrimaryKey(id);
        Assert.notNull(alarmTemplate,"alarmTemplate is null");
        List<AlarmRule> alarmRules = alarmRuleDao.selectByTemplateId(id);
        alarmTemplate.setRules(alarmRules);

        if(StringUtils.isNotBlank(alarmTemplate.getTags())){
            List list = JacksonUtils.parseJSON(alarmTemplate.getTags(), List.class);
            alarmTemplate.setTagMap(list);
        }

        return alarmTemplate;
    }

    @Override
    public void del(Integer id) {
        Assert.notNull(id,"id is null");
        AlarmTemplate alarmTemplate = new AlarmTemplate();
        alarmTemplate.setId(id);
        alarmTemplate.setDelFlag(DEL_FLAG_DELETE);
        alarmTemplate.preUpdate();
        alarmTemplateDao.updateByPrimaryKeySelective(alarmTemplate);
    }
}

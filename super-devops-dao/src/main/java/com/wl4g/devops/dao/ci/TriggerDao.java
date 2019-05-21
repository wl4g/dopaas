package com.wl4g.devops.dao.ci;

import com.wl4g.devops.common.bean.ci.Trigger;
import com.wl4g.devops.common.bean.scm.CustomPage;

import java.util.List;
import java.util.Map;

public interface TriggerDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Trigger record);

    int insertSelective(Trigger record);

    Trigger selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Trigger record);

    int updateByPrimaryKey(Trigger record);

    List<Trigger> list(CustomPage customPag);

    Trigger getTriggerByProjectAndBranch(Map<String,Object> map);
}
package com.wl4g.devops.dao.iam;

import com.wl4g.devops.common.bean.iam.ContactChannel;

public interface ContactChannelDao {
    int deleteByPrimaryKey(Integer id);

    int deleteByContactId(Integer id);

    int insert(ContactChannel record);

    int insertSelective(ContactChannel record);

    ContactChannel selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ContactChannel record);

    int updateByPrimaryKey(ContactChannel record);

}
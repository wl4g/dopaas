package com.wl4g.devops.dao.doc;

import com.wl4g.devops.common.bean.doc.Share;

import java.util.List;

public interface ShareDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Share record);

    int insertSelective(Share record);

    Share selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Share record);

    int updateByPrimaryKey(Share record);

    List<Share> list();

    Share selectByShareCode(String shareCode);
}
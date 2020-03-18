package com.wl4g.devops.dao.doc;

import java.util.List;

import com.wl4g.devops.common.bean.doc.Label;
import org.apache.ibatis.annotations.Param;

public interface LabelDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Label record);

    int insertSelective(Label record);

    Label selectByPrimaryKey(Integer id);

    List<Label> selectByFileId(Integer fileId);

    List<Integer> selectLabelIdsByFileId(Integer fileId);

    List<Label> selectAll();

    List<Label> list(@Param("name") String name);

    int updateByPrimaryKeySelective(Label record);

    int updateByPrimaryKey(Label record);
}
package com.wl4g.devops.dao.doc;

import com.wl4g.devops.common.bean.doc.FileLabel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FileLabelDao {
    int deleteByPrimaryKey(Integer id);

    int deleteByFileId(Integer fileId);

    int insert(FileLabel record);

    int insertBatch(@Param("labelIds") List<Integer> labelIds,@Param("fileId") Integer fileId);

    int insertSelective(FileLabel record);

    FileLabel selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FileLabel record);

    int updateByPrimaryKey(FileLabel record);
}
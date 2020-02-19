package com.wl4g.devops.dao.doc;

import com.wl4g.devops.common.bean.doc.FileChanges;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FileChangesDao {
    int deleteByPrimaryKey(Integer id);

    int insert(FileChanges record);

    int insertSelective(FileChanges record);

    FileChanges selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FileChanges record);

    int updateByPrimaryKey(FileChanges record);

    List<FileChanges> list(@Param("name")String name, @Param("lang")String lang,@Param("labelId")Integer labelId);

    List<FileChanges> selectByDocCode(String fileCode);

    FileChanges selectLastByDocCode(String fileCode);

    void updateIsLatest(String fileCode);
}
package com.wl4g.devops.dao.share;

import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * @author vjay
 * @date 2019-12-24 11:05:00
 */
public interface LogPipelineCleanerDao {

    int cleanJobStatusTraceLog(@Param("creationTime") Date creationTime);

    int cleanJobExecutionLog(@Param("startTime") Date startTime);

    int cleanUmcAlarmRecordSublist(@Param("createTime") Date createTime);

    int cleanUmcAlarmRecord(@Param("createTime") Date createTime);

    int cleanCiTaskHistorySublist(@Param("createDate") Date createDate);

    int cleanCiTaskHistory(@Param("createDate") Date createDate);

}

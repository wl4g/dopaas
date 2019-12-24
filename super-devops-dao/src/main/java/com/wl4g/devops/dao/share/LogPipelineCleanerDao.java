package com.wl4g.devops.dao.share;

import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * @author vjay
 * @date 2019-12-24 11:05:00
 */
public interface LogPipelineCleanerDao {


    void cleanJobStatusTraceLog(@Param("creationTime") Date creationTime);

    void cleanJobExecutionLog(@Param("startTime") Date startTime);


}

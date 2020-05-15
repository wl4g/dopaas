package com.wl4g.devops.ci.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author vjay
 * @date 2020-03-06 17:17:00
 */
public class GraphTask implements Serializable {
    private static final long serialVersionUID = 8940373806493080114L;

    //private String runId;//RUN-{flowId|pipeId}-{timestamp}
    private String graphTaskId;

    private Long createTime;//timestamp

    private List<Project> projects;

    public static class Project{

        private Integer projectId;

        private String status;//WAITING|RUNNING|FAILED|SUCCESS

        private String ref;//branch|tag

        private Long startTime;

        private Long endTime;

        private List<Project> children;

        //TODO ...

    }



}

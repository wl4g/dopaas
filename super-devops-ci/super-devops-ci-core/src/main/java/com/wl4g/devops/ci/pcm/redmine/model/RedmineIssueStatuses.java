package com.wl4g.devops.ci.pcm.redmine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author vjay
 * @date 2020-04-29 10:35:00
 */
public class RedmineIssueStatuses {

    @JsonProperty("issue_statuses")
    private List<IssueStatus> issueStatuses;

    public List<IssueStatus> getIssueStatuses() {
        return issueStatuses;
    }

    public void setIssueStatuses(List<IssueStatus> issueStatuses) {
        this.issueStatuses = issueStatuses;
    }

    public static class IssueStatus{
        private Integer id;

        private String name;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}

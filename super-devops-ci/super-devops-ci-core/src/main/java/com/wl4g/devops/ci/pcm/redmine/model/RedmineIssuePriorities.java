package com.wl4g.devops.ci.pcm.redmine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author vjay
 * @date 2020-04-27 14:23:00
 */
public class RedmineIssuePriorities {


    @JsonProperty("issue_priorities")
    private List<IssuesPriorities> issuePriorities;

    public List<IssuesPriorities> getIssuePriorities() {
        return issuePriorities;
    }

    public void setIssuePriorities(List<IssuesPriorities> issuePriorities) {
        this.issuePriorities = issuePriorities;
    }

    public static class IssuesPriorities{

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

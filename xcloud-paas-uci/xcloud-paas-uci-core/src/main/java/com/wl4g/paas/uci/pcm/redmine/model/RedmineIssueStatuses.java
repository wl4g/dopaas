/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.paas.uci.pcm.redmine.model;

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
/*
 * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
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
package com.wl4g.devops.vcs.operator.gitlab;

import com.wl4g.devops.vcs.operator.model.VcsTagModel;

/**
 * @author vjay
 * @date 2020-04-20 14:49:00
 */
public class GitlabV4TagModel extends VcsTagModel {

    private String message;
    private String target;
    private Commit commit;
    private Object release;

    public String getMessage() { return message; }
    public void setMessage(String value) { this.message = value; }

    public String getTarget() { return target; }
    public void setTarget(String value) { this.target = value; }

    public Commit getCommit() { return commit; }
    public void setCommit(Commit value) { this.commit = value; }

    public Object getRelease() { return release; }
    public void setRelease(Object value) { this.release = value; }

    public static class Commit {
        private String id;
        private String short_iD;
        private String created_at;
        private String[] parent_ids;
        private String title;
        private String message;
        private String author_name;
        private String author_email;
        private String authored_date;
        private String committer_name;
        private String committer_email;
        private String committed_date;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getShort_iD() {
            return short_iD;
        }

        public void setShort_iD(String short_iD) {
            this.short_iD = short_iD;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String[] getParent_ids() {
            return parent_ids;
        }

        public void setParent_ids(String[] parent_ids) {
            this.parent_ids = parent_ids;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getAuthor_name() {
            return author_name;
        }

        public void setAuthor_name(String author_name) {
            this.author_name = author_name;
        }

        public String getAuthor_email() {
            return author_email;
        }

        public void setAuthor_email(String author_email) {
            this.author_email = author_email;
        }

        public String getAuthored_date() {
            return authored_date;
        }

        public void setAuthored_date(String authored_date) {
            this.authored_date = authored_date;
        }

        public String getCommitter_name() {
            return committer_name;
        }

        public void setCommitter_name(String committer_name) {
            this.committer_name = committer_name;
        }

        public String getCommitter_email() {
            return committer_email;
        }

        public void setCommitter_email(String committer_email) {
            this.committer_email = committer_email;
        }

        public String getCommitted_date() {
            return committed_date;
        }

        public void setCommitted_date(String committed_date) {
            this.committed_date = committed_date;
        }
    }

}
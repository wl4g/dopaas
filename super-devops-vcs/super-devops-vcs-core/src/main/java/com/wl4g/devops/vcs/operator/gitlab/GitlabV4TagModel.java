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
        private String shortID;
        private String createdAt;
        private String[] parentIDS;
        private String title;
        private String message;
        private String authorName;
        private String authorEmail;
        private String authoredDate;
        private String committerName;
        private String committerEmail;
        private String committedDate;

        public String getID() { return id; }
        public void setID(String value) { this.id = value; }

        public String getShortID() { return shortID; }
        public void setShortID(String value) { this.shortID = value; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String value) { this.createdAt = value; }

        public String[] getParentIDS() { return parentIDS; }
        public void setParentIDS(String[] value) { this.parentIDS = value; }

        public String getTitle() { return title; }
        public void setTitle(String value) { this.title = value; }

        public String getMessage() { return message; }
        public void setMessage(String value) { this.message = value; }

        public String getAuthorName() { return authorName; }
        public void setAuthorName(String value) { this.authorName = value; }

        public String getAuthorEmail() { return authorEmail; }
        public void setAuthorEmail(String value) { this.authorEmail = value; }

        public String getAuthoredDate() { return authoredDate; }
        public void setAuthoredDate(String value) { this.authoredDate = value; }

        public String getCommitterName() { return committerName; }
        public void setCommitterName(String value) { this.committerName = value; }

        public String getCommitterEmail() { return committerEmail; }
        public void setCommitterEmail(String value) { this.committerEmail = value; }

        public String getCommittedDate() { return committedDate; }
        public void setCommittedDate(String value) { this.committedDate = value; }
    }

}

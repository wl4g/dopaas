/**
  * Copyright 2020 bejson.com 
  */
package com.wl4g.devops.ci.pmplatform.model.redmine;

import java.util.Date;
import java.util.List;

/**
 * Auto-generated: 2020-01-03 14:7:34
 *
 * @author 
 * @website 
 */
public class Issues extends BaseModel{

    private List<Issue> issues;

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }

    public static class Issue {

        private int id;
        private Field project;
        private Field tracker;
        private Field status;
        private Field priority;
        private Field author;
        private Field assigned_to;
        private String subject;
        private String description;
        private Date start_date;
        private Date due_date;
        private int done_ratio;
        private Date created_on;
        private Date updated_on;
        public void setId(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }

        public Field getProject() {
            return project;
        }

        public void setProject(Field project) {
            this.project = project;
        }

        public Field getTracker() {
            return tracker;
        }

        public void setTracker(Field tracker) {
            this.tracker = tracker;
        }

        public Field getStatus() {
            return status;
        }

        public void setStatus(Field status) {
            this.status = status;
        }

        public Field getPriority() {
            return priority;
        }

        public void setPriority(Field priority) {
            this.priority = priority;
        }

        public Field getAuthor() {
            return author;
        }

        public void setAuthor(Field author) {
            this.author = author;
        }

        public Field getAssigned_to() {
            return assigned_to;
        }

        public void setAssigned_to(Field assigned_to) {
            this.assigned_to = assigned_to;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }
        public String getSubject() {
            return subject;
        }

        public void setDescription(String description) {
            this.description = description;
        }
        public String getDescription() {
            return description;
        }

        public void setStart_date(Date start_date) {
            this.start_date = start_date;
        }
        public Date getStart_date() {
            return start_date;
        }

        public void setDue_date(Date due_date) {
            this.due_date = due_date;
        }
        public Date getDue_date() {
            return due_date;
        }

        public void setDone_ratio(int done_ratio) {
            this.done_ratio = done_ratio;
        }
        public int getDone_ratio() {
            return done_ratio;
        }

        public void setCreated_on(Date created_on) {
            this.created_on = created_on;
        }
        public Date getCreated_on() {
            return created_on;
        }

        public void setUpdated_on(Date updated_on) {
            this.updated_on = updated_on;
        }
        public Date getUpdated_on() {
            return updated_on;
        }

    }

}
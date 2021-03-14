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
package com.wl4g.dopaas.uci.pcm.redmine.model;

import java.util.Date;
import java.util.List;

/**
 * {@link RedmineIssues}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年1月7日 v1.0.0
 */
public class RedmineIssues extends BaseRedmine {

	private List<RedmineIssue> issues;

	public List<RedmineIssue> getIssues() {
		return issues;
	}

	public void setIssues(List<RedmineIssue> issues) {
		this.issues = issues;
	}

	public static class RedmineIssue {

		private int id;
		private RedmineField project;
		private RedmineField tracker;
		private RedmineField status;
		private RedmineField priority;
		private RedmineField author;
		private RedmineField assigned_to;
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

		public RedmineField getProject() {
			return project;
		}

		public void setProject(RedmineField project) {
			this.project = project;
		}

		public RedmineField getTracker() {
			return tracker;
		}

		public void setTracker(RedmineField tracker) {
			this.tracker = tracker;
		}

		public RedmineField getStatus() {
			return status;
		}

		public void setStatus(RedmineField status) {
			this.status = status;
		}

		public RedmineField getPriority() {
			return priority;
		}

		public void setPriority(RedmineField priority) {
			this.priority = priority;
		}

		public RedmineField getAuthor() {
			return author;
		}

		public void setAuthor(RedmineField author) {
			this.author = author;
		}

		public RedmineField getAssigned_to() {
			return assigned_to;
		}

		public void setAssigned_to(RedmineField assigned_to) {
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

	public static class RedmineField {

		private int id;
		private String name;

		public void setId(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}

}
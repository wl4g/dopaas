/**
  * Copyright 2020 bejson.com 
  */
package com.wl4g.devops.ci.pcm.redmine.model;

import java.util.Date;
import java.util.List;

/**
 * Auto-generated: 2020-01-03 14:6:40
 *
 * @author
 * @website
 */
public class RedmineProjects extends BaseRedmine {

	private List<Project> projects;

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	public static class Project {

		private int id;
		private String name;
		private String identifier;
		private String description;
		private int status;
		private boolean is_public;
		private Date created_on;
		private Date updated_on;

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

		public void setIdentifier(String identifier) {
			this.identifier = identifier;
		}

		public String getIdentifier() {
			return identifier;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public int getStatus() {
			return status;
		}

		public void setIs_public(boolean is_public) {
			this.is_public = is_public;
		}

		public boolean getIs_public() {
			return is_public;
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
/**
  * Copyright 2020 bejson.com 
  */
package com.wl4g.devops.ci.pcm.redmine.model;

import java.util.Date;
import java.util.List;

/**
 * Auto-generated: 2020-01-03 14:4:21
 *
 * @author
 * @website
 */
public class RedmineUsers extends BaseRedmine {

	private List<User> users;

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public static class User {

		private int id;
		private String login;
		private String firstname;
		private String lastname;
		private String mail;
		private Date created_on;
		private Date last_login_on;

		public void setId(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public void setLogin(String login) {
			this.login = login;
		}

		public String getLogin() {
			return login;
		}

		public void setFirstname(String firstname) {
			this.firstname = firstname;
		}

		public String getFirstname() {
			return firstname;
		}

		public void setLastname(String lastname) {
			this.lastname = lastname;
		}

		public String getLastname() {
			return lastname;
		}

		public void setMail(String mail) {
			this.mail = mail;
		}

		public String getMail() {
			return mail;
		}

		public void setCreated_on(Date created_on) {
			this.created_on = created_on;
		}

		public Date getCreated_on() {
			return created_on;
		}

		public void setLast_login_on(Date last_login_on) {
			this.last_login_on = last_login_on;
		}

		public Date getLast_login_on() {
			return last_login_on;
		}

	}

}
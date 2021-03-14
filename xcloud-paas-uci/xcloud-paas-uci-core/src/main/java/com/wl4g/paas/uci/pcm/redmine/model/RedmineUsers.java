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

import java.util.Date;
import java.util.List;

/**
 * {@link RedmineUsers}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年1月7日 v1.0.0
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
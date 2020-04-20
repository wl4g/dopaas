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
package com.wl4g.devops.iam.common.web.model;

import static com.wl4g.devops.support.redis.ScanCursor.CursorWrapper.parse;
import static com.wl4g.devops.tool.common.serialize.JacksonUtils.toJSONString;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.wl4g.devops.iam.common.authc.ClientRef;

/**
 * Session attribute model.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月6日
 * @since
 */
public class SessionAttributeModel implements Serializable {
	private static final long serialVersionUID = -245342258661231356L;

	/**
	 * Session cursor indexer.
	 */
	private CursorIndex index = new CursorIndex();

	/**
	 * Session attributes.
	 */
	private Collection<IamSessionInfo> sessions = new ArrayList<>(4);

	public SessionAttributeModel() {
		super();
	}

	public SessionAttributeModel(CursorIndex index, List<IamSessionInfo> sessions) {
		super();
		this.index = index;
		this.sessions = sessions;
	}

	public CursorIndex getIndex() {
		return index;
	}

	public void setIndex(CursorIndex index) {
		this.index = index;
	}

	public Collection<IamSessionInfo> getSessions() {
		return sessions;
	}

	public void setSessions(Collection<IamSessionInfo> sessions) {
		this.sessions = sessions;
	}

	/**
	 * Session attributes cursor index information.
	 *
	 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version v1.0.0 2019-11-06
	 * @since
	 */
	public static class CursorIndex implements Serializable {
		private static final long serialVersionUID = 8491557330003820999L;

		/**
		 * Cursor string.
		 */
		private String cursorString = EMPTY;

		/**
		 * Cursor has next records.
		 */
		private Boolean hasNext = false;

		public CursorIndex() {
			super();
		}

		public CursorIndex(Boolean hasNext) {
			this(null, hasNext);
		}

		public CursorIndex(String cursorString, Boolean hasNext) {
			setCursorString(cursorString);
			setHasNext(hasNext);
		}

		public String getCursorString() {
			return cursorString;
		}

		public void setCursorString(String cursorString) {
			if (isNotBlank(cursorString)) {
				parse(cursorString); // Check.
				this.cursorString = cursorString;
			}
		}

		public Boolean getHasNext() {
			return hasNext;
		}

		public void setHasNext(Boolean hasNext) {
			if (nonNull(hasNext)) {
				this.hasNext = hasNext;
			}
		}

		@Override
		public String toString() {
			return toJSONString(this);
		}

	}

	/**
	 * Session attribute information.
	 *
	 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version v1.0.0 2019-10-31
	 * @since
	 */
	public static class IamSessionInfo implements Serializable {
		private static final long serialVersionUID = 1990530522326712114L;

		private String id;
		private String startTime;
		private String stopTime;
		private String lastAccessTime;
		private boolean expired;
		private boolean authenticated;
		private String host;
		private Object principal;
		private Set<String> grants = new HashSet<>(4);
		private ClientRef clientRef = ClientRef.Unknown;
		private String oauth2Provider; // if exist

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getStartTime() {
			return startTime;
		}

		public void setStartTime(String startTimestamp) {
			this.startTime = startTimestamp;
		}

		public String getStopTime() {
			return stopTime;
		}

		public void setStopTime(String stopTimestamp) {
			this.stopTime = stopTimestamp;
		}

		public String getLastAccessTime() {
			return lastAccessTime;
		}

		public void setLastAccessTime(String lastAccessTime) {
			this.lastAccessTime = lastAccessTime;
		}

		public boolean isExpired() {
			return expired;
		}

		public void setExpired(boolean expired) {
			this.expired = expired;
		}

		public boolean isAuthenticated() {
			return authenticated;
		}

		public void setAuthenticated(boolean authenticated) {
			this.authenticated = authenticated;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public Object getPrincipal() {
			return principal;
		}

		public void setPrincipal(Object principal) {
			if (nonNull(principal)) {
				this.principal = principal;
			}
		}

		public Set<String> getGrants() {
			return grants;
		}

		public void setGrants(Set<String> grantApplications) {
			if (!isEmpty(grantApplications)) {
				this.grants.addAll(grantApplications);
			}
		}

		public ClientRef getClientRef() {
			return clientRef;
		}

		public void setClientRef(ClientRef clientRef) {
			this.clientRef = clientRef;
		}

		public String getOauth2Provider() {
			return oauth2Provider;
		}

		public void setOauth2Provider(String oauth2Provider) {
			this.oauth2Provider = oauth2Provider;
		}

		@Override
		public String toString() {
			return toJSONString(this);
		}

	}

}
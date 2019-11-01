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
package com.wl4g.devops.iam.web.api;

import com.wl4g.devops.common.bean.iam.GrantTicketInfo;
import com.wl4g.devops.common.bean.iam.model.SessionModel;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.iam.common.i18n.SessionDelegateMessageBundle;
import com.wl4g.devops.iam.common.session.IamSession;
import com.wl4g.devops.iam.common.session.mgt.IamSessionDAO;
import com.wl4g.devops.iam.config.properties.IamProperties;
import com.wl4g.devops.iam.handler.CentralAuthenticationHandler;
import com.wl4g.devops.support.cache.ScanCursor;

import static com.wl4g.devops.common.bean.iam.model.SessionModel.*;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.BEAN_DELEGATE_MSG_SOURCE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_API_V1_SESSION;
import static com.wl4g.devops.common.utils.serialize.JacksonUtils.toJSONString;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.shiro.web.subject.support.DefaultWebSubjectContext.*;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Generic metric API controller.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年10月31日
 * @since
 */
@com.wl4g.devops.iam.annotation.GenericApiV1Controller
@ResponseBody
public class GenericApiV1Controller extends BaseController {

	/** IAM properties configuration. */
	@Autowired
	protected IamProperties config;

	/**
	 * Session delegate message source bundle.
	 */
	@javax.annotation.Resource(name = BEAN_DELEGATE_MSG_SOURCE)
	protected SessionDelegateMessageBundle bundle;

	/** IAM session DAO. */
	@Autowired
	protected IamSessionDAO sessionDAO;

	/**
	 * Obtain sessions.</br>
	 * 
	 * For example response:
	 * 
	 * <pre>
	 *{
	 *  "code": 200,
	 *  "status": "normal",
	 *  "message": "ok",
	 *  "data": {
	 *    "sessions": [
	 *      {
	 *        "id": "sid2e5dae956fd2489b91f1706a63a5e26b",
	 *        "startTimestamp": 1572596091090,
	 *        "stopTimestamp": null,
	 *        "lastAccessTime": 1572596812856,
	 *        "timeout": 1800000,
	 *        "expired": false,
	 *        "authenticated": false,
	 *        "host": "0:0:0:0:0:0:0:1",
	 *        "principal": null,
	 *        "grantApplications": [
	 *            
	 *        ]
	 *      },
	 *      {
	 *        "id": "sid0f0722a6c3a046c18564b279b7fab2b9",
	 *        "startTimestamp": 1572596092031,
	 *        "stopTimestamp": null,
	 *        "lastAccessTime": 1572596092272,
	 *        "timeout": 1800000,
	 *        "expired": false,
	 *        "authenticated": true,
	 *        "host": "0:0:0:0:0:0:0:1",
	 *        "principal": "root",
	 *        "grantApplications": [
	 *            "umc-manager"
	 *         ]
	 *      }
	 *    ]
	 *  }
	 *}
	 * </pre>
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@GetMapping(path = URI_S_API_V1_SESSION)
	public RespBase<?> getSessions(SessionQuery query) throws Exception {
		RespBase<List<SessionModel>> resp = RespBase.create();
		if (log.isInfoEnabled()) {
			log.info("Get sessions by <= {}", query);
		}

		// Scan active sessions all.
		try (ScanCursor<IamSession> cursor = sessionDAO.getActiveSessions(query.getLimit())) {
			List<SessionModel> ss = cursor.readValues().stream().map(s -> convertToSessionModel(s)).collect(toList());
			resp.getData().put(KEY_SESSIONS, ss);
		}

		if (log.isInfoEnabled()) {
			log.info("Get sessions => {}", resp);
		}
		return resp;
	}

	/**
	 * Cleanup remove session.
	 *
	 * @param query
	 * @return
	 * @throws Exception
	 */
	@DeleteMapping(path = URI_S_API_V1_SESSION)
	public RespBase<?> deleteSession(SessionQuery query) throws Exception {
		// TODO
		return null;
	}

	/**
	 * Convert {@link IamSession} to {@link SessionModel}. </br>
	 * </br>
	 * 
	 * <b>Origin {@link IamSession} json string example:</b>
	 * 
	 * <pre>
	 *	{
	 *	  "code": 200,
	 *	  "status": "normal",
	 *	  "message": "ok",
	 *	  "data": {
	 *	    "sessions": [
	 *	      {
	 *	        "id": "sid4c034ff4e95741dcb3b20f687c952cd4",
	 *	        "startTimestamp": 1572593959441,
	 *	        "stopTimestamp": null,
	 *	        "lastAccessTime": 1572593993963,
	 *	        "timeout": 1800000,
	 *	        "expired": false,
	 *	        "host": "0:0:0:0:0:0:0:1",
	 *	        "attributes": {
	 *	          "org.apache.shiro.subject.support.DefaultSubjectContext_AUTHENTICATED_SESSION_KEY": true,
	 *	          "authcTokenAttributeKey": {
	 *	            "principals": {
	 *	              "empty": false,
	 *	              "primaryPrincipal": "root",
	 *	              "realmNames": [
	 *	                "com.wl4g.devops.iam.realm.GeneralAuthorizingRealm_0"
	 *	              ]
	 *	            },
	 *	            "credentials": "911ef082b5de81151ba25d8442efb6e77bb380fd36ac349ee737ee5461ae6d3e8a13e4366a20e6dd71f95e8939fe375e203577568297cdbc34d598dd47475a7c",
	 *	            "credentialsSalt": null,
	 *	            "accountInfo": {
	 *	              "principal": "root",
	 *	              "storedCredentials": "911ef082b5de81151ba25d8442efb6e77bb380fd36ac349ee737ee5461ae6d3e8a13e4366a20e6dd71f95e8939fe375e203577568297cdbc34d598dd47475a7c"
	 *	            }
	 *	          },
	 *	          "CentralAuthenticationHandler.GRANT_TICKET": {
	 *	            "applications": {
	 *	              "umc-manager": "stzgotzYWGdweoBGgEOtDKpXwJsxyEaqCrttfMSgFMYkZuIWrDWNpzPYWFa"
	 *	            }
	 *	          },
	 *	          "org.apache.shiro.subject.support.DefaultSubjectContext_PRINCIPALS_SESSION_KEY": {
	 *	            "empty": false,
	 *	            "primaryPrincipal": "root",
	 *	            "realmNames": [
	 *	              "com.wl4g.devops.iam.realm.GeneralAuthorizingRealm_0"
	 *	            ]
	 *	          }
	 *	        }
	 *	      }
	 *	    ]
	 *	  }
	 *	}
	 * </pre>
	 * 
	 * @param s
	 * @return
	 */
	private SessionModel convertToSessionModel(IamSession s) {
		SessionModel sm = new SessionModel();
		sm.setId(s.getId());
		sm.setLastAccessTime(s.getLastAccessTime());
		sm.setStartTimestamp(s.getStartTimestamp());
		sm.setStopTimestamp(s.getStopTimestamp());
		sm.setHost(s.getHost());
		sm.setExpired(s.isExpired());
		sm.setTimeout(s.getTimeout());

		// Authenticated.
		Object authenticated = s.getAttribute(AUTHENTICATED_SESSION_KEY);
		sm.setAuthenticated(false);
		if (nonNull(authenticated)) {
			if (authenticated instanceof Boolean || authenticated.getClass() == boolean.class) {
				sm.setAuthenticated((Boolean) authenticated);
			} else {
				sm.setAuthenticated(Boolean.parseBoolean((String) authenticated));
			}
		}

		// Authenticate principal.
		PrincipalCollection principals = (PrincipalCollection) s.getAttribute(PRINCIPALS_SESSION_KEY);
		if (nonNull(principals)) {
			sm.setPrincipal(principals.getPrimaryPrincipal());
		}

		// Application grant info.
		GrantTicketInfo grantInfo = (GrantTicketInfo) s.getAttribute(CentralAuthenticationHandler.GRANT_APP_INFO_KEY);
		if (nonNull(grantInfo) && grantInfo.hasApplications()) {
			sm.setGrantApplications(grantInfo.getApplications().keySet());
		}

		return sm;
	}

	/**
	 * Sessions query model.
	 * 
	 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version v1.0.0 2019-10-31
	 * @since
	 */
	public static class SessionQuery implements Serializable {
		private static final long serialVersionUID = 5766036036946339544L;

		/** Scan cursor. */
		@NotBlank
		private String cursor;

		/** Page size. */
		private int limit = 200;

		public String getCursor() {
			return cursor;
		}

		public void setCursor(String cursor) {
			this.cursor = cursor;
		}

		public int getLimit() {
			return limit;
		}

		public void setLimit(int limit) {
			this.limit = limit;
		}

		@Override
		public String toString() {
			return toJSONString(this);
		}

	}

	/**
	 * Sessions destroy model.
	 * 
	 * @author Wangl.sir &lt;Wanglsir@gmail.com, 983708408@qq.com&gt;
	 * @version v1.0.0 2019-10-31
	 * @since
	 */
	public static class SessionDestroy implements Serializable {
		private static final long serialVersionUID = 2579844578836104918L;

		private String sessionId;

		public String getSessionId() {
			return sessionId;
		}

		public void setSessionId(String sessionId) {
			this.sessionId = sessionId;
		}

		@Override
		public String toString() {
			return toJSONString(this);
		}

	}

}
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
package com.wl4g.devops.iam.common.web;

import com.google.common.annotations.Beta;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.iam.common.config.AbstractIamProperties;
import com.wl4g.devops.iam.common.i18n.SessionDelegateMessageBundle;
import com.wl4g.devops.iam.common.session.IamSession;
import com.wl4g.devops.iam.common.session.mgt.IamSessionDAO;
import com.wl4g.devops.iam.common.web.model.SessionAttributeModel;
import com.wl4g.devops.iam.common.web.model.SessionDestroyModel;
import com.wl4g.devops.iam.common.web.model.SessionQueryModel;
import com.wl4g.devops.support.cache.ScanCursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import static com.wl4g.devops.support.cache.ScanCursor.CursorWrapper.*;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.BEAN_DELEGATE_MSG_SOURCE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_API_V1_SESSION;
import static com.wl4g.devops.iam.common.web.model.SessionAttributeModel.CursorIndex;
import static com.wl4g.devops.iam.common.web.model.SessionAttributeModel.SessionAttribute;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.shiro.web.subject.support.DefaultWebSubjectContext.AUTHENTICATED_SESSION_KEY;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Generic abstract API controller.
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年10月31日
 * @since
 */
@Beta
@ResponseBody
public abstract class GenericApiController extends BaseController {

	/**
	 * IAM properties configuration.
	 */
	@Autowired
	protected AbstractIamProperties<?> config;

	/**
	 * Session delegate message source bundle.
	 */
	@javax.annotation.Resource(name = BEAN_DELEGATE_MSG_SOURCE)
	protected SessionDelegateMessageBundle bundle;

	/**
	 * IAM session DAO.
	 */
	@Autowired
	protected IamSessionDAO sessionDAO;

	/**
	 * Iterative scan gets the list of access sessions (including all clients
	 * and authenticated and uncertified sessions).</br>
	 * <p>
	 * For example response:
	 *
	 * <pre>
	 * {
	 *   "code": 200,
	 *   "status": "Normal",
	 *   "message": "Ok",
	 *   "data": {
	 *     "index": {
	 *       "cursorString": "0@5",
	 *       "hasNext": false
	 *     },
	 *     "sessions": [
	 *       {
	 *         "id": "sidad6d3cbae8e24b1488f439845b1c3540",
	 *         "startTimestamp": 1573200604837,
	 *         "stopTimestamp": null,
	 *         "lastAccessTime": 1573200612065,
	 *         "timeout": 2592000000,
	 *         "expired": false,
	 *         "authenticated": true,
	 *         "host": "127.0.0.1",
	 *         "principal": "admin2",
	 *         "grants": [
	 *           "portal",
	 *           "base"
	 *         ],
	 *         "clientRef": "WINDOWS",
	 *         "oauth2Provider": null
	 *       },
	 *       {
	 *         "id": "sidc14af9cc44374d0c810d5a3d948766fb",
	 *         "startTimestamp": 1573200412703,
	 *         "stopTimestamp": null,
	 *         "lastAccessTime": 1573200412703,
	 *         "timeout": 2592000000,
	 *         "expired": false,
	 *         "authenticated": false,
	 *         "host": "127.0.0.1",
	 *         "principal": null,
	 *         "grants": [
	 *           
	 *         ],
	 *         "clientRef": "WINDOWS",
	 *         "oauth2Provider": null
	 *       ]
	 *     }
	 *  }
	 * </pre>
	 */
	@GetMapping(path = URI_S_API_V1_SESSION)
	public RespBase<?> getSessions(@Validated SessionQueryModel query) throws Exception {
		RespBase<Object> resp = RespBase.create();
		if (log.isInfoEnabled()) {
			log.info("Get sessions by <= {}", query);
		}

		// Priority search principal.
		if (!isBlank(query.getPrincipal())) {
			Collection<IamSession> ss = sessionDAO.getAccessSessions(query.getPrincipal());
			List<SessionAttribute> sas = ss.stream().map(s -> wrapSessionAttribute(s)).collect(toList());
			resp.setData(new SessionAttributeModel(new CursorIndex(false), sas));
		} else {
			// Do scan sessions all.
			ScanCursor<IamSession> sc = sessionDAO.getAccessSessions(parse(query.getCursor()), query.getLimit());
			// Convert to SessionAttribute.
			List<SessionAttribute> sas = sc.readValues().stream().map(s -> wrapSessionAttribute(s)).collect(toList());
			// Setup response attributes.
			CursorIndex index = new CursorIndex(sc.getCursor().getCursorString(), sc.getCursor().getHasNext());
			resp.setData(new SessionAttributeModel(index, sas));
		}

		if (log.isInfoEnabled()) {
			log.info("Get sessions => {}", resp.asJson());
		}
		return resp;
	}

	/**
	 * Destroy cleanup session.
	 *
	 * @param destroy
	 * @return
	 * @throws Exception
	 */
	@PostMapping(path = URI_S_API_V1_SESSION)
	public RespBase<?> destroySessions(@Validated @RequestBody SessionDestroyModel destroy) throws Exception {
		RespBase<String> resp = RespBase.create();
		if (log.isInfoEnabled()) {
			log.info("Destroy sessions by <= {}", destroy);
		}

		// Destroy with sessionIds.
		if (!isEmpty(destroy.getSessionIds())) {
			for (Serializable sessionId : destroy.getSessionIds()) {
				sessionDAO.delete(new IamSession(sessionId));
			}
		}

		// Destroy with principal.
		if (!isBlank(destroy.getPrincipal())) {
			sessionDAO.removeAccessSession(destroy.getPrincipal());
		}

		if (log.isInfoEnabled()) {
			log.info("Destroy sessions => {}", resp);
		}
		return resp;
	}

	/**
	 * Convert wrap {@link IamSession} to {@link SessionAttributeModel}. </br>
	 * </br>
	 *
	 * <b>Origin {@link IamSession} json string example:</b>
	 *
	 * <pre>
	 *    {
	 * 	  "code": 200,
	 * 	  "status": "normal",
	 * 	  "message": "ok",
	 * 	  "data": {
	 * 	    "sessions": [
	 *          {
	 * 	        "id": "sid4c034ff4e95741dcb3b20f687c952cd4",
	 * 	        "startTimestamp": 1572593959441,
	 * 	        "stopTimestamp": null,
	 * 	        "lastAccessTime": 1572593993963,
	 * 	        "timeout": 1800000,
	 * 	        "expired": false,
	 * 	        "host": "0:0:0:0:0:0:0:1",
	 * 	        "attributes": {
	 * 	          "org.apache.shiro.subject.support.DefaultSubjectContext_AUTHENTICATED_SESSION_KEY": true,
	 * 	          "authcTokenAttributeKey": {
	 * 	            "principals": {
	 * 	              "empty": false,
	 * 	              "primaryPrincipal": "root",
	 * 	              "realmNames": [
	 * 	                "com.wl4g.devops.iam.realm.GeneralAuthorizingRealm_0"
	 * 	              ]
	 *                },
	 * 	            "credentials": "911ef082b5de81151ba25d8442efb6e77bb380fd36ac349ee737ee5461ae6d3e8a13e4366a20e6dd71f95e8939fe375e203577568297cdbc34d598dd47475a7c",
	 * 	            "credentialsSalt": null,
	 * 	            "accountInfo": {
	 * 	              "principal": "root",
	 * 	              "storedCredentials": "911ef082b5de81151ba25d8442efb6e77bb380fd36ac349ee737ee5461ae6d3e8a13e4366a20e6dd71f95e8939fe375e203577568297cdbc34d598dd47475a7c"
	 *                }
	 *              },
	 * 	          "CentralAuthenticationHandler.GRANT_TICKET": {
	 * 	            "applications": {
	 * 	              "umc-manager": "stzgotzYWGdweoBGgEOtDKpXwJsxyEaqCrttfMSgFMYkZuIWrDWNpzPYWFa"
	 *                }
	 *              },
	 * 	          "org.apache.shiro.subject.support.DefaultSubjectContext_PRINCIPALS_SESSION_KEY": {
	 * 	            "empty": false,
	 * 	            "primaryPrincipal": "root",
	 * 	            "realmNames": [
	 * 	              "com.wl4g.devops.iam.realm.GeneralAuthorizingRealm_0"
	 * 	            ]
	 *              }
	 *            }
	 *          }
	 * 	    ]
	 *      }
	 *    }
	 * </pre>
	 *
	 * @param session
	 * @return
	 */
	protected SessionAttribute wrapSessionAttribute(IamSession session) {
		SessionAttribute sa = new SessionAttribute();
		sa.setId(String.valueOf(session.getId()));
		sa.setLastAccessTime(session.getLastAccessTime());
		sa.setStartTime(session.getStartTimestamp());
		sa.setStopTime(session.getStopTimestamp());
		sa.setHost(session.getHost());
		sa.setExpired(session.isExpired());

		// Authentication status.
		Object authenticated = session.getAttribute(AUTHENTICATED_SESSION_KEY);
		sa.setAuthenticated(false);
		if (nonNull(authenticated)) {
			if (authenticated instanceof Boolean || authenticated.getClass() == boolean.class) {
				sa.setAuthenticated((Boolean) authenticated);
			} else {
				sa.setAuthenticated(Boolean.parseBoolean((String) authenticated));
			}
		}

		// Authentication principal.
		sa.setPrincipal(session.getPrimaryPrincipal());

		return sa;
	}

}
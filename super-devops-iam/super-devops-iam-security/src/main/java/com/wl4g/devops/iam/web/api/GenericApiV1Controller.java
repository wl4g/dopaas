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

import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.iam.common.i18n.SessionDelegateMessageBundle;
import com.wl4g.devops.iam.common.session.IamSession;
import com.wl4g.devops.iam.common.session.mgt.IamSessionDAO;
import com.wl4g.devops.iam.config.properties.IamProperties;
import com.wl4g.devops.iam.web.model.SessionModel;
import com.wl4g.devops.support.cache.ScanCursor;

import static com.wl4g.devops.iam.web.model.SessionModel.*;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.BEAN_DELEGATE_MSG_SOURCE;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_API_V1_SESSIONS;
import static com.wl4g.devops.common.utils.serialize.JacksonUtils.toJSONString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
	 * Obtain sessions.
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@GetMapping(path = URI_S_API_V1_SESSIONS)
	public RespBase<?> getSessions(SessionQuery query) throws Exception {
		RespBase<List<SessionModel>> resp = RespBase.create();
		if (log.isInfoEnabled()) {
			log.info("Get sessions by <= {}", query);
		}

		// Obtain scan active sessions.
		ScanCursor<IamSession> cursor = sessionDAO.getActiveSessions(200);
		List<SessionModel> ss = new ArrayList<SessionModel>(128);
		while (cursor.hasNext()) {
			IamSession s = cursor.next();
			ss.add(new SessionModel(s));
		}
		resp.getData().put(KEY_SESSIONS, ss);
		return resp;
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

		private int pageSize = 200;

		public int getPageSize() {
			return pageSize;
		}

		public void setPageSize(int pageSize) {
			this.pageSize = pageSize;
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
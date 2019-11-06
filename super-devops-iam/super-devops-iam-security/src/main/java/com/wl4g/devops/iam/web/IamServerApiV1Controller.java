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
package com.wl4g.devops.iam.web;

import com.wl4g.devops.common.bean.iam.GrantTicketInfo;
import com.wl4g.devops.iam.common.annotation.IamApiV1Controller;
import com.wl4g.devops.iam.common.session.IamSession;
import com.wl4g.devops.iam.common.web.GenericApiController;
import com.wl4g.devops.iam.common.web.model.SessionAttributeModel;
import com.wl4g.devops.iam.handler.CentralAuthenticationHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import static java.util.Objects.nonNull;

/**
 * IAM server API v1 controller.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年10月31日
 * @since
 */
@IamApiV1Controller
@ResponseBody
public class IamServerApiV1Controller extends GenericApiController {

	/**
	 * Convert wrap {@link IamSession} to {@link SessionAttributeModel}. </br>
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
	 * @param session
	 * @return
	 */
	@Override
	protected SessionAttributeModel wrapSessionAttribute(IamSession session) {
		SessionAttributeModel sa = super.wrapSessionAttribute(session);

		// Application grant info.
		GrantTicketInfo grantInfo = (GrantTicketInfo) session.getAttribute(CentralAuthenticationHandler.GRANT_APP_INFO_KEY);
		if (nonNull(grantInfo) && grantInfo.hasApplications()) {
			sa.setGrantApplications(grantInfo.getApplications().keySet());
		}
		return sa;
	}

}
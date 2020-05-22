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
package com.wl4g.devops.iam.client.validation;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestTemplate;

import com.wl4g.devops.common.exception.iam.IllegalApplicationAccessException;
import com.wl4g.devops.common.exception.iam.InvalidGrantTicketException;
import com.wl4g.devops.common.exception.iam.TicketValidateException;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;
import com.wl4g.devops.iam.client.config.IamClientProperties;
import com.wl4g.devops.iam.common.authc.model.TicketValidatedAssertModel;
import com.wl4g.devops.iam.common.authc.model.TicketValidateModel;
import com.wl4g.devops.iam.common.subject.SimplePrincipalInfo;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_VALIDATE;

import java.util.Map;

/**
 * Fast-CAS ticket validator
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月29日
 * @since
 */
public class FastCasTicketIamValidator
		extends AbstractBasedIamValidator<TicketValidateModel, TicketValidatedAssertModel<SimplePrincipalInfo>> {

	public FastCasTicketIamValidator(IamClientProperties config, RestTemplate restTemplate) {
		super(config, restTemplate);
	}

	@Override
	protected void postQueryParameterSet(TicketValidateModel req, Map<String, Object> queryParams) {
		queryParams.put(config.getParam().getGrantTicket(), req.getTicket());
	}

	@Override
	public TicketValidatedAssertModel<SimplePrincipalInfo> validate(TicketValidateModel req) throws TicketValidateException {
		final RespBase<TicketValidatedAssertModel<SimplePrincipalInfo>> resp = doIamRemoteValidate(URI_S_VALIDATE, req);
		if (!RespBase.isSuccess(resp)) {
			// Only if the error is not authenticated, can it be redirected to
			// the IAM server login page, otherwise the client will display the
			// error page directly (to prevent unlimited redirection).
			/** See:{@link CentralAuthenticatorController#validate()} */
			if (RespBase.eq(resp, RetCode.UNAUTHC)) {
				throw new InvalidGrantTicketException(resp.getMessage());
			} else if (RespBase.eq(resp, RetCode.UNAUTHZ)) {
				throw new IllegalApplicationAccessException(resp.getMessage());
			}
			throw new TicketValidateException(resp != null ? resp.getMessage() : "Unknown error");
		}
		return resp.getData();
	}

	@Override
	protected ParameterizedTypeReference<RespBase<TicketValidatedAssertModel<SimplePrincipalInfo>>> getTypeReference() {
		return new ParameterizedTypeReference<RespBase<TicketValidatedAssertModel<SimplePrincipalInfo>>>() {
		};
	}

}
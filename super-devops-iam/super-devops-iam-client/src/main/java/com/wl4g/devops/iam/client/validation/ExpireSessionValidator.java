package com.wl4g.devops.iam.client.validation;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestTemplate;

import com.wl4g.devops.common.bean.iam.model.SessionValidationAssertion;
import com.wl4g.devops.common.exception.iam.InvalidGrantTicketException;
import com.wl4g.devops.common.exception.iam.SessionValidateException;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;
import com.wl4g.devops.iam.client.config.IamClientProperties;

import static com.wl4g.devops.common.constants.IAMDevOpsConstants.KEY_SESSION_VALID_ASSERT;
import static com.wl4g.devops.common.constants.IAMDevOpsConstants.URI_S_SESSION_VALIDATE;

import java.util.Map;

/**
 * Expire session validator
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月29日
 * @since
 */
public class ExpireSessionValidator extends AbstractBasedValidator<SessionValidationAssertion, SessionValidationAssertion> {

	public ExpireSessionValidator(IamClientProperties config, RestTemplate restTemplate) {
		super(config, restTemplate);
	}

	@Override
	protected void postQueryParameterSet(SessionValidationAssertion req, Map<String, Object> queryParams) {

	}

	@Override
	public SessionValidationAssertion validate(SessionValidationAssertion request) throws SessionValidateException {
		final RespBase<SessionValidationAssertion> resp = this.doGetRemoteValidate(URI_S_SESSION_VALIDATE, request);
		if (!RespBase.isSuccess(resp)) {
			if (RespBase.eq(resp, RetCode.UNAUTHC)) {
				throw new InvalidGrantTicketException(String.format("Remote validate error, %s", resp.getMessage()));
			}
			throw new SessionValidateException(String.format("Remote validate error, %s", resp.getMessage()));
		}
		return resp.getData().get(KEY_SESSION_VALID_ASSERT);
	}

	@Override
	protected ParameterizedTypeReference<RespBase<SessionValidationAssertion>> getTypeReference() {
		return new ParameterizedTypeReference<RespBase<SessionValidationAssertion>>() {
		};
	}

}

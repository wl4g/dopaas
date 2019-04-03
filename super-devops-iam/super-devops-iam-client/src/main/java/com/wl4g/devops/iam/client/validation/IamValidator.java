package com.wl4g.devops.iam.client.validation;

import com.wl4g.devops.common.bean.iam.model.BasedModel;
import com.wl4g.devops.common.exception.iam.ValidateException;

/**
 * IAM validator
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年3月31日
 * @since
 * @param <R>
 * @param <A>
 */
public interface IamValidator<R extends BasedModel, A> {

	/**
	 * Do execute validation
	 * 
	 * @param request
	 * @return
	 * @throws ValidateException
	 */
	A validate(R request) throws ValidateException;

}

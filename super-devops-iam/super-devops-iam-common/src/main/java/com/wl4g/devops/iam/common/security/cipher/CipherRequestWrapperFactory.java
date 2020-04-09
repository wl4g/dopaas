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
package com.wl4g.devops.iam.common.security.cipher;

import javax.servlet.http.HttpServletRequest;

import com.wl4g.devops.iam.common.config.AbstractIamProperties;
import com.wl4g.devops.iam.common.config.AbstractIamProperties.ParamProperties;

/**
 * Can be used to extend and create a custom {@link CipherRequestWrapper}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2020年3月28日 v1.0.0
 * @see
 */
public interface CipherRequestWrapperFactory {

	/**
	 * New create {@link CipherRequestWrapper} instance.
	 * 
	 * @param config
	 * @param request
	 * @return
	 */
	default CipherRequestWrapper newRequestWrapper(AbstractIamProperties<? extends ParamProperties> config,
			HttpServletRequest request) {
		return new AesCipherRequestWrapper(config, request);
	}

}

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
package com.wl4g.devops.doc.plugin.swagger.springfox.plugin;

import static com.wl4g.component.common.web.WebUtils2.toQueryParams;

import java.util.Set;

import com.fasterxml.classmate.TypeResolver;

import springfox.documentation.service.ParameterType;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spring.web.readers.operation.AbstractOperationParameterRequestConditionReader;
import springfox.documentation.spring.wrapper.NameValueExpression;

/**
 * {@link VersionedApiListingPlugin}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-08
 * @sine v1.0
 * @see
 */
public class VersionedApiListingPlugin extends AbstractOperationParameterRequestConditionReader {

	public VersionedApiListingPlugin(TypeResolver resolver) {
		super(resolver);
	}

	@Override
	public boolean supports(DocumentationType delimiter) {
		return delimiter == DocumentationType.SWAGGER_2 || delimiter == DocumentationType.OAS_30;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void apply(OperationContext context) {
		Set<NameValueExpression<String>> params = context.params();

		// Addidition version info to query parameters.
		toQueryParams(context.requestMappingPattern()).forEach((key, value) -> {
			params.add(new NameValueExpression<String>() {

				@Override
				public boolean isNegated() {
					return false;
				}

				@Override
				public String getValue() {
					return value;
				}

				@Override
				public String getName() {
					return key;
				}
			});
		});

		context.operationBuilder().parameters(getParameters(params, "query"))
				.requestParameters(getRequestParameters(params, ParameterType.QUERY));
	}

}

/*
 * Copyright (C) 2017 ~ 2025 the original author or authors.
 * <Wanglsir@gmail.com, 983708408@qq.com> Technology CO.LTD.
 * All rights reserved.
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
 * 
 * Reference to website: http://wl4g.com
 */
package com.wl4g.devops.doc.plugin.swagger.springfox.plugin;

import com.google.common.collect.Lists;

import static com.wl4g.components.common.lang.StringUtils2.eqIgnCase;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Optional;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import springfox.documentation.service.StringVendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;

/**
 * {@link AuthorOperationBuilderPlugin}
 * 
 * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version v1.0 2020-12-14
 * @sine v1.0
 * @see
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 101)
public class AuthorOperationBuilderPlugin implements OperationBuilderPlugin {

	@Override
	public boolean supports(DocumentationType delimiter) {
		return true;
	}

	/**
	 * Sort orders of extension APIs.
	 */
	@Override
	public void apply(OperationContext context) {
		Optional<ApiOperationExtension> extOpt = context.findAnnotation(ApiOperationExtension.class);
		if (extOpt.isPresent()) {
			String author = extOpt.get().author();
			if (!isBlank(author) && !eqIgnCase("null", author)) {
				context.operationBuilder().extensions(Lists.newArrayList(new StringVendorExtension("x-author", author)));
			}
		}
	}

}

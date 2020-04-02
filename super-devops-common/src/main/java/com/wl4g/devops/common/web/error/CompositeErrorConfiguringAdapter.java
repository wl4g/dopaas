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
package com.wl4g.devops.common.web.error;

import com.wl4g.devops.tool.common.collection.RegisteredUnmodifiableList;

import org.springframework.core.annotation.Order;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.wl4g.devops.common.web.RespBase.RetCode.*;
import static java.util.Collections.sort;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.Assert.state;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Composite error configure adapter.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月1日
 * @since
 */
public class CompositeErrorConfiguringAdapter implements ErrorConfiguring {

	/**
	 * Error configures.
	 */
	final protected List<ErrorConfiguring> errorConfigures = new RegisteredUnmodifiableList<>(new ArrayList<>());

	public CompositeErrorConfiguringAdapter(List<ErrorConfiguring> configures) {
		Assert.state(!isEmpty(configures), "Error configures has at least one.");
		// Sort by order.
		sort(configures, (o1, o2) -> {
			Order order1 = findAnnotation(o1.getClass(), Order.class);
			Order order2 = findAnnotation(o2.getClass(), Order.class);
			notNull(order1, "ErrorConfigure implements must Order must be annotated.");
			notNull(order2, "ErrorConfigure implements must Order must be annotated.");
			int compare = order1.value() - order2.value();
			state(compare != 0, String.format("ErrorConfigure implements %s:%s and %s:%s order conflict!", o1.getClass(),
					order1.value(), o2.getClass(), order2.value()));
			return compare;
		});
		this.errorConfigures.addAll(configures);
	}

	@Override
	public Integer getStatus(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model, Exception ex) {
		for (ErrorConfiguring c : errorConfigures) {
			Integer status = c.getStatus(request, response, model, ex);
			if (nonNull(status)) {
				return status;
			}
		}
		// Fallback.
		return SYS_ERR.getErrcode();
	}

	@Override
	public String getRootCause(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model,
			Exception ex) {
		for (ErrorConfiguring c : errorConfigures) {
			String errmsg = c.getRootCause(request, response, model, ex);
			if (!isBlank(errmsg)) {
				return errmsg;
			}
		}
		// Fallback.
		return "Unknown error";
	}

}
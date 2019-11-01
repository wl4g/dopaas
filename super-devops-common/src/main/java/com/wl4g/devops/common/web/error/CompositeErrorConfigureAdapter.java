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

import static org.springframework.util.CollectionUtils.isEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import com.wl4g.devops.common.utils.lang.OnceModifiableList;

/**
 * Composite error configure adapter.
 * 
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2019年11月1日
 * @since
 */
public class CompositeErrorConfigureAdapter implements ErrorConfigure {

	/**
	 * Error configures.
	 */
	final protected List<ErrorConfigure> errorConfigures = new OnceModifiableList<>(new ArrayList<>());

	public CompositeErrorConfigureAdapter(List<ErrorConfigure> configures) {
		Assert.state(!isEmpty(configures), "Error configures has at least one.");
		this.errorConfigures.addAll(configures);
	}

	@Override
	public HttpStatus getStatus(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model,
			Exception ex) {
		// TODO
		return null;
	}

	@Override
	public String getCause(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model, Exception ex) {
		return null;
	}

}

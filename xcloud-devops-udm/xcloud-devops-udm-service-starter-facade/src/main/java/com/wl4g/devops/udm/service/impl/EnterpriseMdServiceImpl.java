// Generated by XCloud PaaS for Codegen, refer: http://dts.devops.wl4g.com

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

package com.wl4g.devops.udm.service.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wl4g.devops.common.bean.udm.EnterpriseDocument;
import com.wl4g.devops.udm.service.EnterpriseMdService;
import com.wl4g.devops.udm.service.formater.Md2Html;

import freemarker.template.TemplateException;

/**
 * service implements of {@link EnterpriseDocument}
 *
 * @author root
 * @version 0.0.1-SNAPSHOT
 * @Date
 * @since v1.0
 */
@Service
public class EnterpriseMdServiceImpl implements EnterpriseMdService {

	@Autowired
	private Md2Html md2Html;

	@Override
	public String mdToHtml(String md) throws IOException, TemplateException {
		return md2Html.mdToHtml(md);
	}

	@Override
	public String formatTemplate(String md, String template) throws Exception {
		return md2Html.formatTemplate(template, md);
	}
}

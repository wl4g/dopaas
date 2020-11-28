/*
 * Copyright 2017 ~ 2050 the original author or authors <Wanglsir@gmail.com, 983708408@qq.com>.
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
package com.wl4g.devops.ci.service.impl;

import com.wl4g.components.core.bean.model.PageModel;
import com.wl4g.devops.ci.data.AnalysisHistoryDao;
import com.wl4g.devops.ci.service.AnalysisHistoryService;
import com.wl4g.devops.common.bean.ci.AnalysisHistory;

import static com.wl4g.iam.core.utils.IamOrganizationHolder.getRequestOrganizationCodes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;

/**
 * @author vjay
 * @date 2019-12-16 16:13:00
 */
@Service
public class AnalysisHistoryServiceImpl implements AnalysisHistoryService {

	@Autowired
	private AnalysisHistoryDao analysisHistoryDao;

	@Override
	public PageModel<AnalysisHistory> list(PageModel<AnalysisHistory> pm) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		pm.setRecords(analysisHistoryDao.list(getRequestOrganizationCodes()));
		return pm;
	}

}
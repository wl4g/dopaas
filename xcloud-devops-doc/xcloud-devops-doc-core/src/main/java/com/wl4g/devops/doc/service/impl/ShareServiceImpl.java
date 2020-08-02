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
package com.wl4g.devops.doc.service.impl;

import com.github.pagehelper.PageHelper;
import com.wl4g.devops.common.bean.doc.FileChanges;
import com.wl4g.devops.common.bean.doc.Share;
import com.wl4g.devops.common.constants.DocDevOpsConstants;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.dao.doc.ShareDao;
import com.wl4g.devops.doc.config.DocProperties;
import com.wl4g.devops.doc.service.DocService;
import com.wl4g.devops.doc.service.ShareService;
import com.wl4g.devops.page.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.wl4g.devops.common.web.RespBase.RetCode.NOT_FOUND_ERR;
import static com.wl4g.devops.common.web.RespBase.RetCode.UNAUTHC;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

/**
 * @author vjay
 * @date 2020-02-19 16:23:00
 */
@Service
public class ShareServiceImpl implements ShareService {

	@Autowired
	private ShareDao shareDao;

	@Autowired
	private DocService docService;

	@Autowired
	private DocProperties docProperties;

	@Override
	public PageModel list(PageModel pm) {
		pm.page(PageHelper.startPage(pm.getPageNum(), pm.getPageSize(), true));
		List<Share> list = shareDao.list();
		for (Share share : list) {
			FileChanges fileChanges = docService.getLastByDocCode(share.getDocCode());
			share.setName(fileChanges.getName());
		}
		pm.setRecords(list);
		return pm;
	}

	@Override
	public void cancelShare(Integer id) {
		Share share = new Share();
		share.setId(id);
		share.setDelFlag(1);
		shareDao.updateByPrimaryKeySelective(share);
	}

	@Override
	public RespBase<?> rendering(String code, String passwd) {
		RespBase<Object> resp = RespBase.create();

		// for external
		Share share = shareDao.selectByShareCode(code);
		if (nonNull(share)) {
			if (System.currentTimeMillis() >= share.getExpireTime().getTime()) {
				resp.setCode(NOT_FOUND_ERR);
				return resp;
			}
			if (nonNull(share.getShareType()) && share.getShareType() == 1 && !equalsIgnoreCase(share.getPasswd(), passwd)) {
				resp.setCode(UNAUTHC);
				return resp;
			}
			FileChanges lastByFileCode = docService.getLastByDocCode(share.getDocCode());
			resp.setData(parse(lastByFileCode.getContent()));
			return resp;
		}

		// for manager
		FileChanges lastByFileCode = docService.getLastByDocCode(code);
		if (nonNull(lastByFileCode)) {
			resp.setData(parse(lastByFileCode.getContent()));
			return resp;
		} else {
			resp.setCode(NOT_FOUND_ERR);
			return resp;
		}

	}

	/**
	 * parse base url
	 *
	 * @param content
	 * @return
	 */
	private String parse(String content) {
		content = content.replaceAll(DocDevOpsConstants.SHARE_LINK_BASEURI, docProperties.getShareBaseUrl());
		content = content.replaceAll(DocDevOpsConstants.SHARE_LINK_BASEURI_TRAN, docProperties.getShareBaseUrl());
		content = content.replaceAll(DocDevOpsConstants.DOC_LINK_BASEURI, docProperties.getDocBaseUrl());
		content = content.replaceAll(DocDevOpsConstants.DOC_LINK_BASEURI_TRAN, docProperties.getDocBaseUrl());
		return content;
	}

}
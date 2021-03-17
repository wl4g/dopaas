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
package com.wl4g.dopaas.udm.service.impl;

import static com.wl4g.component.common.web.rest.RespBase.RetCode.NOT_FOUND_ERR;
import static com.wl4g.component.common.web.rest.RespBase.RetCode.UNAUTHC;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wl4g.component.common.web.rest.RespBase;
import com.wl4g.component.core.page.PageHolder;
import com.wl4g.dopaas.common.bean.udm.FileChanges;
import com.wl4g.dopaas.common.bean.udm.Share;
import com.wl4g.dopaas.common.constant.UdmConstants;
import com.wl4g.dopaas.udm.config.DocProperties;
import com.wl4g.dopaas.udm.data.ShareDao;
import com.wl4g.dopaas.udm.service.DocService;
import com.wl4g.dopaas.udm.service.ShareService;

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
	public PageHolder<Share> list(PageHolder<Share> pm) {
		pm.useCount().bindPage();
		List<Share> list = shareDao.list();
		for (Share share : list) {
			FileChanges fileChanges = docService.getLastByDocCode(share.getDocCode());
			share.setName(fileChanges.getName());
		}
		pm.setRecords(list);
		return pm;
	}

	@Override
	public void cancelShare(Long id) {
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
		content = content.replaceAll(UdmConstants.SHARE_LINK_BASEURI, docProperties.getShareBaseUrl());
		content = content.replaceAll(UdmConstants.SHARE_LINK_BASEURI_TRAN, docProperties.getShareBaseUrl());
		content = content.replaceAll(UdmConstants.DOC_LINK_BASEURI, docProperties.getDocBaseUrl());
		content = content.replaceAll(UdmConstants.DOC_LINK_BASEURI_TRAN, docProperties.getDocBaseUrl());
		return content;
	}

}
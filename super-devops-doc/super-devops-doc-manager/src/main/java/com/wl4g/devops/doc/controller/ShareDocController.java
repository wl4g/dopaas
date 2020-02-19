package com.wl4g.devops.doc.controller;

import com.wl4g.devops.common.bean.doc.FileChanges;
import com.wl4g.devops.common.constants.DocDevOpsConstants;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.doc.config.DocProperties;
import com.wl4g.devops.doc.service.FileService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.wl4g.devops.common.web.RespBase.RetCode.NOT_FOUND_ERR;
import static com.wl4g.devops.common.web.RespBase.RetCode.UNAUTHC;
import static com.wl4g.devops.tool.common.log.SmartLoggerFactory.getLogger;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

@RestController
@RequestMapping("/doc")
public class ShareDocController {

	final protected Logger log = getLogger(getClass());

	@Autowired
	private FileService fileService;

	@Autowired
	private DocProperties docProperties;

	@CrossOrigin
	@RequestMapping(value = "/rendering")
	public RespBase<?> rendering(String code, String passwd) {
		log.info("rendering file code={} passwd={}", code, passwd);
		RespBase<Object> resp = RespBase.create();
		FileChanges lastByFileCode = fileService.getLastByFileCode(code);

		if (isNull(lastByFileCode.getShareType()) || lastByFileCode.getShareType() == -1) {// not share yet
			resp.setCode(NOT_FOUND_ERR);
			return resp;
		} else if (lastByFileCode.getShareType() == 1 && !equalsIgnoreCase(lastByFileCode.getPasswd(), passwd)) {
			resp.setCode(UNAUTHC);
			return resp;
		}
		resp.setData(parse(lastByFileCode.getContent()));
		return resp;
	}

	private String parse(String content){
		content = content.replaceAll(DocDevOpsConstants.SHARE_BASE_URL,docProperties.getShareBaseUrl());
		content = content.replaceAll(DocDevOpsConstants.SHARE_BASE_URL_TRAN,docProperties.getShareBaseUrl());
		content = content.replaceAll(DocDevOpsConstants.DOC_BASE_URL,docProperties.getDocBaseUrl());
		content = content.replaceAll(DocDevOpsConstants.DOC_BASE_URL_TRAN,docProperties.getDocBaseUrl());
		return content;
	}

}

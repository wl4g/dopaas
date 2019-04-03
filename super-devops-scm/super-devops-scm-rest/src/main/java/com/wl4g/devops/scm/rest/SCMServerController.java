package com.wl4g.devops.scm.rest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.devops.common.bean.scm.model.GetReleaseModel;
import com.wl4g.devops.common.bean.scm.model.PreReleaseModel;
import com.wl4g.devops.common.bean.scm.model.ReleaseModel;
import com.wl4g.devops.common.bean.scm.model.ReportModel;
import com.wl4g.devops.common.constants.SCMDevOpsConstants;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;
import com.wl4g.devops.common.web.BaseController;
import com.wl4g.devops.scm.service.ConfigServerService;

@RestController
@RequestMapping(SCMDevOpsConstants.URI_S_BASE)
public class SCMServerController extends BaseController {

	@Autowired
	private ConfigServerService configServerService;

	@GetMapping(value = SCMDevOpsConstants.URI_S_SOURCE_GET)
	public RespBase<ReleaseModel> getSourceConfig(@Validated GetReleaseModel req, BindingResult bind) {
		if (log.isInfoEnabled()) {
			log.info("Get config-source request: {}, bind: {}", req, bind);
		}

		RespBase<ReleaseModel> resp = new RespBase<>();
		try {
			if (bind.hasErrors()) {
				resp.setCode(RetCode.PARAM_ERR);
				resp.setMessage(bind.toString());
			} else {
				// Get adminServer source configuration.
				ReleaseModel release = this.configServerService.findSource(req);
				resp.getData().put(SCMDevOpsConstants.KEY_RELEASE, release);
			}
		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			resp.setMessage(ExceptionUtils.getRootCauseMessage(e));
			log.error("Get config-source failed.", e);
		}

		if (log.isInfoEnabled()) {
			log.info("Get config-source response: {}", resp);
		}
		return resp;
	}

	@PostMapping(value = SCMDevOpsConstants.URI_S_REPORT_POST)
	public RespBase<?> report(@Validated @RequestBody ReportModel req, BindingResult bind) {
		if (log.isInfoEnabled()) {
			log.info("Report request: {}, bind: {}", req, bind);
		}

		RespBase<?> resp = new RespBase<>();
		try {
			if (bind.hasErrors()) {
				resp.setCode(RetCode.PARAM_ERR);
				resp.setMessage(bind.toString());
			} else {
				// Post to adminServer report-message.
				this.configServerService.report(req);
			}
		} catch (Exception e) {
			resp.setCode(RetCode.SYS_ERR);
			resp.setMessage(ExceptionUtils.getRootCauseMessage(e));
			log.error("Report persistence failed.", e);
		}

		if (log.isDebugEnabled()) {
			log.debug("Report response: {}", resp);
		}
		return resp;
	}

	/* for test */ // @PostMapping(value = DevOpsConstants.URL_CONF_RELEASE)
	public RespBase<?> release(@Validated @RequestBody PreReleaseModel req, BindingResult bind) {
		if (log.isInfoEnabled()) {
			log.info("Releasing... {}, bind: {}", req, bind);
		}

		RespBase<?> resp = new RespBase<>();
		if (bind.hasErrors()) {
			resp.setCode(RetCode.PARAM_ERR);
			resp.setMessage(bind.toString());
		} else {
			// Invoke release.
			this.configServerService.release(req);
		}

		return resp;
	}

}

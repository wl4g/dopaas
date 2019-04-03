package com.wl4g.devops.scm.client.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.MapPropertySource;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wl4g.devops.common.bean.scm.model.BaseModel.ReleaseMeta;
import com.wl4g.devops.common.bean.scm.model.ReleaseModel.ReleasePropertySource;
import com.wl4g.devops.common.constants.SCMDevOpsConstants;
import com.wl4g.devops.common.web.RespBase;
import com.wl4g.devops.common.web.RespBase.RetCode;
import com.wl4g.devops.scm.client.annotation.ScmController;
import com.wl4g.devops.scm.client.configure.refresh.AbstractBeanRefresher;

/**
 * https://blog.csdn.net/cml_blog/article/details/78411312
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年10月9日
 * @since
 */
@ScmController
@ResponseBody
@RequestMapping
public class SCMClientController {
	private Logger log = LoggerFactory.getLogger(getClass());

	private AbstractBeanRefresher refresher;

	public SCMClientController(AbstractBeanRefresher refresher) {
		super();
		this.refresher = refresher;
	}

	@PostMapping(value = SCMDevOpsConstants.URI_C_REFRESH)
	public RespBase<?> refresh(@RequestParam("releaseMeta") ReleaseMeta releaseMeta, BindingResult bind) {
		if (log.isInfoEnabled()) {
			log.info("Post configure refresh ...");
		}

		RespBase<?> resp = new RespBase<>();
		if (bind.hasErrors()) {
			resp.setCode(RetCode.PARAM_ERR);
			resp.setMessage(bind.toString());
		} else {
			try {
				// Do refresh.
				this.refresher.refresh(releaseMeta);
			} catch (Exception e) {
				String errmsg = ExceptionUtils.getRootCauseMessage(e);
				resp.setCode(RetCode.SYS_ERR);
				resp.setMessage(errmsg);
				log.error("Invoke refresh failure. {}", errmsg);
			}
		}
		return resp;
	}

	@GetMapping(value = SCMDevOpsConstants.URI_C_LATEST)
	public RespBase<?> latest() {
		if (log.isInfoEnabled()) {
			log.info("Get configure latest ...");
		}

		RespBase<List<ReleasePropertySource>> resp = new RespBase<>();
		try {
			// Get current environment devops property source.
			resp.getData().put(SCMDevOpsConstants.KEY_ENV_SOURCES, this.getEnvironmentPropertySources());
		} catch (Exception e) {
			String errmsg = ExceptionUtils.getRootCauseMessage(e);
			resp.setCode(RetCode.SYS_ERR);
			resp.setMessage(errmsg);
			log.error("Get latest configuration failure. {}", errmsg);
		}

		return resp;
	}

	private List<ReleasePropertySource> getEnvironmentPropertySources() {
		List<ReleasePropertySource> sources = new ArrayList<>();
		this.refresher.getDevOpsConfigurablePropertySource().getPropertySources()
				.forEach(source -> sources.add(ReleasePropertySource.build((MapPropertySource) source)));
		return sources;
	}

}

/*
 * Copyright 2015 the original author or authors.
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
package com.wl4g.devops.scm.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.wl4g.devops.common.bean.scm.ConfigSourceBean;
import com.wl4g.devops.common.bean.scm.VersionContentBean.FileType;
import com.wl4g.devops.common.bean.scm.model.GetReleaseModel;
import com.wl4g.devops.common.bean.scm.model.PreReleaseModel;
import com.wl4g.devops.common.bean.scm.model.ReleaseModel;
import com.wl4g.devops.common.bean.scm.model.ReportModel;
import com.wl4g.devops.common.bean.scm.model.BaseModel.ReleaseInstance;
import com.wl4g.devops.common.bean.scm.model.ReleaseModel.ReleasePropertySource;
import com.wl4g.devops.common.utils.PropertySources;
import com.wl4g.devops.common.utils.PropertySources.Type;
import com.wl4g.devops.scm.release.ConfigSourceReleaser;
import com.wl4g.devops.scm.service.ConfigurationService;
import com.wl4g.devops.scm.service.ConfigServerService;

/**
 * Configuration server Service implements.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月1日
 * @since
 */
@Service
public class ConfigServerServiceImpl implements ConfigServerService {
	final private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private ConfigSourceReleaser releaser;
	@Autowired
	private ConfigurationService configService;

	@Override
	public ReleaseModel findSource(GetReleaseModel get) {
		/*
		 * When the client initializes, it sends out the requested version
		 * information, at which time releaseMeta # version / releaseMeta #
		 * releaseId will be empty
		 */
		get.validation(false, false);
		ReleaseModel release = new ReleaseModel(get.getApplication(), get.getProfile(), get.getInstance());

		ConfigSourceBean config = this.configService.findSource(get);
		if (config != null) {
			// Set release meta information.
			release.setReleaseMeta(config.getReleaseMeta());

			if (config.getContents() != null) {
				config.getContents().forEach(c -> {
					// Full filename.
					String fileType = FileType.of(c.getType()).name().toLowerCase();
					String fullFileName = c.getFilename() + "." + fileType;
					// Resolve file content.
					Map<String, Object> source = PropertySources.resolve(Type.of(fileType), c.getContent());
					release.getPropertySources().add(new ReleasePropertySource(fullFileName, source));
				});
			}
		}

		return release;
	}

	@Override
	public void report(ReportModel report) {
		report.validation(true, true);
		this.configService.updateReleaseDetail(report);
	}

	@Override
	public void release(PreReleaseModel preRelease) {
		if (log.isTraceEnabled()) {
			log.trace("Release configuration. {}", preRelease);
		}

		this.releaser.release(preRelease);
	}

	/* for test */ ReleaseModel getReleaseMessage(String application, String profile, ReleaseInstance instance) {
		ReleaseModel release = new ReleaseModel(application, profile, instance);

		StringBuffer content = new StringBuffer();
		try {
			Files.readLines(new File("f://test.yml"), Charsets.UTF_8).forEach(s -> content.append(s).append("\r\n"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(content);

		Map<String, Object> source = PropertySources.resolve(Type.YAML, content.toString());
		release.getPropertySources().add(new ReleasePropertySource("application-test.yml", source));

		return release;
	}

}
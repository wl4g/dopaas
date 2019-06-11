/*
 * Copyright 2017 ~ 2025 the original author or authors.
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
package com.wl4g.devops.scm;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.wl4g.devops.common.bean.scm.ConfigSourceBean;
import com.wl4g.devops.common.bean.scm.VersionContentBean.FileType;
import com.wl4g.devops.common.bean.scm.model.*;
import com.wl4g.devops.common.bean.scm.model.GenericInfo.ReleaseInstance;
import com.wl4g.devops.common.bean.scm.model.GenericInfo.ReleaseMeta;
import com.wl4g.devops.common.bean.scm.model.ReleaseMessage.ReleasePropertySource;
import com.wl4g.devops.common.utils.PropertySources;
import com.wl4g.devops.common.utils.PropertySources.Type;
import com.wl4g.devops.scm.context.ConfigContextHandler;
import com.wl4g.devops.scm.publish.ConfigSourcePublisher;
import com.wl4g.devops.scm.publish.WatchDeferredResult;
import com.wl4g.devops.scm.service.ConfigurationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

/**
 * Configuration servers implements.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月1日
 * @since
 */
public class StandardConfigContextHandler implements ConfigContextHandler {
	final private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private ConfigSourcePublisher publisher;

	@Autowired
	private ConfigurationService configService;

	@Override
	public ReleaseMessage getSource(GetRelease get) {
		/*
		 * When the client initializes, it sends out the requested version
		 * information, at which time releaseMeta # version / releaseMeta #
		 * releaseId will be empty
		 */
		get.validation(false, false);
		ReleaseMessage release = new ReleaseMessage(get.getGroup(), get.getProfile(), null, get.getInstance());

		ConfigSourceBean config = this.configService.findSource(get);
		if (config != null) {
			// Set release meta information.
			release.setMeta(config.getReleaseMeta());

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
	public void report(ReportInfo report) {
		report.validation(true, true);
		this.configService.updateReleaseDetail(report);
	}

	@Override
	public void release(PreRelease pre) {
		if (log.isInfoEnabled()) {
			log.info("Pre release for {}", pre);
		}

		this.publisher.publish(pre);
	}

	@Override
	public WatchDeferredResult<ResponseEntity<?>> watch(GetRelease watch) {
		return publisher.watch(watch);
	}

	//
	// for test
	//

	public ReleaseMessage getReleaseMessage(String group, String namespace, ReleaseMeta meta, ReleaseInstance instance) {
		ReleaseMessage release = new ReleaseMessage(group, namespace, meta, instance);

		StringBuffer content = new StringBuffer();
		try {
			Files.readLines(new File("f://test.yml"), Charsets.UTF_8).forEach(s -> content.append(s).append("\r\n"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(content);

		Map<String, Object> source = PropertySources.resolve(Type.YAML, content.toString());
		release.getPropertySources().add(new ReleasePropertySource(namespace, source));

		return release;
	}

}
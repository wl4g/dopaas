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
package com.wl4g.devops.scm;

import com.wl4g.devops.common.bean.scm.ConfigSourceBean;
import com.wl4g.devops.common.bean.scm.VersionContentBean;
import com.wl4g.devops.common.bean.scm.model.GetRelease;
import com.wl4g.devops.common.bean.scm.model.PreRelease;
import com.wl4g.devops.common.bean.scm.model.ReleaseMessage;
import com.wl4g.devops.common.bean.scm.model.ReleaseMessage.ReleasePropertySource;
import com.wl4g.devops.common.bean.scm.model.ReportInfo;
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
import org.springframework.util.Assert;

import java.util.Map;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.contains;

/**
 * Configuration servers implements.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0
 * @date 2018年11月1日
 * @since
 */
public class StandardConfigContextHandler implements ConfigContextHandler {

	final protected Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private ConfigSourcePublisher publisher;

	@Autowired
	private ConfigurationService configService;

	@Override
	public WatchDeferredResult<ResponseEntity<?>> watch(GetRelease watch) {
		return publisher.watch(watch);
	}

	@Override
	public void release(PreRelease pre) {
		publisher.publish(pre);
	}

	@Override
	public ReleaseMessage getSource(GetRelease get) {
		/*
		 * When the client initializes, it sends out the requested version
		 * information, at which time releaseMeta # version / releaseMeta #
		 * releaseId will be empty
		 */
		get.validation(false, false);
		ReleaseMessage release = new ReleaseMessage(get.getCluster(), get.getNamespaces(), get.getMeta(), get.getInstance());

		ConfigSourceBean config = configService.findSource(get);
		if (config != null) {
			// Sets release meta information.
			release.setMeta(config.getReleaseMeta());
			if (nonNull(config.getContents())) {
				config.getContents().forEach(vc -> release.getPropertySources().add(convertReleasePropertySource(vc)));
			}
		}

		return release;
	}

	@Override
	public void report(ReportInfo report) {
		report.validation(true, true);
		configService.updateReleaseDetail(report);
	}

	/**
	 * Resolve to releasePropertySource
	 * 
	 * @param vc
	 * @return
	 */
	private ReleasePropertySource convertReleasePropertySource(VersionContentBean vc) {
		String filename = vc.getNamespace().toLowerCase();
		Assert.state(contains(filename, "."), String.format("Invalid namespace filename for: %s", filename));

		// Resolve file content
		String fileType = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
		Map<String, Object> source = PropertySources.resolve(Type.of(fileType), vc.getContent());
		return new ReleasePropertySource(filename, source);
	}

	/**
	 * 
	 * /// for test
	 * 
	 * @param group
	 * @param namespace
	 * @param meta
	 * @param instance
	 * @return
	 */
	/*
	 * public ReleaseMessage getReleaseMessage(String group, List<String>
	 * namespaces, ReleaseMeta meta, ReleaseInstance instance) { ReleaseMessage
	 * release = new ReleaseMessage(group, namespaces, meta, instance);
	 * 
	 * StringBuffer content = new StringBuffer(); try { Files.readLines(new
	 * File("f://test.yml"), Charsets.UTF_8).forEach(s ->
	 * content.append(s).append("\r\n")); } catch (IOException e) {
	 * e.printStackTrace(); } System.out.println(content);
	 * 
	 * Map<String, Object> source = PropertySources.resolve(Type.YAML,
	 * content.toString()); release.getPropertySources().add(new
	 * ReleasePropertySource(namespace, source));
	 * 
	 * return release; }
	 */

}
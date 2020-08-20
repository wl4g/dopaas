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
package com.wl4g.devops.scm.publish;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.wl4g.devops.scm.common.command.FetchConfigRequest;
import com.wl4g.devops.scm.common.command.ReleaseConfigInfo;

/**
 * SCM config source server publisher api.
 * 
 * @author Wangl.sir <983708408@qq.com>
 * @version v1.0 2019年5月27日
 * @since
 */
public interface ConfigSourcePublisher {

	/**
	 * Real-time publishing config source.
	 * 
	 * @param result
	 * @return
	 */
	List<WatchDeferredResult<ResponseEntity<?>>> publish(ReleaseConfigInfo result);

	/**
	 * Used for hang live client listening configuration.
	 * 
	 * @param watch
	 * @return
	 */
	WatchDeferredResult<ResponseEntity<?>> watch(FetchConfigRequest watch);

}
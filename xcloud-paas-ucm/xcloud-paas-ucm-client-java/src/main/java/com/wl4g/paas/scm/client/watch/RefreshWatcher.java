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
package com.wl4g.paas.scm.client.watch;

import java.io.Closeable;
import java.util.Collection;

import com.wl4g.paas.scm.common.model.ReportChangedRequest.ChangedRecord;

/**
 * {@link RefreshWatcher}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-20
 * @since
 */
public interface RefreshWatcher extends Closeable {

	/**
	 * Start SCM server watching for configuration source changed .
	 * 
	 * @throws Exception
	 */
	void start() throws Exception;

	/**
	 * DO reporting changed records
	 * 
	 * @param records
	 * @return
	 */
	boolean doReporting(Collection<ChangedRecord> records);

}
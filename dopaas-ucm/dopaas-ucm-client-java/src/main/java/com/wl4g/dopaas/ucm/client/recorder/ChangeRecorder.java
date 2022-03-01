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
package com.wl4g.dopaas.ucm.client.recorder;

import java.util.Collection;
import java.util.Set;

import com.wl4g.dopaas.common.bean.ucm.model.ReportChangedRequest.ChangedRecord;

/**
 * {@link ChangeRecorder}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2020-08-20
 * @since
 */
public interface ChangeRecorder {

    /**
     * Save released configuration source.
     * 
     * @param wrapper
     */
    void save(ReleaseConfigSourceWrapper wrapper);

    /**
     * Gets last refresh configuration property source.
     * 
     * @return
     */
    ReleaseConfigSourceWrapper lastRelease();

    /**
     * Gets refresh configuration source currently in use.
     * 
     * @return
     */
    ReleaseConfigSourceWrapper currentRelease();

    // --- Changed records .---

    /**
     * Poll chanaged keys all.
     * 
     * @return
     */
    Collection<ChangedRecord> pollAll();

    /**
     * Gets changed keys all
     * 
     * @return
     */
    Collection<ChangedRecord> getChangedAll();

    /**
     * Addition changed keys.
     * 
     * @param changedKeys
     * @param wrapper
     */
    void save(Set<String> changedKeys, ReleaseConfigSourceWrapper wrapper);

}
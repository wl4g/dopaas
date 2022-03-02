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
package com.wl4g.dopaas.common.bean.ucm.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.wl4g.dopaas.common.bean.ucm.model.BaseConfigInfo.ConfigInstance;
import com.wl4g.dopaas.common.bean.ucm.model.ReleaseConfigInfo.ConfigSource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Wither;

/**
 * {@link ReportChangedRequest}
 *
 * @author Wangl.sir <wanglsir@gmail.com, 983708408@qq.com>
 * @version v1.0 2018-08-18
 * @since
 */
@Getter
@Setter
@Wither
@ToString
@AllArgsConstructor
public class ReportChangedRequest extends ReleaseConfigInfoRequest {
    private static final long serialVersionUID = 2523769504519533902L;

    private Collection<ChangedRecord> changedRecords;

    /**
     * {@link ChangedRecord}
     *
     * @since
     */
    @Getter
    @Setter
    @Wither
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangedRecord extends BaseConfigInfo {
        private static final long serialVersionUID = 1197156266938234001L;

        /** Release configuration changed keys. */
        private Set<String> changedKeys = new HashSet<>();

        /** Release configuration content sources */
        @NotNull
        @NotEmpty
        private List<ConfigSource> sources = new ArrayList<>(1);

        /**
         * {@link ConfigInstance} of current myself SCM client application.
         */
        @NotNull
        private ConfigInstance instance;
    }

}